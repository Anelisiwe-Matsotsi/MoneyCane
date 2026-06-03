package com.example.prog7313_sem1_part2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.LineChart          // (PhilJay, 2015)
import com.github.mikephil.charting.charts.PieChart          // (PhilJay, 2015)
import com.github.mikephil.charting.data.Entry               // (PhilJay, 2015)
import com.github.mikephil.charting.data.LineData            // (PhilJay, 2015)
import com.github.mikephil.charting.data.LineDataSet         // (PhilJay, 2015)
import com.github.mikephil.charting.data.PieData             // (PhilJay, 2015)
import com.github.mikephil.charting.data.PieDataSet          // (PhilJay, 2015)
import com.github.mikephil.charting.data.PieEntry            // (PhilJay, 2015)
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AnalysisLight : Fragment(R.layout.fragment_analysis_light) {

    private lateinit var lineChart: LineChart
    private lateinit var tvMonthName: TextView
    private var allTransactions = listOf<Transaction>()
    private val currentCalendar = Calendar.getInstance()

    private lateinit var pieChartExpenses: PieChart
    private lateinit var pieChartIncomes: PieChart
    private lateinit var tabLayout: TabLayout
    private var allCategories = mapOf<String, Category>()

    private lateinit var tvBudgetSummary: TextView
    private lateinit var pbBudget: ProgressBar
    private lateinit var tvBudgetStatus: TextView
    private var totalBudget = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lineChart = view.findViewById(R.id.line_chart)
        tvMonthName = view.findViewById(R.id.tv_month_name)

        pieChartExpenses = view.findViewById(R.id.pie_chart_expenses)
        pieChartIncomes = view.findViewById(R.id.pie_chart_incomes)
        tabLayout = view.findViewById(R.id.tab_layout)

        tvBudgetSummary = view.findViewById(R.id.tv_budget_summary)
        pbBudget = view.findViewById(R.id.pb_budget)
        tvBudgetStatus = view.findViewById(R.id.tv_budget_status)

        val btnPrev = view.findViewById<ImageButton>(R.id.btn_month_prev)
        val btnNext = view.findViewById<ImageButton>(R.id.btn_month_next)
        val btnFilter = view.findViewById<ImageView>(R.id.iv_filter)
        val btnBack = view.findViewById<ImageButton>(R.id.iv_back_arrow)

        btnPrev.setOnClickListener {
            currentCalendar.add(Calendar.MONTH, -1)
            updateMonthDisplay()
            applyFilterAndDraw()
        }

        btnNext.setOnClickListener {
            currentCalendar.add(Calendar.MONTH, 1)
            updateMonthDisplay()
            applyFilterAndDraw()
        }

        btnFilter.setOnClickListener {
            showMonthYearPicker()
        }

        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // (clyeung0714, 2026) – TabLayout toggles between expense and income pie charts
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        pieChartExpenses.visibility = View.VISIBLE
                        pieChartIncomes.visibility = View.GONE
                    }
                    1 -> {
                        pieChartExpenses.visibility = View.GONE
                        pieChartIncomes.visibility = View.VISIBLE
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val transactionsRef = FirebaseDatabase.getInstance()
            .getReference("transactions")
            .child(uid)

        val categoriesRef = FirebaseDatabase.getInstance().getReference("categories").child(uid)

        //Firebase. 2026. how to read and write to firebase with snapshots
        transactionsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val transactionList = mutableListOf<Transaction>()
                for (transactionSnapshot in snapshot.children) {
                    transactionSnapshot.getValue(Transaction::class.java)?.let {
                        transactionList.add(it)
                    }
                }
                if (transactionList.isEmpty()) {
                    Toast.makeText(context, "No transactions found", Toast.LENGTH_SHORT).show()
                    return
                }
                allTransactions = transactionList
                setInitialMonthYearFromTransactions()
                loadBudgets()
                updateMonthDisplay()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        categoriesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val map = mutableMapOf<String, Category>()
                for (catSnap in snapshot.children) {
                    catSnap.getValue(Category::class.java)?.let {
                        map[catSnap.key ?: ""] = it
                    }
                }
                allCategories = map
            }
            override fun onCancelled(error: DatabaseError) { }
        })
    }

    private fun setInitialMonthYearFromTransactions() {
        if (allTransactions.isEmpty()) return
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val latestDate = allTransactions.maxByOrNull { dateFormat.parse(it.date) }?.date
        if (latestDate != null) {
            val parts = latestDate.split("-")
            if (parts.size == 3) {
                val year = parts[0].toInt()
                val month = parts[1].toInt() - 1
                currentCalendar.set(year, month, 1)
            }
        }
    }

    private fun updateMonthDisplay() {
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        tvMonthName.text = monthFormat.format(currentCalendar.time)
    }

    private fun applyFilterAndDraw() {
        if (allTransactions.isEmpty()) return

        val selectedYear = currentCalendar.get(Calendar.YEAR)
        val selectedMonth = currentCalendar.get(Calendar.MONTH)

        val filtered = allTransactions.filter { tx ->
            val parts = tx.date.split("-")
            parts.size >= 3 && parts[0].toInt() == selectedYear && (parts[1].toInt() - 1) == selectedMonth
        }

        if (filtered.isEmpty()) {
            Toast.makeText(context, "No transactions for ${SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentCalendar.time)}", Toast.LENGTH_SHORT).show()
            lineChart.clear()
            updateSummary(0.0, 0.0, 0.0)
            tvBudgetSummary.text = "R0.00 spent of R0.00"
            pbBudget.progress = 0
            tvBudgetStatus.text = "No transactions"
            return
        }

        var totalIncome = 0.0
        var totalExpense = 0.0
        for (tx in filtered) {
            if (tx.type == "income") totalIncome += tx.amount
            else if (tx.type == "expense") totalExpense += tx.amount
        }
        val netTotal = totalIncome - totalExpense
        updateSummary(netTotal, totalIncome, totalExpense)

        val totalSpent = totalExpense
        val percent = if (totalBudget > 0) (totalSpent / totalBudget) * 100 else 0f
        tvBudgetSummary.text = "R${"%.2f".format(totalSpent)} spent of R${"%.2f".format(totalBudget)}"
        pbBudget.progress = percent.toInt()
        tvBudgetStatus.text = if (totalBudget > 0) "${percent.toInt()}% of budget used" else "No budget set"

        drawCumulativeNetChart(lineChart, filtered)
        updatePieCharts(filtered)
    }

    private fun updateSummary(netTotal: Double, totalIncome: Double, totalExpense: Double) {
        val tvNetTotal = view?.findViewById<TextView>(R.id.tv_net_total)
        val tvIncome = view?.findViewById<TextView>(R.id.tv_income_amount)
        val tvExpense = view?.findViewById<TextView>(R.id.tv_expenses_amount)

        val netString = if (netTotal >= 0) "+R${"%.2f".format(netTotal)}" else "-R${"%.2f".format(-netTotal)}"
        tvNetTotal?.text = netString
        tvIncome?.text = "R${"%.2f".format(totalIncome)}"
        tvExpense?.text = "R${"%.2f".format(totalExpense)}"

        if (netTotal >= 0) {
            tvNetTotal?.setTextColor(android.graphics.Color.parseColor("#A3C2A3"))
        } else {
            tvNetTotal?.setTextColor(android.graphics.Color.parseColor("#E57373"))
        }
    }

    // ------------------------------------------------------------------
    // Line Chart – cumulative net total (MPAndroidChart)
    // ------------------------------------------------------------------
    private fun drawCumulativeNetChart(chart: LineChart, transactions: List<Transaction>) {
        // (PhilJay, 2015) – using MPAndroidChart LineChart

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sorted = transactions.sortedBy { dateFormat.parse(it.date) }

        val dailyNet = mutableMapOf<String, Double>()
        for (tx in sorted) {
            val netChange = if (tx.type == "income") tx.amount else -tx.amount
            dailyNet[tx.date] = dailyNet.getOrDefault(tx.date, 0.0) + netChange
        }

        val uniqueDates = dailyNet.keys.sortedBy { dateFormat.parse(it) }
        val dailyNetValues = mutableListOf<Double>()
        val cumulativeValues = mutableListOf<Double>()
        var runningTotal = 0.0
        for (date in uniqueDates) {
            val daily = dailyNet[date] ?: 0.0
            dailyNetValues.add(daily)
            runningTotal += daily
            cumulativeValues.add(runningTotal)
        }

        // (leelaprasad4648, 2026) – creating LineDataSet with entries
        val entries = cumulativeValues.mapIndexed { index, value ->
            Entry(index.toFloat(), value.toFloat())
        }

        val dataSet = LineDataSet(entries, "Net Total").apply {
            color = android.graphics.Color.rgb(163, 194, 163)
            lineWidth = 2f
            setDrawCircles(true)
            circleRadius = 4f
            setDrawValues(true)
            valueTextSize = 12f
            setDrawFilled(true)
            fillColor = android.graphics.Color.rgb(163, 194, 163)
            fillAlpha = 50

            // (Vishal Kamboj, 2025) – custom ValueFormatter to show daily net instead of cumulative
            valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getFormattedValue(value: Float, entry: Entry, dataSetIndex: Int, viewPortHandler: com.github.mikephil.charting.utils.ViewPortHandler?): String {
                    val index = entry.x.toInt()
                    if (index in dailyNetValues.indices) {
                        val daily = dailyNetValues[index]
                        val sign = if (daily >= 0) "+" else ""
                        return String.format("R%s%.2f", sign, daily)
                    }
                    return "R0.00"
                }
            }
        }

        chart.data = LineData(dataSet)
        chart.description.isEnabled = false
        chart.axisLeft.setDrawGridLines(true)
        chart.xAxis.setDrawGridLines(true)
        chart.axisRight.isEnabled = false
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)

        // (SeanAT19, 2026) – Y‑axis formatter adds “R” prefix
        chart.axisLeft.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "R${value.toInt()}"
            }
        }

        // (SeanAT19, 2026) – X‑axis shows actual dates
        val xAxis = chart.xAxis
        xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index in uniqueDates.indices) uniqueDates[index] else ""
            }
        }
        xAxis.setLabelCount(uniqueDates.size, true)
        xAxis.labelRotationAngle = -45f
        xAxis.setPosition(com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM)
        xAxis.granularity = 1f
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = (uniqueDates.size - 1).toFloat()

        chart.invalidate()
    }

    private fun showMonthYearPicker() {
        val year = currentCalendar.get(Calendar.YEAR)
        val month = currentCalendar.get(Calendar.MONTH)
        android.app.DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, _ ->
            currentCalendar.set(selectedYear, selectedMonth, 1)
            updateMonthDisplay()
            applyFilterAndDraw()
        }, year, month, android.app.DatePickerDialog.THEME_HOLO_LIGHT).show()
    }

    // ------------------------------------------------------------------
    // Pie Charts – expenses and incomes by category
    // ------------------------------------------------------------------
    private fun updatePieCharts(transactions: List<Transaction>) {
        // (clyeung0714, 2026) – build PieEntry lists per category
        val expenseTotals = mutableMapOf<String, Double>()
        val incomeTotals = mutableMapOf<String, Double>()

        for (tx in transactions) {
            val catId = tx.categoryId
            val amount = tx.amount
            val category = allCategories[catId]
            if (category == null) continue

            if (category.type == "expense") {
                expenseTotals[catId] = expenseTotals.getOrDefault(catId, 0.0) + amount
            } else if (category.type == "income") {
                incomeTotals[catId] = incomeTotals.getOrDefault(catId, 0.0) + amount
            }
        }

        val expenseEntries = expenseTotals.map { (catId, total) ->
            PieEntry(total.toFloat(), allCategories[catId]?.name ?: "Unknown")
        }
        val incomeEntries = incomeTotals.map { (catId, total) ->
            PieEntry(total.toFloat(), allCategories[catId]?.name ?: "Unknown")
        }

        setupPieChart(pieChartExpenses, expenseEntries)
        setupPieChart(pieChartIncomes, incomeEntries)
    }

    // (malcolmmaima, 2026) – configures PieChart with custom colours and hides description
    private fun setupPieChart(pieChart: PieChart, entries: List<PieEntry>) {
        if (entries.isEmpty()) {
            pieChart.clear()
            pieChart.setNoDataText("No data")
            pieChart.invalidate()
            return
        }

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                android.graphics.Color.rgb(163, 194, 163),
                android.graphics.Color.rgb(100, 150, 100),
                android.graphics.Color.rgb(200, 220, 180),
            )
            valueTextSize = 12f
            setDrawValues(true)
        }

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.description.isEnabled = false
        pieChart.setUsePercentValues(true)
        pieChart.setDrawHoleEnabled(true)
        pieChart.invalidate()
    }

    // ------------------------------------------------------------------
    // Budget loading from Firebase
    // ------------------------------------------------------------------
    private fun loadBudgets() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val budgetsRef = FirebaseDatabase.getInstance().getReference("user_budgets").child(uid)

        budgetsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                totalBudget = 0.0
                for (budgetSnap in snapshot.children) {
                    val budget = budgetSnap.getValue(Budget::class.java)
                    if (budget != null) {
                        totalBudget += budget.budget
                    }
                }
                applyFilterAndDraw()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load budgets", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
/*



PhilJay (2015) – MPAndroidChart library GitHub repository. The library was used to draw all line charts and pie charts.URL: https://github.com/PhilJay/MPAndroidChart
SeanAT19 (2026) – Medium article: “How to use MPAndroidChart in Android Studio!”. Helped understand setup, basic line chart, and customisation.URL: https://medium.com/@SeanAT19/how-to-use-mpandroidchart-in-android-studio-c01a8150720f
leelaprasad4648 (2026) – Medium article: “Creating LineChart using MpAndroidChart”. Assisted with LineDataSet configuration and axis formatting.URL: https://medium.com/@leelaprasad4648/creating-linechart-using-mpandroidchart-33632324886d
Vishal Kamboj (2025) – YouTube video: “MPAndroidLineChart JetPack Compose” (used concepts for ValueFormatter and real‑time data updates).URL: https://www.youtube.com/watch?v=dsjYA-jK_b4
clyeung0714 (2026) – Medium article: “Using MPAndroidChart for Android Application — PieChart”. Provided guidance on PieDataSet, PieEntry, and displaying percentage values.URL: https://medium.com/@clyeung0714/using-mpandroidchart-for-android-application-piechart-123d62d4ddc0
malcolmmaima (2026) – Medium article: “(Kotlin) Implementing a Barchart and Piechart using MPAndroidChart”. Helped adapt pie chart code to Kotlin and apply custom colours.URL: https://malcolmmaima.medium.com/kotlin-implementing-a-barchart-and-piechart-using-mpandroidchart-8c7643c4ba75
Firebase. 2026. Read and Write Data on Android. (version 8) [Source Code]. Available at: <https://firebase.google.com/docs/database/android/read-and-write?authuser=1> [Accessed 1 June 2026]






*/

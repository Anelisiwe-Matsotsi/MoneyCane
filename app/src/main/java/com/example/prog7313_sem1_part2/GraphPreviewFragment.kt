package com.example.prog7313_sem1_part2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GraphPreviewFragment : Fragment(R.layout.fragment_graph_preview) {

    private lateinit var lineChart: LineChart

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lineChart = view.findViewById(R.id.line_chart_preview)
        val btnFullAnalysis = view.findViewById<Button>(R.id.btn_view_full_analysis)

        btnFullAnalysis.setOnClickListener {
            // Navigate to the analysis fragment (ensure action ID exists)
            findNavController().navigate(R.id.action_home_to_analysisLight)
        }

        loadTransactionData()
    }

    private fun loadTransactionData() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            lineChart.setNoDataText("Login to view chart")
            return
        }

        val transactionsRef = FirebaseDatabase.getInstance()
            .getReference("transactions")
            .child(uid)

        transactionsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val transactionList = mutableListOf<Transaction>()
                for (txSnap in snapshot.children) {
                    txSnap.getValue(Transaction::class.java)?.let { transactionList.add(it) }
                }
                if (transactionList.isEmpty()) {
                    lineChart.setNoDataText("No transactions yet")
                    return
                }
                val filtered = filterLast30Days(transactionList)
                drawPreviewChart(filtered)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterLast30Days(transactions: List<Transaction>): List<Transaction> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        val cutoffDate = calendar.time

        return transactions.filter {
            try {
                val txDate = dateFormat.parse(it.date)
                txDate != null && txDate.after(cutoffDate)
            } catch (e: Exception) {
                false
            }
        }
    }

    private fun drawPreviewChart(transactions: List<Transaction>) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sorted = transactions.sortedBy { dateFormat.parse(it.date) }

        val dailyNet = mutableMapOf<String, Double>()
        for (tx in sorted) {
            val netChange = if (tx.type == "income") tx.amount else -tx.amount
            dailyNet[tx.date] = dailyNet.getOrDefault(tx.date, 0.0) + netChange
        }

        val uniqueDates = dailyNet.keys.sortedBy { dateFormat.parse(it) }
        val cumulativeValues = mutableListOf<Double>()
        var runningTotal = 0.0
        for (date in uniqueDates) {
            runningTotal += dailyNet[date] ?: 0.0
            cumulativeValues.add(runningTotal)
        }

        val entries = cumulativeValues.mapIndexed { index, value ->
            Entry(index.toFloat(), value.toFloat())
        }

        val dataSet = LineDataSet(entries, "Net Total").apply {
            color = android.graphics.Color.rgb(163, 194, 163)
            lineWidth = 2f
            setDrawCircles(true)
            circleRadius = 3f
            setDrawValues(false)  // no labels for preview
            setDrawFilled(true)
            fillColor = android.graphics.Color.rgb(163, 194, 163)
            fillAlpha = 50
        }

        lineChart.data = LineData(dataSet)
        lineChart.description.isEnabled = false
        lineChart.axisLeft.setDrawGridLines(true)
        lineChart.xAxis.setDrawGridLines(true)
        lineChart.axisRight.isEnabled = false
        lineChart.setTouchEnabled(false)
        lineChart.setScaleEnabled(false)
        lineChart.legend.isEnabled = false
        lineChart.invalidate()
    }
}
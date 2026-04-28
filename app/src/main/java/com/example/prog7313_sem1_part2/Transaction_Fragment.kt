package com.example.prog7313_sem1_part2

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.prog7313_sem1_part2.FileUtils
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar
import kotlin.apply
import kotlin.concurrent.thread
import kotlin.io.readBytes
import kotlin.let




class Transaction_Fragment : Fragment() {

    //Add image code
    private val storageService = SupabaseStorageService()
    private var selectedImageUri: Uri? = null
    private var uploadedImageUrl: String? = null
    private lateinit var imgSelected: ImageView
    //private lateinit var imgDownloaded: ImageView

    // Firebase Realtime Database reference
    private val database = FirebaseDatabase.getInstance().getReference("transactions")
    private lateinit var btnChoose: Button
    private lateinit var btnUpload: Button
    // private lateinit var btnDownload: Button
    private lateinit var txtStatus: TextView


    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            imgSelected.setImageURI(uri)
            txtStatus.text = "Status: Image selected"
            Log.d("ProfileBadge", "Image selected: $uri")
        } else {
            txtStatus.text = "Status: No image selected"
            Log.e("ProfileBadge", "No image selected")
        }
    }


    // TODO: Rename and change types of parameters
    //(Gaurav, 2025)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_transaction_, container, false)}
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // val sharedPref =
        // requireActivity().getSharedPreferences("TransactionPrefs", Context.MODE_PRIVATE)
        // retrieve the user that is currently "logged in" to ensure that only that user's data is saved
        //val currentUser = sharedPref.getString("username", "Guest")
        // val userKey = "entries_$currentUser"
        val  etName= view.findViewById<EditText>(R.id.tbTransactionName)
        val etAmount = view.findViewById<EditText>(R.id.tbAmount)
      //  val etWallet = view.findViewById<EditText>(R.id.tbWallet)
        // Initialize DatePicker from layout
        val datePicker: DatePicker = view.findViewById(R.id.datePicker)
        val etDescription = view.findViewById<EditText>(R.id.tbDescription)
        val radioGroupType = view.findViewById<RadioGroup>(R.id.radioGroupType)
        val radioExpense = view.findViewById<RadioButton>(R.id.radioExpense)
        val radioIncome = view.findViewById<RadioButton>(R.id.radioIncome)

//Image upload code
        imgSelected = view.findViewById(R.id.imgSelected)
        btnChoose = view.findViewById(R.id.btnChooseImage)
        btnUpload = view.findViewById(R.id.btnUploadImage)
        txtStatus = view.findViewById(R.id.txtUploadStatus)

        btnChoose.setOnClickListener {
            Log.d("ProfileBadge", "Choose Image clicked")
            imagePickerLauncher.launch("image/*")
        }

        btnUpload.setOnClickListener {
            Log.d("ProfileBadge", "Upload Image clicked")
            uploadSelectedImage()
        }




        // Get today's date using Calendar instance
        val today = Calendar.getInstance()

        // Initialize DatePicker with the current date
        datePicker.init(
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ) { view, year, month, day ->
            // Display selected date in Toast message
            val msg = "You Selected: $day/${month+1}/$year"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }



        val btnSave = view.findViewById<Button>(R.id.btnSave)
        // val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        // next we are doing the save button. this will save the transaction entry that the logged in
        // user created, this will also be linked to that user with shared preferences

        btnSave.setOnClickListener {
            val name=etName.text.toString().trim()
            val amountText = etAmount.text.toString().trim()
            val content = etDescription.text.toString()
            val imageUrl = uploadedImageUrl ?: ""// if no image is uploaded empty string is returned

            if (content.isEmpty()) {
                etDescription.error = "EnterDescription"
                return@setOnClickListener
            }

            if (amountText.isEmpty()) {
                etAmount.error = "Enter Amount"
                return@setOnClickListener
            }


            val amount = amountText.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                etAmount.error = "Please enter an amount more than R0"
                return@setOnClickListener
            }

            val day=datePicker.dayOfMonth
            val month=datePicker.month + 1
            val year =datePicker.year
          val date= "$day/$month/$year"


            // Determine selected type from the RadioGroup
            //(StackOverflow, 2023)
            val selectedType = when (radioGroupType.checkedRadioButtonId) {
                R.id.radioIncome -> "income"
                else -> "expense"
            }

            saveTransaction(name,amount, selectedType, date, imageUrl)
        }

    }
            //Firebase, 2026)
            //transaction is saved in firebase DB
            private fun saveTransaction(name: String, amount: Double, type: String,
           date: String, imageUrl: String ) {
            // push creates a unique key for the new transaction

            val transactionRef = database.push()
            val transactionId = transactionRef.key ?: return

            val newTransaction = Transaction(
                id = transactionId,
                name = name,
                type = type,
                amount = amount,
                date=date,
                imageUrl=imageUrl
            )

            transactionRef.setValue(newTransaction)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "$name transaction created!", Toast.LENGTH_SHORT).show()
                    // Return to the categories list
                    //(Android Developers, 2026)
                    parentFragmentManager.popBackStack()
                }
                .addOnFailureListener { error ->
                    Toast.makeText(requireContext(), "Failed to save: ${error.message}", Toast.LENGTH_SHORT).show()
                }


            // fetches the user who is logged in and links the entry to it (shared preferences again)
           /* if (date.isNotEmpty() && content.isNotEmpty()) {
                val userData = sharedPref.getString(userKey, "")
                // now we are going to combine the entry's data: the date and whatever you typed
                val newEntry = "$date|$content"
                val updatedData = "$newEntry\n%userData"
                //Okay> the | combines content together
                sharedPref.edit().putString(userKey, updatedData).apply()

                // after creating an entry (successfully) then it will clear the textboxes
                etDate.text.clear()
                etDescription.text.clear()
                Toast.makeText(context, "Entry has been saved", Toast.LENGTH_SHORT).show()

            }*/
        }




    private fun uploadSelectedImage() {
        val uri = selectedImageUri

        if (uri == null) {
            Log.e("ProfileBadge", "Upload attempted with no image selected")
            return
        }

        txtStatus.text = "Status: Uploading..."

        thread {
            try {
                val fileBytes = FileUtils.readBytes(requireContext(), uri)//Ghanem, A. 2024

                val extension = MimeTypeMap.getFileExtensionFromUrl("image.jpg")
                val filePath = "demo_${System.currentTimeMillis()}.$extension"

                Log.d("ProfileBadge", "Uploading file: $filePath")

                val result = storageService.uploadImage(
                    fileBytes,
                    filePath,
                    requireContext().contentResolver.getType(uri) ?: "image/jpeg"
                )
                //Ghanem, A. 2024
                requireActivity().runOnUiThread {
                    result.onSuccess {
                        uploadedImageUrl = it
                        txtStatus.text = "Status: Upload successful"
                        Log.d("ProfileBadge", "Upload success: $it")
                    }.onFailure {
                        txtStatus.text = "Status: Upload failed"
                        Log.e("ProfileBadge", "Upload failed", it)
                    }
                }

            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    txtStatus.text = "Status: Error"
                    Log.e("ProfileBadge", "Exception during upload", e)
                }
            }
        }
    }
}

/*
* Ghanem, A. 2024. What is Context? In Android, represents the current state of the application. It provides access to various resources (e. [online] Linkedin.com. Available at: <https://www.linkedin.com/pulse/context-android-abdelrhman-ghanem-zbpcf/ [Accessed 26 Apr. 2026].

‌> */
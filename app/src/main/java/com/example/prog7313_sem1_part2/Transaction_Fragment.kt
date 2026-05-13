package com.example.prog7313_sem1_part2

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.prog7313_sem1_part2.FileUtils
import com.google.firebase.auth.FirebaseAuth
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar
import kotlin.concurrent.thread
import android.media.AudioAttributes
import  android.media.AudioManager
import  android.media.SoundPool
import android.os.Build

class Transaction_Fragment : Fragment() {

    //Add image code
    private val storageService = SupabaseStorageService()
    private var selectedImageUri: Uri? = null
    private var uploadedImageUrl: String? = null
    private lateinit var imgSelected: ImageView
    //private lateinit var imgDownloaded: ImageView

    // Firebase Realtime Database reference
    private val database = FirebaseDatabase.getInstance().getReference("transactions")
    private val categorygoryDatabase = FirebaseDatabase.getInstance().getReference("categories")
    private val categoryList= ArrayList<Category>()

    private  var selectedCategoryId: String=""
    private lateinit var btnChoose: Button
    private lateinit var btnUpload: Button
    // private lateinit var btnDownload: Button
    private lateinit var txtStatus: TextView

    //soundpool variables

   private  lateinit var  soundPool: SoundPool
    private  var saveSound= 0

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


        val  etName= view.findViewById<EditText>(R.id.tbTransactionName)
        val etAmount = view.findViewById<EditText>(R.id.tbAmount)
      //  val etWallet = view.findViewById<EditText>(R.id.tbWallet)
        // Initialize DatePicker from layout
        val datePicker: DatePicker = view.findViewById(R.id.datePicker)
        val etDescription = view.findViewById<EditText>(R.id.tbDescription)
        val radioGroupType = view.findViewById<RadioGroup>(R.id.radioGroupType)
        val radioExpense = view.findViewById<RadioButton>(R.id.radioExpense)
        val radioIncome = view.findViewById<RadioButton>(R.id.radioIncome)

        val btnSelectCategory = view.findViewById<Button>(R.id.btn_select_category)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val back = view.findViewById<ImageButton>(R.id.btnBack)
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
        back.setOnClickListener {
            findNavController().navigateUp()
        }





        //soundpool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build()
        }else {
            soundPool = SoundPool(
                1,
                AudioManager.STREAM_MUSIC,
                0
            )

        }

saveSound = soundPool.load(requireContext(), R.raw.universfield_happy_message_ping,1)

        // Get today's date using Calendar instance
        val today = Calendar.getInstance()

        // Initialize DatePicker with the current date
        datePicker.init(
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ) { view, year, month, day ->
            // Display selected date in Toast message
            val msg = "You Selected: $year/${month+1}/$day"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }







        btnSelectCategory.setOnClickListener {
            showBottomView()
        }


        // val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        // next we are doing the save button. this will save the transaction entry that the logged in
        // user created, this will also be linked to that user with shared preferences

        btnSave.setOnClickListener {
            val name=etName.text.toString().trim()
            val amountText = etAmount.text.toString().trim()
            val content = etDescription.text.toString()
            val imageUrl = uploadedImageUrl ?: ""// if no image is uploaded empty string is returned



            if (name.isEmpty()) {
                etName.error = "Enter Transaction Name"
                return@setOnClickListener
            }

            if (content.isEmpty()) {
                etDescription.error = "Enter Description"
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
          val date= String.format("%04d-%02d-%02d",year,month,day)


            // Determine selected type from the RadioGroup
            //(StackOverflow, 2023)
            val selectedType = when (radioGroupType.checkedRadioButtonId) {
                R.id.radioIncome -> "income"
                else -> "expense"
            }

            if (selectedCategoryId.isEmpty()) {
                Toast.makeText(requireContext(),"Please pick a category for your transaction",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveTransaction(name,amount, selectedType, content, date, imageUrl, selectedCategoryId )
        }

    }
            //Firebase, 2026)
            //transaction is saved in firebase DB
            private fun saveTransaction(name: String, amount: Double, type: String, content: String,
           date: String, imageUrl: String, category: String ) {
            // push creates a unique key for the new transaction

                val uid = FirebaseAuth.getInstance().currentUser?.uid

                if(uid == null){
                    Toast.makeText(requireContext(),"User not logged in",Toast.LENGTH_SHORT).show()
                    return
                }

                val transactionRef = database.child(uid).push()

                val transactionId = transactionRef.key ?: return

            val newTransaction = Transaction(
                id = transactionId,
                name = name,
                type = type,
                amount = amount,
                content = content,
                date=date,
                imageUrl=imageUrl,
                categoryId = category
            )


            transactionRef.setValue(newTransaction)
                .addOnSuccessListener {

                    //Play success sound
                    requireView().postDelayed({
                        soundPool.play(saveSound,1f,1f,0,0,1f)
                    }, 200)

                    Toast.makeText(requireContext(), "$name transaction created!", Toast.LENGTH_SHORT).show()


             requireActivity().supportFragmentManager.setFragmentResult(
                      "mascot_request", Bundle()
                       .apply {
                       putString("type",type)
                    }
                 )
                    // Return to the transactions list
                    //(Android Developers, 2026)
             requireView().postDelayed({
                 if (isAdded && activity !=null) {
requireActivity().supportFragmentManager.popBackStack()
                 }
             },400)



                }
                .addOnFailureListener { error ->
                    Toast.makeText(requireContext(), "Failed to save: ${error.message}", Toast.LENGTH_SHORT).show()
                }



        }
    //choose category code
    private fun showBottomView() {
        /*GeekForGeeks
           * Inflate layout */
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val chipGroup = bottomSheetView.findViewById<ChipGroup>(R.id.cgCategories)
        val dialog = BottomSheetDialog(requireContext())

        //load firebase
        categorygoryDatabase.get().addOnSuccessListener { snapshot ->
            chipGroup.removeAllViews()
            for (child in snapshot.children) {
                val category = child.getValue(Category::class.java)
                val categoryId = child.key ?: continue


                if (category != null) {
                    val chip = Chip(requireContext())
                    chip.text = category.name
                    chip.tag = categoryId

                    chip.setOnClickListener {
                        selectedCategoryId = chip.tag.toString()
                        dialog.dismiss()
                    }
                    chipGroup.addView(chip)

                }
            }
        }
        dialog.setContentView(bottomSheetView)
        dialog.show()
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
                        txtStatus.text = "Status: ${it.message}"

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

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }
}

/*
Android Developers. 2026. Fragment manager | App architecture. (version 8) [source code]. Available at: < https://developer.android.com/guide/fragments/fragmentmanager > [Accessed 26 April 2026]
Android Knowledge. 2023. CRUD using Firebase Realtime Database in Android Studio using Kotlin | Create, Read, Update, Delete. (version 8) [source code] Available at: < https://youtu.be/oGyQMBKPuNY?si=Nj0DRStlfvfFvane > [Accessed 26 April 2026]
Bak, K. 2023. Validating user input in kotlin: a guide to adding validation to a new transaction. (version 8) [source code]. Available at: < https://53jk1.medium.com/validating-user-input-in-kotlin-a-guide-to-adding-validation-to-a-new-transaction-2e486f176674 > [Accessed 26 April 2026]
Firebase. 2026. Read and Write Data on Android. (version 8) [source code]. Available at: < https://firebase.google.com/docs/database/android/read-and-write > [Accessed 26 April 2026]
StackOverflow. 2023. Set Variable depending on RadioGroup button selected in Kotlin. (Version 8)
* Ghanem, A. 2024. What is Context? In Android, represents the current state of the application. It provides access to various resources (e. [online] Linkedin.com. Available at: <https://www.linkedin.com/pulse/context-android-abdelrhman-ghanem-zbpcf/ [Accessed 26 Apr. 2026].

‌>
 GeekforGeeks.2020.SoundPool in Android with Examples.(version 8) [source code]. Available at: <https://www.geeksforgeeks.org/android/soundpool-in-android-with-examples/ >[Accessed 11 May 2026]
 */
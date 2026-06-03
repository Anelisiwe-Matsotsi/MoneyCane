package com.example.prog7313_sem1_part2

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.savedstate.serialization.saved
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val navHostFragment =supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment

        val navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bnv_main)
        bottomNav.setOnItemSelectedListener { item ->

            val options = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setRestoreState(true)
                .setPopUpTo(navController.graph.startDestinationId, false)
                .build()

            navController.navigate(item.itemId, null, options)
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->

            when(destination.id) {
                R.id.logFragment,
                R.id.splashFragment,
                R.id.AuthenticationFragment,
                R.id.AppSecurityFragment,
                R.id.CreatePinFragment
                    -> { bottomNav.visibility = View.GONE
                }

                else -> {
                    bottomNav.visibility = View.VISIBLE
                }
            }
        }
        notificationPermission() //prompts user when app starts
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment_container)
        return navController.navigateUp()

    }

    //(Poplawski, 2023)
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->  //(Android Developers, 2026)
        if (isGranted) {
            // Permission granted, you can now send notifications.
        } else {
            // Permission denied, handle accordingly.
        }
    }

    //(Android Developers, 2026)
    private fun notificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED ->
                {

                }else -> {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    companion object {//links to AllTransactionsFragment, ExpensesFragment, IncomesFragment
        fun getUserID(): String {
            val user = Firebase.auth.currentUser

            user?.let {
                val uid = it.uid
                return uid
            }
            return ""
        }
    }
}
//reference
// https://www.youtube.com/watch?v=0LLlMz2NPhE
//https://www.linkedin.com/posts/chauhan-paresh-915340236_androiddevelopment-kotlin-navigationcomponent-activity-7377347549923409920-t3b9
//https://medium.com/@appdevinsights/companion-object-in-kotlin-c3a1203cd63c how to add and what are companion objects
//https://developer.android.com/guide/navigation/backstack how to set up the bottom navigation bar to pop back to spesific fragments

/* References

Android Developers. 2026. Notification runtime permission. (version 8) [Source Code]. Available at: <https://developer.android.com/develop/ui/compose/notifications/notification-permission> [Accessed 3 June 2026]
Poplawski, A. 2023. Flexible way to request runtime permissions on Android 13 from anywhere in the app (feat. DI & Composition). (version 8)[source code]. Available at: <https://medium.com/@a.poplawski96/flexible-way-to-request-runtime-permissions-on-android-13-from-anywhere-in-the-app-feat-720174dff44e> [Accessed 3 June 2026]

*/
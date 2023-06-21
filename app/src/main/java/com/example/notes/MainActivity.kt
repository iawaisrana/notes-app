package com.example.notes

import android.Manifest.permission.POST_NOTIFICATIONS
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.notes.navigation.SetupNavigation
import com.example.notes.ui.theme.NotesTheme
import com.example.notes.ui.viewmodels.SharedViewModel
import com.example.notes.utils.GlobalVariable
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private lateinit var sharedViewModel: SharedViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                sharedViewModel.shouldShowSplashScreen.value
            }
        }

        if (sharedViewModel.shouldShowSplashScreen.value) {
            requestNotificationPermission()
        }

        setContent {
            NotesTheme {
                navController = rememberNavController()
                SetupNavigation(navController = navController, sharedViewModel = sharedViewModel)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestNotificationPermission() {
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                Log.d("NOTIFICATION_PERMISSION", isGranted.toString())
                GlobalVariable.hasNotificationPermission = isGranted
            }
        requestPermissionLauncher.launch(
            POST_NOTIFICATIONS
        )
    }
}

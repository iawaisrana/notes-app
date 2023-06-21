package com.example.notes.navigation.destinations

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.notes.ui.screens.splash.SplashScreen
import com.example.notes.utils.Constants.SPLASH_SCREEN

fun NavGraphBuilder.splashComposable(
    navigateToListScreen: () -> Unit
) {
    composable(route = SPLASH_SCREEN) {
        SplashScreen(navigateToListScreen = navigateToListScreen)
    }
}
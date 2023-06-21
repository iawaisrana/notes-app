package com.example.notes.navigation

import androidx.navigation.NavController
import com.example.notes.utils.Action
import com.example.notes.utils.Constants.LIST_SCREEN
import com.example.notes.utils.Constants.SPLASH_SCREEN

class ScreenRoutes(navController: NavController) {

    val fromNoteToList: (Action) -> Unit = { action ->
        navController.navigate(route = "list/${action.name}") {
            popUpTo(LIST_SCREEN) { inclusive = true }
        }
    }

    val fromListToNote: (Int) -> Unit = { noteId ->
        navController.navigate(route = "note/$noteId")
    }

    val fromSplashToList: () -> Unit = {
        navController.navigate(route = "list/${Action.NO_ACTION.name}") {
            popUpTo(SPLASH_SCREEN) { inclusive = true }
        }
    }
}
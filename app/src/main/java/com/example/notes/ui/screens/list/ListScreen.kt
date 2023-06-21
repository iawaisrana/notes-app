package com.example.notes.ui.screens.list

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.notes.R
import com.example.notes.components.CustomLoading
import com.example.notes.data.models.Note
import com.example.notes.data.models.Priority
import com.example.notes.ui.theme.BlackOlive
import com.example.notes.ui.viewmodels.SharedViewModel
import com.example.notes.utils.Action
import com.example.notes.utils.RequestState
import com.example.notes.utils.SearchAppBarState
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ListScreen(
    navigateToNoteScreen: (taskId: Int) -> Unit,
    sharedViewModel: SharedViewModel
) {

    val action by sharedViewModel.action
    val allNotes = sharedViewModel.allNotes.collectAsLazyPagingItems()
    val sortState by sharedViewModel.sortState.collectAsState()
    val lowPriorityNotes = sharedViewModel.sortLowPriorityNotes.collectAsLazyPagingItems()
    val highPriorityNotes = sharedViewModel.sortHighPriorityNotes.collectAsLazyPagingItems()
    val searchedNotes = sharedViewModel.searchedNotes.collectAsLazyPagingItems()
    val searchAppBarState: SearchAppBarState by sharedViewModel.searchAppBarState


    LaunchedEffect(key1 = action) {
        sharedViewModel.handleDatabaseAction(action = action)
    }

    val scaffoldState = rememberScaffoldState()

    DisplaySnackBar(
        scaffoldState = scaffoldState,
        onComplete = { sharedViewModel.action.value = it },
        onUndoClicked = {
            sharedViewModel.action.value = it
        },
        noteTitle = sharedViewModel.title.value,
        action = action
    )

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackbarHost(it) { data ->
                Snackbar(
                    actionColor = MaterialTheme.colors.primary,
                    snackbarData = data,
                    contentColor = MaterialTheme.colors.primary,
                    backgroundColor = MaterialTheme.colors.secondary
                )
            }
        },
        backgroundColor = MaterialTheme.colors.primary,
        topBar = {
            ListAppBar(
                sharedViewModel = sharedViewModel,
                searchAppBarState = searchAppBarState
            )
        },
        content = {
            if (sortState is RequestState.Success) {
                when {
                    searchAppBarState == SearchAppBarState.TRIGGERED -> {
                        HandleListContent(
                            notes = searchedNotes,
                            onSwipeToDelete = { action, note ->
                                onSwipeToDelete(
                                    sharedViewModel = sharedViewModel,
                                    action = action,
                                    note = note,
                                    scaffoldState = scaffoldState
                                )
                            },
                            navigateToNoteScreen = navigateToNoteScreen
                        )
                    }
                    (sortState as RequestState.Success<Priority>).data == Priority.LOW -> {
                        HandleListContent(
                            notes = lowPriorityNotes,
                            onSwipeToDelete = { action, note ->
                                onSwipeToDelete(
                                    sharedViewModel = sharedViewModel,
                                    action = action,
                                    note = note,
                                    scaffoldState = scaffoldState
                                )
                            },
                            navigateToNoteScreen = navigateToNoteScreen
                        )
                    }
                    (sortState as RequestState.Success<Priority>).data == Priority.HIGH -> {
                        HandleListContent(
                            notes = highPriorityNotes,
                            onSwipeToDelete = { action, note ->
                                onSwipeToDelete(
                                    sharedViewModel = sharedViewModel,
                                    action = action,
                                    note = note,
                                    scaffoldState = scaffoldState
                                )
                            },
                            navigateToNoteScreen = navigateToNoteScreen
                        )
                    }
                    (sortState as RequestState.Success<Priority>).data == Priority.NONE -> {
                        HandleListContent(
                            notes = allNotes,
                            onSwipeToDelete = { action, note ->
                                onSwipeToDelete(
                                    sharedViewModel = sharedViewModel,
                                    action = action,
                                    note = note,
                                    scaffoldState = scaffoldState
                                )
                            },
                            navigateToNoteScreen = navigateToNoteScreen
                        )

                    }
                    else -> {}
                }
            }
        },
        floatingActionButton = {
            FloatingButton(onFloatingActionButtonPressed = navigateToNoteScreen)
        })
}

private fun onSwipeToDelete(
    scaffoldState: ScaffoldState,
    sharedViewModel: SharedViewModel,
    action: Action,
    note: Note
) {
    sharedViewModel.action.value = action
    sharedViewModel.updateNoteFields(note)
    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
}

@Composable
fun HandleListContent(
    notes: LazyPagingItems<Note>,
    onSwipeToDelete: (Action, Note) -> Unit,
    navigateToNoteScreen: (taskId: Int) -> Unit
) {
    Log.d("Notes_LOG", notes.loadState.toString())
    if (notes.loadState.refresh == LoadState.Loading) {
        CustomLoading()
    } else {
        if (notes.itemCount == 0) {
            EmptyContent()
        } else {
            ListContent(
                notes = notes,
                onSwipeToDelete = onSwipeToDelete,
                navigateToNoteScreen = navigateToNoteScreen
            )
        }
    }
}


@Composable
fun FloatingButton(onFloatingActionButtonPressed: (taskId: Int) -> Unit) {
    FloatingActionButton(
        modifier = Modifier.padding(end = 8.dp, bottom = 32.dp),
        elevation = FloatingActionButtonDefaults.elevation(20.dp),
        backgroundColor = BlackOlive,
        onClick = { onFloatingActionButtonPressed(-1) }) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = stringResource(id = R.string.add_note_action),
            tint = MaterialTheme.colors.secondary
        )
    }
}

@Composable
fun DisplaySnackBar(
    scaffoldState: ScaffoldState,
    onComplete: (Action) -> Unit,
    onUndoClicked: (Action) -> Unit,
    noteTitle: String,
    action: Action
) {

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = action) {
        if (action != Action.NO_ACTION) {
            scope.launch {
                val snackBarResult = scaffoldState.snackbarHostState.showSnackbar(
                    message = getSnackBarMessage(action = action, noteTitle = noteTitle),
                    actionLabel = getActionLabel(action)
                )

                undoDeleteTask(
                    action = action,
                    snackBarResult = snackBarResult,
                    onUndoClicked = onUndoClicked
                )
            }
        }
        onComplete(Action.NO_ACTION)
    }
}

private fun getSnackBarMessage(action: Action, noteTitle: String): String {
    return when (action) {
        Action.DELETE_ALL -> {
            "All Notes Delete"
        }
        else -> {
            "${action.name}: $noteTitle"
        }
    }
}

private fun getActionLabel(action: Action): String {
    return if (action.name == "DELETE") {
        "UNDO"
    } else {
        "OK"
    }
}

private fun undoDeleteTask(
    action: Action,
    snackBarResult: SnackbarResult,
    onUndoClicked: (Action) -> Unit
) {
    if (snackBarResult == SnackbarResult.ActionPerformed && action == Action.DELETE) {
        onUndoClicked(Action.UNDO)
    }
}
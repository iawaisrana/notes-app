package com.example.notes.ui.screens.note

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notes.R
import com.example.notes.components.CustomText
import com.example.notes.components.DisplayAlertDialog
import com.example.notes.data.models.Note
import com.example.notes.data.models.Priority
import com.example.notes.ui.theme.BlackOlive
import com.example.notes.utils.Action
import java.util.*

@Composable
fun NoteAppBar(navigateToListScreen: (Action) -> Unit, selectedNote: Note?) {
    if (selectedNote == null) {
        NewNoteAppBar(navigateToListScreen = navigateToListScreen)
    } else {
        EditNoteAppBar(note = selectedNote, navigateToListScreen = navigateToListScreen)
    }
}

@Composable
fun NewNoteAppBar(navigateToListScreen: (Action) -> Unit) {
    TopAppBar(
        elevation = 0.dp,
        navigationIcon = {
            Divider(modifier = Modifier.width(12.dp), color = MaterialTheme.colors.primary)
            BackButton(backButtonPressed = navigateToListScreen)
        },
        title = {
            CustomText(
                text = stringResource(id = R.string.add_note),
                color = MaterialTheme.colors.secondary,
                fontSize = 20.sp,
                fontWeight = FontWeight.W600
            )
        },
        backgroundColor = MaterialTheme.colors.primary,
        actions = {
            AddNoteButton(addNoteButtonPressed = navigateToListScreen)
            Divider(modifier = Modifier.width(12.dp), color = MaterialTheme.colors.primary)
        }
    )
}

@Composable
fun BackButton(backButtonPressed: (Action) -> Unit) {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(40.dp)
            .background(color = BlackOlive, shape = RoundedCornerShape(10.dp))
            .clickable { backButtonPressed(Action.NO_ACTION) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = stringResource(id = R.string.back_arrow),
            tint = MaterialTheme.colors.secondary
        )
    }
}

@Composable
fun AddNoteButton(addNoteButtonPressed: (Action) -> Unit) {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(40.dp)
            .background(color = BlackOlive, shape = RoundedCornerShape(10.dp))
            .clickable { addNoteButtonPressed(Action.ADD) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_save),
            contentDescription = stringResource(id = R.string.save_note_action),
            tint = MaterialTheme.colors.secondary
        )
    }
}

@Composable
fun EditNoteAppBar(note: Note, navigateToListScreen: (Action) -> Unit) {
    TopAppBar(
        elevation = 0.dp,
        navigationIcon = {
            Divider(modifier = Modifier.width(12.dp), color = MaterialTheme.colors.primary)
            BackButton(backButtonPressed = navigateToListScreen)
        },
        title = {
            CustomText(
                text = stringResource(id = R.string.edit_note),
                color = MaterialTheme.colors.secondary,
                fontSize = 20.sp,
                fontWeight = FontWeight.W600
            )
        },
        backgroundColor = MaterialTheme.colors.primary,
        actions = {
            EditNoteAppBarActions(note = note, navigateToListScreen = navigateToListScreen)
            Divider(modifier = Modifier.width(12.dp), color = MaterialTheme.colors.primary)
        }
    )
}

@Composable
fun EditNoteAppBarActions(note: Note, navigateToListScreen: (Action) -> Unit) {
    var openDialog by remember {
        mutableStateOf(false)
    }

    DisplayAlertDialog(
        title = stringResource(id = R.string.delete_note_alert_title),
        message = stringResource(id = R.string.delete_note_alert_message),
        openDialog = openDialog,
        button1Text = "No",
        button2Text = "Yes",
        onButton1Pressed = { openDialog = false },
        onButton2Pressed = {
            openDialog = false
            navigateToListScreen(Action.DELETE)
        })

    DeleteNoteButton(deleteNoteButtonPressed = { openDialog = true })
    Divider(modifier = Modifier.width(12.dp), color = MaterialTheme.colors.primary)
    EditNoteButton(editNoteButtonPressed = navigateToListScreen)
}

@Composable
fun EditNoteButton(editNoteButtonPressed: (Action) -> Unit) {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(40.dp)
            .background(color = BlackOlive, shape = RoundedCornerShape(10.dp))
            .clickable { editNoteButtonPressed(Action.UPDATE) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_save),
            contentDescription = stringResource(id = R.string.edit_note_action),
            tint = MaterialTheme.colors.secondary
        )
    }
}

@Composable
fun DeleteNoteButton(deleteNoteButtonPressed: () -> Unit) {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(40.dp)
            .background(color = BlackOlive, shape = RoundedCornerShape(10.dp))
            .clickable { deleteNoteButtonPressed() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_delete),
            contentDescription = stringResource(id = R.string.delete_note_action),
            tint = MaterialTheme.colors.secondary
        )
    }
}

@Composable
@Preview
fun NoteAppBarPreview() {
    NoteAppBar(navigateToListScreen = {}, selectedNote = null)
}

@Composable
@Preview
fun EditNoteAppBarPreview() {
    EditNoteAppBar(
        note = Note(
            id = 0,
            title = "UI concepts worth existing",
            description = "UI concepts worth existing",
            priority = Priority.HIGH,
            reminderDateTime = null,
            workerRequestId = null,
            createdAt = Date(),
            updatedAt = Date()
        ), navigateToListScreen = {})
}
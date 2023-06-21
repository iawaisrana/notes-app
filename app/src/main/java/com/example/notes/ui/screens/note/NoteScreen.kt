package com.example.notes.ui.screens.note

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.notes.R
import com.example.notes.data.models.Note
import com.example.notes.data.models.Priority
import com.example.notes.ui.viewmodels.SharedViewModel
import com.example.notes.utils.Action
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NoteScreen(
    navigateToListScreen: (Action) -> Unit, selectedNote: Note?, sharedViewModel: SharedViewModel
) {
    val title: String by sharedViewModel.title
    val description: String by sharedViewModel.description
    val priority: Priority by sharedViewModel.priority
    val reminderDateTime: Date? by sharedViewModel.reminderDateTime

    val context = LocalContext.current
    val validationErrorMessage = stringResource(id = R.string.note_validation_error)

    BackHandler {
        navigateToListScreen(Action.NO_ACTION)
    }

    Scaffold(backgroundColor = MaterialTheme.colors.primary, topBar = {
        NoteAppBar(
            navigateToListScreen = { action ->
                if (action == Action.NO_ACTION || sharedViewModel.validateNoteFields()) {
                    navigateToListScreen(action)
                } else {
                    displayToast(
                        context = context, message = validationErrorMessage
                    )
                }
            }, selectedNote = selectedNote
        )
    }, content = {
        NoteContent(
            title = title,
            onTitleChange = { title ->
                sharedViewModel.title.value = title
            },
            description = description,
            onDescriptionChange = { description ->
                sharedViewModel.description.value = description
            },
            priority = priority,
            onPriorityChange = { priority ->
                sharedViewModel.priority.value = priority
            },
            reminderDateTime = reminderDateTime,
            onReminderDateTimeChange = { date ->
                sharedViewModel.reminderDateTime.value = date
            }
        )
    })
}

fun displayToast(context: Context, message: String) {
    val toast: Toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);

    val view: View? = toast.getView();

    view?.getBackground()?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

    val text: TextView? = view?.findViewById(android.R.id.message);

    text?.setTextColor(Color.BLACK);
    text?.setBackgroundColor(Color.TRANSPARENT);

    toast.show();
}
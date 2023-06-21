package com.example.notes.ui.screens.list

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.example.notes.R
import com.example.notes.components.CustomLoading
import com.example.notes.components.CustomText
import com.example.notes.data.models.Note
import com.example.notes.data.models.Priority
import com.example.notes.ui.theme.BlackShade
import com.example.notes.ui.theme.Red
import com.example.notes.utils.Action
import com.example.notes.utils.noteItemColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListContent(
    notes: LazyPagingItems<Note>,
    onSwipeToDelete: (Action, Note) -> Unit,
    navigateToNoteScreen: (taskId: Int) -> Unit
) {
    Log.d("LazyPagingItems_LoadState_APPEND", notes.loadState.append.toString())
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp)
    ) {
        itemsIndexed(notes, key = { _, note -> note.id }) { index, note ->
            note?.let {
                val dismissState = rememberDismissState()
                val dismissDirection = dismissState.dismissDirection
                val isDismissed = dismissState.isDismissed(DismissDirection.EndToStart)

                if (isDismissed && dismissDirection == DismissDirection.EndToStart) {
                    val scope = rememberCoroutineScope()
                    scope.launch {
                        delay(500)
                        onSwipeToDelete(Action.DELETE, note)
                    }
                }

                val degrees by animateFloatAsState(
                    targetValue = if (dismissState.targetValue == DismissValue.Default) 0f
                    else -45f
                )

                var itemAppeared by remember {
                    mutableStateOf(false)
                }

                LaunchedEffect(key1 = true) {
                    itemAppeared = true
                }

                AnimatedVisibility(
                    visible = itemAppeared && !isDismissed,
                    enter = expandVertically(
                        animationSpec = tween(durationMillis = 500)
                    ),
                    exit = shrinkVertically(
                        animationSpec = tween(durationMillis = 500)
                    )
                ) {
                    SwipeToDismiss(modifier = Modifier
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                        .clip(RoundedCornerShape(10.dp)),
                        state = dismissState,
                        directions = setOf(DismissDirection.EndToStart),
                        dismissThresholds = { FractionalThreshold(0.2f) },
                        background = { SwipeItemBackground(degrees = degrees) },
                        dismissContent = {
                            NoteItem(
                                note = note,
                                index = index,
                                navigateToNoteScreen = navigateToNoteScreen
                            )
                        })
                }
            }
        }

        when (notes.loadState.append) {
            is LoadState.Loading -> {
                item {
                    CustomLoading()
                }
            }
            is LoadState.NotLoading -> Unit
            is LoadState.Error -> Unit
        }
    }
}

@Composable
fun SwipeItemBackground(degrees: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Red), contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            modifier = Modifier
                .rotate(degrees = degrees)
                .padding(end = 16.dp),
            imageVector = Icons.Filled.Delete,
            contentDescription = stringResource(id = R.string.delete_note_action),
            tint = MaterialTheme.colors.secondary
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NoteItem(
    note: Note, index: Int, navigateToNoteScreen: (taskId: Int) -> Unit
) {
    Surface(modifier = Modifier.fillMaxWidth(),
        color = noteItemColor(index = index),
        elevation = 10.dp,
        onClick = {
            navigateToNoteScreen(note.id)
        }) {

        Column(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 24.dp)
                .fillMaxWidth()
        ) {
            CustomText(
                text = note.title,
                color = BlackShade,
                fontWeight = FontWeight.W700,
                fontSize = 22.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            CustomText(
                modifier = Modifier.padding(top = 8.dp),
                text = note.description,
                color = BlackShade,
                fontWeight = FontWeight.W400,
                fontSize = 18.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CustomText(
                        text = "${stringResource(id = R.string.priority)}:",
                        color = MaterialTheme.colors.primary,
                        fontWeight = FontWeight.W700,
                        fontSize = 16.sp
                    )
                    CustomText(
                        modifier = Modifier
                            .padding(start = 8.dp),
                        text = note.priority.name,
                        color = MaterialTheme.colors.primary,
                        fontWeight = FontWeight.W400,
                        fontSize = 14.sp
                    )
                }

                CustomText(
                    text = SimpleDateFormat(
                        "E, dd MMM yyyy",
                        Locale.ENGLISH
                    ).format(note.updatedAt),
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.W500,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
@Preview
fun NoteItemPreview() {
    NoteItem(note = Note(
        id = 0,
        title = "Book Review : The Design of Everyday Things by Don Norman",
        description = "Book Review : The Design of Everyday Things by Don Norman",
        priority = Priority.HIGH,
        reminderDateTime = null,
        workerRequestId = null,
        createdAt = Date(),
        updatedAt = Date()
    ),
        index = 0,
        navigateToNoteScreen = {})
}

@Composable
@Preview
fun CustomLoadingPreview() {
    CustomLoading()
}


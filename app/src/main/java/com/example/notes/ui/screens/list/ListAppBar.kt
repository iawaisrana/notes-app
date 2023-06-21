package com.example.notes.ui.screens.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notes.R
import com.example.notes.components.CustomText
import com.example.notes.components.DisplayAlertDialog
import com.example.notes.components.PriorityItem
import com.example.notes.data.models.Priority
import com.example.notes.ui.theme.BlackOlive
import com.example.notes.ui.theme.ChineseSilver
import com.example.notes.ui.theme.fontFamily
import com.example.notes.ui.viewmodels.SharedViewModel
import com.example.notes.utils.APP_BAR_HEIGHT
import com.example.notes.utils.Action
import com.example.notes.utils.SearchAppBarState

@Composable
fun ListAppBar(
    sharedViewModel: SharedViewModel,
    searchAppBarState: SearchAppBarState,
) {

    when (searchAppBarState) {
        SearchAppBarState.CLOSED -> {
            DefaultListAppBar(
                onSearchIconPressed = {
                    sharedViewModel.searchAppBarState.value = SearchAppBarState.OPENED
                },
                sortNotesByPriority = { priority ->
                    sharedViewModel.persistSortState(priority)
                },
                deleteAllNotes = {
                    sharedViewModel.action.value = Action.DELETE_ALL
                }
            )
        }
        else -> {
            SearchAppBar(
                onSearchPressed = { text ->
                    sharedViewModel.searchTextState.value = text
                    sharedViewModel.searchNotes()
                },
                onClosePressed = {
                    sharedViewModel.searchAppBarState.value = SearchAppBarState.CLOSED
                    sharedViewModel.searchTextState.value = ""
                })
        }
    }


}

@Composable
fun DefaultListAppBar(
    onSearchIconPressed: () -> Unit,
    sortNotesByPriority: (priority: Priority) -> Unit,
    deleteAllNotes: () -> Unit
) {
    TopAppBar(elevation = 0.dp, title = {
        CustomText(
            text = stringResource(id = R.string.app_name),
            color = MaterialTheme.colors.secondary,
            fontSize = 32.sp,
            fontWeight = FontWeight.W600,

            )
    }, backgroundColor = MaterialTheme.colors.primary, actions = {
        ListAppBarActions(
            onSearchIconPressed = onSearchIconPressed,
            sortNotesByPriority = sortNotesByPriority,
            deleteAllNotes = deleteAllNotes
        )
    })
}

@Composable
fun ListAppBarActions(
    onSearchIconPressed: () -> Unit,
    sortNotesByPriority: (priority: Priority) -> Unit,
    deleteAllNotes: () -> Unit
) {
    SearchAction(onSearchIconPressed = onSearchIconPressed)
    Divider(modifier = Modifier.width(16.dp), color = MaterialTheme.colors.primary)
    SortAction(sortNotesByPriority = sortNotesByPriority)
    Divider(modifier = Modifier.width(16.dp), color = MaterialTheme.colors.primary)
    DeleteAllAction(deleteAllNotes = deleteAllNotes)
    Divider(modifier = Modifier.width(12.dp), color = MaterialTheme.colors.primary)
}

@Composable
fun SearchAction(onSearchIconPressed: () -> Unit) {
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(40.dp)
            .background(color = BlackOlive, shape = RoundedCornerShape(10.dp))
            .clickable { onSearchIconPressed() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = stringResource(id = R.string.search_notes_action),
            tint = MaterialTheme.colors.secondary
        )
    }
}

@Composable
fun SortAction(sortNotesByPriority: (priority: Priority) -> Unit) {
    var expanded by remember {
        mutableStateOf(false)
    }
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(40.dp)
            .background(color = BlackOlive, shape = RoundedCornerShape(10.dp))
            .clickable { expanded = true },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_filter_list),
            contentDescription = stringResource(id = R.string.sort_notes_action),
            tint = MaterialTheme.colors.secondary
        )

        DropdownMenu(
            modifier = Modifier.background(BlackOlive),
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
            Priority.values().slice(setOf(0, 2, 3)).forEach { priority ->
                DropdownMenuItem(onClick = {
                    sortNotesByPriority(priority)
                    expanded = false
                }) {
                    PriorityItem(priority = priority)
                }
            }
        }
    }
}

@Composable
fun DeleteAllAction(deleteAllNotes: () -> Unit) {

    var expanded by remember {
        mutableStateOf(false)
    }

    var openDialog by remember {
        mutableStateOf(false)
    }

    DisplayAlertDialog(
        title = stringResource(id = R.string.delete_all_notes_alert_title),
        message = stringResource(id = R.string.delete_all_notes_alert_message),
        openDialog = openDialog,
        button1Text = "No",
        button2Text = "Yes",
        onButton1Pressed = { openDialog = false },
        onButton2Pressed = {
            openDialog = false
            deleteAllNotes()
        })

    Box(
        modifier = Modifier
            .width(40.dp)
            .height(40.dp)
            .background(color = BlackOlive, shape = RoundedCornerShape(10.dp))
            .clickable { expanded = true },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = stringResource(id = R.string.delete_all_notes_action),
            tint = MaterialTheme.colors.secondary
        )

        DropdownMenu(
            modifier = Modifier.background(BlackOlive),
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
            DropdownMenuItem(onClick = {
                openDialog = true
                expanded = false
            }) {
                CustomText(
                    text = stringResource(id = R.string.delete_all_notes_action),
                    color = MaterialTheme.colors.secondary,
                    fontWeight = FontWeight.W400,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun SearchAppBar(
    onClosePressed: () -> Unit,
    onSearchPressed: (String) -> Unit
) {
    var searchText by remember {
        mutableStateOf("")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(APP_BAR_HEIGHT),
        horizontalArrangement = Arrangement.Center
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.9f)
                .clip(shape = RoundedCornerShape(30.dp))
                .background(color = BlackOlive),
            value = searchText,
            onValueChange = { text ->
                searchText = text
            },
            placeholder = {
                CustomText(
                    modifier = Modifier.alpha(ContentAlpha.medium),
                    text = stringResource(id = R.string.search_placeholder),
                    color = ChineseSilver,
                    fontWeight = FontWeight.W300,
                    fontSize = 18.sp
                )
            },
            textStyle = TextStyle(
                color = MaterialTheme.colors.secondary,
                fontFamily = fontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 18.sp
            ),
            singleLine = true,
            leadingIcon = {
                IconButton(
                    modifier = Modifier
                        .alpha(ContentAlpha.medium)
                        .padding(start = 8.dp),
                    onClick = { }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = stringResource(id = R.string.search_icon),
                        tint = MaterialTheme.colors.secondary
                    )
                }
            },
            trailingIcon = {
                IconButton(modifier = Modifier
                    .alpha(ContentAlpha.medium)
                    .padding(end = 8.dp),
                    onClick = {
                        if (searchText.isEmpty()) {
                            onClosePressed()
                        } else {
                            searchText = ""
                        }
                    }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(id = R.string.close_icon),
                        tint = MaterialTheme.colors.secondary
                    )
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchPressed(searchText)
                }
            ),
            colors = TextFieldDefaults.textFieldColors(
                cursorColor = MaterialTheme.colors.secondary,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                backgroundColor = Color.Transparent
            )
        )
    }
}


@Composable
@Preview
private fun DefaultListAppBarPreview() {
    SearchAppBar(onSearchPressed = {}, onClosePressed = {})
    //DefaultListAppBar(onSearchClicked = {}, onSortClicked = {}, onDeleteClicked = {})
}
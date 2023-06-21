package com.example.notes.ui.screens.note

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.notes.components.PriorityDropDown
import com.example.notes.data.models.Priority
import com.example.notes.ui.theme.ChineseSilver
import com.example.notes.ui.theme.fontFamily
import com.example.notes.utils.GlobalVariable
import com.example.notes.utils.dateToString
import java.util.*

@Composable
fun NoteContent(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    priority: Priority,
    onPriorityChange: (Priority) -> Unit,
    reminderDateTime: Date?,
    onReminderDateTimeChange: (Date) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp)
    ) {
        CustomText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            text = stringResource(id = R.string.title),
            color = MaterialTheme.colors.secondary,
            fontSize = 20.sp,
            fontWeight = FontWeight.W600
        )

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = title,
            onValueChange = { text ->
                if (text.length <= 35) onTitleChange(text)
            },
            placeholder = {
                CustomText(
                    modifier = Modifier.alpha(ContentAlpha.medium),
                    text = stringResource(id = R.string.enter_title),
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
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.None),
            colors = TextFieldDefaults.textFieldColors(
                cursorColor = MaterialTheme.colors.secondary,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                backgroundColor = Color.Transparent
            )
        )

        Divider(
            modifier = Modifier.height(8.dp), color = MaterialTheme.colors.primary
        )

        CustomText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            text = stringResource(id = R.string.priority),
            color = MaterialTheme.colors.secondary,
            fontSize = 20.sp,
            fontWeight = FontWeight.W600
        )

        Divider(
            modifier = Modifier.height(12.dp), color = MaterialTheme.colors.primary
        )

        PriorityDropDown(priority = priority, onPriorityChange = onPriorityChange)

        Divider(
            modifier = Modifier.height(16.dp), color = MaterialTheme.colors.primary
        )

        if (GlobalVariable.hasNotificationPermission) {
            CustomText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                text = stringResource(id = R.string.reminder_title),
                color = MaterialTheme.colors.secondary,
                fontSize = 20.sp,
                fontWeight = FontWeight.W600
            )

            Divider(
                modifier = Modifier.height(16.dp), color = MaterialTheme.colors.primary
            )

            DateAndTimerPicker(
                reminderDateTime = reminderDateTime,
                onReminderDateTimeChange = onReminderDateTimeChange
            )

            Divider(
                modifier = Modifier.height(20.dp), color = MaterialTheme.colors.primary
            )
        }

        CustomText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            text = stringResource(id = R.string.description),
            color = MaterialTheme.colors.secondary,
            fontSize = 20.sp,
            fontWeight = FontWeight.W600
        )

        TextField(
            modifier = Modifier.fillMaxSize(),
            value = description,
            onValueChange = { text ->
                onDescriptionChange(text)
            },
            placeholder = {
                CustomText(
                    modifier = Modifier.alpha(ContentAlpha.medium),
                    text = stringResource(id = R.string.enter_description),
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
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.None),
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
fun DateAndTimerPicker(
    reminderDateTime: Date?,
    onReminderDateTimeChange: (Date) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    val mContext = LocalContext.current
    var selectedYear: Int = 0
    var selectedMonth: Int = 0
    var selectedDay: Int = 0
    var selectedHour: Int = 0
    var selectedMin: Int = 0

    // Instance to display today date in both date and time picker
    val today = Calendar.getInstance()

    // Creating a TimePicker dialog
    val mTimePickerDialog = TimePickerDialog(
        mContext, { _, hour: Int, minute: Int ->
            selectedHour = hour
            selectedMin = minute
            val calendar = Calendar.getInstance()
            calendar.set(
                selectedYear, selectedMonth, selectedDay, selectedHour, selectedMin
            )
            onReminderDateTimeChange(calendar.time)
        }, today[Calendar.HOUR_OF_DAY], today[Calendar.MINUTE], false
    )

    // Creating a DatePicker dialog
    val mDatePickerDialog = DatePickerDialog(
        mContext, { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            selectedYear = year
            selectedMonth = month
            selectedDay = dayOfMonth
            mTimePickerDialog.show()
        }, today[Calendar.YEAR], today[Calendar.MONTH], today[Calendar.DAY_OF_MONTH]
    )

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp)
        .clickable(
            interactionSource = interactionSource, indication = null
        ) {
            mDatePickerDialog.show()
        }) {
        Icon(
            painter = painterResource(id = R.drawable.ic_calendar),
            contentDescription = stringResource(id = R.string.calendar_icon),
            tint = MaterialTheme.colors.secondary
        )
        CustomText(
            modifier = Modifier
                .alpha(ContentAlpha.medium)
                .padding(start = 12.dp),
            text = if (dateToString(reminderDateTime).isNullOrEmpty()) stringResource(id = R.string.pick_date_time)
            else dateToString(reminderDateTime)!!,
            color = ChineseSilver,
            fontWeight = FontWeight.W300,
            fontSize = 18.sp
        )
    }
}

@Composable
@Preview
fun NoteContentPreview() {
    NoteContent(
        title = "Title",
        onTitleChange = {},
        description = "Description",
        onDescriptionChange = {},
        priority = Priority.LOW,
        onPriorityChange = {},
        reminderDateTime = Date(),
        onReminderDateTimeChange = {}
    )
}
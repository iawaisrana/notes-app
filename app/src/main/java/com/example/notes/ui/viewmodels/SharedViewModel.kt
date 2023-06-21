package com.example.notes.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.notes.data.models.Note
import com.example.notes.data.models.Priority
import com.example.notes.data.repositories.DataStoreRepository
import com.example.notes.data.repositories.NotesRepository
import com.example.notes.utils.Action
import com.example.notes.utils.RequestState
import com.example.notes.utils.SearchAppBarState
import com.example.notes.worker.NoteReminderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val application: Application,
    private val notesRepository: NotesRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val workManager: WorkManager
) : ViewModel() {

    private val _shouldShowSplashScreen = MutableStateFlow<Boolean>(true)
    val shouldShowSplashScreen = _shouldShowSplashScreen.asStateFlow()

    val id: MutableState<Int> = mutableStateOf(0)
    val title: MutableState<String> = mutableStateOf("")
    val description: MutableState<String> = mutableStateOf("")
    val priority: MutableState<Priority> = mutableStateOf(Priority.LOW)
    val reminderDateTime: MutableState<Date?> = mutableStateOf(null)
    private val workerRequestId: MutableState<UUID?> = mutableStateOf(null)
    private val createdAt: MutableState<Date> = mutableStateOf(Date())
    private val updatedAt: MutableState<Date> = mutableStateOf(Date())

    val action: MutableState<Action> = mutableStateOf(Action.NO_ACTION)

    private val _selectedNote: MutableStateFlow<Note?> = MutableStateFlow(null)
    val selectedNote: MutableStateFlow<Note?> = _selectedNote

    val searchAppBarState: MutableState<SearchAppBarState> =
        mutableStateOf(SearchAppBarState.CLOSED)
    val searchTextState: MutableState<String> = mutableStateOf("")

    private val _allNotes = MutableStateFlow<PagingData<Note>>(PagingData.empty())
    val allNotes: StateFlow<PagingData<Note>> = _allNotes

    private val _searchedNotes = MutableStateFlow<PagingData<Note>>(PagingData.empty())
    val searchedNotes: StateFlow<PagingData<Note>> = _searchedNotes

    private val _sortState = MutableStateFlow<RequestState<Priority>>(RequestState.Idle)
    val sortState: StateFlow<RequestState<Priority>> = _sortState

    val sortLowPriorityNotes: Flow<PagingData<Note>> =
        notesRepository.sortByHighPriority().cachedIn(viewModelScope)
    val sortHighPriorityNotes: Flow<PagingData<Note>> =
        notesRepository.sortByHighPriority().cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            delay(2000)
            _shouldShowSplashScreen.value = false
        }
        getAllNotes()
        readSortState()
    }

    private fun getAllNotes() {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                notesRepository.getAllNotes().cachedIn(viewModelScope).collect { pagingData ->
                    Log.d("Notes", pagingData.toString())
                    _allNotes.value = pagingData
                }
            }
        } catch (e: Exception) {
            Log.d("Notes", e.toString())
            _allNotes.value = PagingData.empty()
        }
    }

    fun searchNotes() {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                notesRepository.searchNotes(searchQuery = "%${searchTextState.value}%")
                    .cachedIn(viewModelScope).collect { pagingData ->
                        _searchedNotes.value = pagingData
                    }
            }
        } catch (e: Exception) {
            _searchedNotes.value = PagingData.empty()
        }
        searchAppBarState.value = SearchAppBarState.TRIGGERED
    }

    fun getSelectedNote(noteId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            notesRepository.getSelectedNote(noteId = noteId).collect { note ->
                _selectedNote.value = note
            }
        }
    }

    fun updateNoteFields(selectedNote: Note?) {
        if (selectedNote != null) {
            id.value = selectedNote.id
            title.value = selectedNote.title
            description.value = selectedNote.description
            priority.value = selectedNote.priority
            reminderDateTime.value = selectedNote.reminderDateTime
            workerRequestId.value = selectedNote.workerRequestId
            createdAt.value = selectedNote.createdAt
            updatedAt.value = selectedNote.updatedAt
        } else {
            id.value = 0
            title.value = ""
            description.value = ""
            priority.value = Priority.LOW
            reminderDateTime.value = null
            workerRequestId.value = null
            createdAt.value = Date()
            updatedAt.value = Date()
        }
    }

    fun validateNoteFields(): Boolean {
        return title.value.isNotEmpty() && description.value.isNotEmpty()
    }

    fun handleDatabaseAction(action: Action) {
        when (action) {
            Action.ADD -> {
                addNote()
            }
            Action.UPDATE -> {
                updateNote()
            }
            Action.DELETE -> {
                deleteNote()
            }
            Action.DELETE_ALL -> {
                deleteAllNotes()
            }
            Action.UNDO -> {
                addNote()
            }
            else -> {
            }
        }
    }

    private fun addNote() {
        viewModelScope.launch(Dispatchers.IO) {
            createOrUpdateWorkerForNotesReminder()
            val note = Note(
                title = title.value,
                description = description.value,
                priority = priority.value,
                reminderDateTime = reminderDateTime.value,
                workerRequestId = workerRequestId.value,
                createdAt = Calendar.getInstance().time,
                updatedAt = Calendar.getInstance().time
            )

            notesRepository.addNote(note)
        }
    }

    private fun updateNote() {
        viewModelScope.launch(Dispatchers.IO) {
            createOrUpdateWorkerForNotesReminder()
            val note = Note(
                id = id.value,
                title = title.value,
                description = description.value,
                priority = priority.value,
                reminderDateTime = reminderDateTime.value,
                workerRequestId = workerRequestId.value,
                createdAt = createdAt.value,
                updatedAt = Date()
            )

            notesRepository.updateNote(note)
        }
    }

    private fun deleteNote() {
        viewModelScope.launch(Dispatchers.IO) {
            if (workerRequestId.value != null) {
                cancelNoteReminderWorkerById(workerRequestId.value!!)
            }
            val note = Note(
                id = id.value,
                title = title.value,
                description = description.value,
                priority = priority.value,
                reminderDateTime = reminderDateTime.value,
                workerRequestId = workerRequestId.value,
                createdAt = createdAt.value,
                updatedAt = updatedAt.value
            )

            notesRepository.deleteNote(note)
        }
    }

    private fun deleteAllNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            cancelAllNoteReminderWorkers()
            notesRepository.deleteAllNotes()
        }
    }

    fun persistSortState(priority: Priority) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.persistSortState(priority = priority)
        }
    }

    private fun readSortState() {
        _sortState.value = RequestState.Loading
        try {
            viewModelScope.launch(Dispatchers.IO) {
                dataStoreRepository.readSortState.map { Priority.valueOf(it) }.collect { priority ->
                    Log.d("Notes_LOG", priority.name)
                    _sortState.value = RequestState.Success(data = priority)
                }
            }
        } catch (e: Exception) {
            _sortState.value = RequestState.Error<Exception>(e)
        }
    }


    private fun cancelAllNoteReminderWorkers() {
        workManager.cancelAllWork()
    }

    private fun cancelNoteReminderWorkerById(id: UUID) {
        workManager.cancelWorkById(workerRequestId.value!!)
    }

    private fun createOrUpdateWorkerForNotesReminder() {
        if (reminderDateTime.value != null) {

            // if worker is already Scheduled then cancel it
            if (workerRequestId.value != null) {
                cancelNoteReminderWorkerById(workerRequestId.value!!)
            }

            val currentDateTime = Date()

            val delayInSeconds =
                (reminderDateTime.value!!.time / 1000L) - (currentDateTime.time / 1000L)

            createWorkRequest(message = title.value, timeDelayInSeconds = delayInSeconds)
        }
    }

    private fun createWorkRequest(message: String, timeDelayInSeconds: Long) {
        val myWorkRequest = OneTimeWorkRequestBuilder<NoteReminderWorker>()
            .setInitialDelay(timeDelayInSeconds, TimeUnit.SECONDS)
            .setInputData(
                workDataOf(
                    "title" to "Reminder",
                    "message" to message,
                )
            )
            .build()

        workerRequestId.value = myWorkRequest.id

        workManager.enqueue(myWorkRequest)
    }
}
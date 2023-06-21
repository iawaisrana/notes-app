package com.example.notes.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.notes.data.NotesDao
import com.example.notes.data.models.Note
import com.example.notes.utils.Constants.NOTE_PAGE_SIZE
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class NotesRepository @Inject constructor(private val notesDao: NotesDao) {
    fun getAllNotes(): Flow<PagingData<Note>> {
        val pagingSourceFactory = { notesDao.getAllNotes() }
        return Pager(
            PagingConfig(
                pageSize = NOTE_PAGE_SIZE
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun getSelectedNote(noteId: Int): Flow<Note> {
        return notesDao.getSelectedNote(noteId = noteId)
    }

    suspend fun addNote(note: Note) {
        notesDao.addNote(note = note);
    }

    suspend fun updateNote(note: Note) {
        notesDao.updateNote(note = note);
    }

    suspend fun deleteNote(note: Note) {
        notesDao.deleteNote(note = note);
    }

    suspend fun deleteAllNotes() {
        notesDao.deleteAllNotes();
    }

    fun searchNotes(searchQuery: String): Flow<PagingData<Note>> {
        val pagingSourceFactory = { notesDao.searchNotes(searchQuery = searchQuery) }
        return Pager(
            PagingConfig(
                pageSize = NOTE_PAGE_SIZE
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun sortByLowPriority(): Flow<PagingData<Note>> {
        val pagingSourceFactory = { notesDao.sortByLowPriority() }
        return Pager(
            PagingConfig(
                pageSize = NOTE_PAGE_SIZE
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun sortByHighPriority(): Flow<PagingData<Note>> {
        val pagingSourceFactory = { notesDao.sortByHighPriority() }
        return Pager(
            PagingConfig(
                pageSize = NOTE_PAGE_SIZE
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
}
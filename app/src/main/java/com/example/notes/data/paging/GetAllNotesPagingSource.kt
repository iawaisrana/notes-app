//package com.example.notes.data.paging
//
//import androidx.paging.PagingSource
//import androidx.paging.PagingState
//import com.example.notes.data.models.Note
//import com.example.notes.data.repositories.NotesRepository
//import javax.inject.Inject
//
//class GetAllNotesPagingSource @Inject constructor(
//    private val notesRepository: NotesRepository
//) : PagingSource<Int, Note>() {
//    override fun getRefreshKey(state: PagingState<Int, Note>): Int? {
//        return state.anchorPosition?.let { anchorPosition ->
//            val anchorPage = state.closestPageToPosition(anchorPosition)
//            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
//        }
//    }
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Note> {
//        val page = params.key ?: 1
//        return try {
//            val response = notesRepository.getAllNotes(
//                limit = params.loadSize,
//                offset = page * params.loadSize
//            )
//
//            if (response.isNotEmpty()) {
//                return LoadResult.Page(
//                    data = response,
//                    prevKey = if (page == 0) null else page - 1,
//                    nextKey = if (response.isEmpty()) null else page + 1
//                )
//            } else {
//                return LoadResult.Page(
//                    data = emptyList(),
//                    prevKey = null,
//                    nextKey = null
//                )
//            }
//        } catch (e: Exception) {
//            LoadResult.Error(e)
//        }
//    }
//}
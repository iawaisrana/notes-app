package com.example.notes.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.notes.service.NotificationService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NoteReminderWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        NotificationService.showNotification(
            context = context,
            title = inputData.getString("title").toString(),
            message = inputData.getString("message").toString()
        )
        return Result.success()
    }
}
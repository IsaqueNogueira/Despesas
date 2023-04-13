package com.isaquesoft.despesas

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.presentation.ui.activity.MainActivity
import java.text.SimpleDateFormat
import java.util.*

class DailyNotificationJobService(private val expenses: List<Expense>) : JobService() {
    override fun onStopJob(params: JobParameters?): Boolean {
        // Called if the job is cancelled before it can finish
        return true // Retry the job
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(TAG, "Daily notification job started")

        val expenses = expenses

        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val date = SimpleDateFormat("dd/MM/yyyy").parse(today)
        val calendar = Calendar.getInstance()
        calendar.time = date
        val expensesToday = expenses.filter { it.date == calendar.timeInMillis }

        if (expensesToday.isNotEmpty()) {
            createNotification(expensesToday.size)
        }

        // Reschedule the job to run again tomorrow
        scheduleJob(applicationContext)

        return true // The job is still running
    }

    private fun createNotification(expensesCount: Int) {
        val channelId = "MyChannelId"
        val channelName = "MyChannelName"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 1
        val notificationTitle = "Despesas vencidas hoje"
        val notificationText = "$expensesCount despesa(s) venceu(ram) hoje."
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.baseline_backup)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    companion object {
        private const val TAG = "DailyNotificationJobService"
        private const val JOB_ID = 1000

        fun scheduleJob(context: Context) {
            val jobScheduler =
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val jobInfo = JobInfo.Builder(
                JOB_ID,
                ComponentName(context, DailyNotificationJobService::class.java),
            )
                .setPersisted(true) // The job should survive device reboots
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) // Require any network connection
                .setRequiresDeviceIdle(false) // Don't require the device to be idle
                .setRequiresCharging(false) // Don't require the device to be
                .setPeriodic(24 * 60 * 60 * 1000) // 24 hours
                .setPersisted(true)
                .build()
            jobScheduler.schedule(jobInfo)
        }

        fun cancelJob(context: Context) {
            val jobScheduler =
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.cancel(JOB_ID)
        }
    }
}

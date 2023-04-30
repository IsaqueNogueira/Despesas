package com.isaquesoft.despesas.receiver

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.database.AppDatabase
import com.isaquesoft.despesas.presentation.ui.activity.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

private const val VENCIMENTO = "Vencimento"
private val expensesNotification = mutableListOf<Expense>()

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val db = AppDatabase.getDatabase(context)

        CoroutineScope(Dispatchers.Default).launch {
            val expenses = db.expenseDao().getExpenses()
            expenses.forEach { expense ->
                val hoje = Calendar.getInstance()
                val vencimento = Calendar.getInstance()
                vencimento.timeInMillis = expense.date
                if (vencimento.get(Calendar.YEAR) == hoje.get(Calendar.YEAR) &&
                    vencimento.get(Calendar.DAY_OF_YEAR) == hoje.get(Calendar.DAY_OF_YEAR) &&
                    expense.paidOut == false
                ) {
                    expensesNotification.add(expense)
                }
            }

            if (expensesNotification.isNotEmpty()) {
                val titulo = context.getString(R.string.titlenotification)
                val mensagem =
                    context.getString(R.string.expense_due_today)

                withContext(Dispatchers.Main) {
                    showNotificationWithIntent(
                        context,
                        titulo,
                        mensagem,
                        expenses[0].id,
                        MainActivity::class.java,
                    )
                }
            }
        }
    }

    fun showNotificationWithIntent(
        context: Context,
        titulo: String?,
        mensagem: String?,
        id: Int,
        activityClass: Class<out Activity>,
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "my_channel_id"
        val channelName = VENCIMENTO
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, activityClass).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
            PendingIntent.FLAG_IMMUTABLE, // Only use FLAG_IMMUTABLE or FLAG_MUTABLE for S+ targeting
        )
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(titulo)
            .setContentText(mensagem)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(id, notification)
    }
}

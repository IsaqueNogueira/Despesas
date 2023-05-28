package com.isaquesoft.despesas

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.isaquesoft.despesas.receiver.AlarmReceiver
import java.util.Calendar

class AlarmService : Service() {
    private lateinit var alarmManager: AlarmManager
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        startAlarm()
    }

    private fun startAlarm() {
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

// Defina a hora em que deseja que a notificação seja enviada (por exemplo, às 9h da manhã)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, 7)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

// Defina o intervalo de repetição diária
        val intervalMillis = AlarmManager.INTERVAL_DAY

// Agende o BroadcastReceiver para ser chamado diariamente no horário especificado
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            intervalMillis,
            pendingIntent,
        )
    }
}

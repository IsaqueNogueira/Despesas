package com.isaquesoft.despesas

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.isaquesoft.despesas.receiver.AlarmReceiver
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

class AlarmService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
       // setAlarmManager()
        setDailyNotification()
    }
    private fun setAlarmManager() {
        val timeInterval = TimeUnit.DAYS.toMillis(1)
        val intent = Intent(this, AlarmReceiver::class.java)
        val sender = PendingIntent.getBroadcast(this, 2, intent, PendingIntent.FLAG_IMMUTABLE)
        val am = getSystemService(ALARM_SERVICE) as AlarmManager
        var l = Date().time
        if (l < Date().time) l += timeInterval
        am.setRepeating(AlarmManager.RTC_WAKEUP, l, timeInterval, sender)
    }

    private fun setDailyNotification() {
        // Define a hora em que a notificação deve ser disparada pela primeira vez
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 7)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        // Se a hora atual já passou, agende o alarme para o próximo dia
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        // Cria um Intent que será usado para iniciar a exibição da notificação
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            2,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        // Obtém uma referência ao serviço AlarmManager e configura o alarme para disparar todos os dias
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent,
        )
    }
}

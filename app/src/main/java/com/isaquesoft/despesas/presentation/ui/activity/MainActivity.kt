package com.isaquesoft.despesas.presentation.ui.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.isaquesoft.despesas.databinding.ActivityMainBinding
import com.isaquesoft.despesas.presentation.ui.viewmodel.EstadoAppViewModel
import com.isaquesoft.despesas.receiver.AlarmReceiver
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val estadoAppViewModel: EstadoAppViewModel by viewModel()
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setAlarmManager()
        estadoAppViewModel.componentes.observe(
            this,
            Observer {
                it?.let {
                    if (it.appBar) {
                        supportActionBar?.show()
                    } else {
                        supportActionBar?.hide()
                    }
                    if (it.temBotaoBack) {
                        supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    } else {
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    }
                }
            },
        )
    }

    private fun setAlarmManager() {
        val calendar = Calendar.getInstance()
        val timeInterval = TimeUnit.DAYS.toMillis(1)
        val intent = Intent(this, AlarmReceiver::class.java)
        val sender = PendingIntent.getBroadcast(this, 2, intent, PendingIntent.FLAG_IMMUTABLE)
        val am = getSystemService(ALARM_SERVICE) as AlarmManager
        var l = Date().time
        if (l < Date().time) l += timeInterval
        am.setRepeating(AlarmManager.RTC_WAKEUP, l, timeInterval, sender)
    }
}

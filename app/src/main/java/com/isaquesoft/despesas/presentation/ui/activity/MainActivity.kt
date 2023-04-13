package com.isaquesoft.despesas.presentation.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.isaquesoft.despesas.DailyNotificationJobService
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.repository.ExpenseRepository
import com.isaquesoft.despesas.presentation.ui.viewmodel.EstadoAppViewModel
import com.isaquesoft.despesas.presentation.ui.viewmodel.MainActivityViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val estadoAppViewModel: EstadoAppViewModel by viewModel()
    private val viewModel: MainActivityViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

//        GlobalScope.launch {
//            val expenseRepository = viewModel.getExpenses()
//            val constructor = DailyNotificationJobService::class.java.getDeclaredConstructor(ExpenseRepository::class.java)
//            val jobService = constructor.newInstance(expenseRepository)
//            jobService.scheduleJob(this@MainActivity)
//        }

    }
}

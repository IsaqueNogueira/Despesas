package com.isaquesoft.despesas.di.modules

import com.isaquesoft.despesas.data.repository.ExpenseRepository
import com.isaquesoft.despesas.database.AppDatabase
import com.isaquesoft.despesas.presentation.ui.viewmodel.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModules = module {

    single {
        AppDatabase.getDatabase(get()).expenseDao()
    }

    single { ExpenseRepository(get()) }

    factory { MainActivityViewModel(get()) }

    viewModel {
        EstadoAppViewModel()
    }

    viewModel {
        ExpenseFragmentViewModel(get())
    }

    viewModel {
        NewExpenseFragmentViewModel(get())
    }

    viewModel {
        ExpenseDetailsFramentViewModel(get())
    }

    viewModel {
        ResumeGraphicExpenseViewModel(get())
    }

    viewModel { CategoryFragmentViewModel(get()) }

    viewModel { BackupFragmentViewModel(get()) }
}

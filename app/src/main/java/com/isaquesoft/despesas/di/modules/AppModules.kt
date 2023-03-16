package com.isaquesoft.despesas.di.modules

import com.isaquesoft.despesas.data.repository.ExpenseRepository
import com.isaquesoft.despesas.database.AppDatabase
import com.isaquesoft.despesas.presentation.ui.viewmodel.EstadoAppViewModel
import com.isaquesoft.despesas.presentation.ui.viewmodel.ExpenseFragmentViewModel
import com.isaquesoft.despesas.presentation.ui.viewmodel.NewExpenseFragmentViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModules = module {

    single {
        AppDatabase.getDatabase(get()).expenseDao()
    }

    single { ExpenseRepository(get()) }

    viewModel {
        EstadoAppViewModel()
    }

    viewModel {
        ExpenseFragmentViewModel(get())
    }

    viewModel {
        NewExpenseFragmentViewModel(get())
    }
}

package com.isaquesoft.despesas.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.data.repository.ExpenseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivityViewModel(private val expenseRepository: ExpenseRepository) : ViewModel() {

    suspend fun getExpenses(): List<Expense> {
        return withContext(Dispatchers.IO) {
            expenseRepository.getExpenses()
        }
    }
}

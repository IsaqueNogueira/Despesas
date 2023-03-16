package com.isaquesoft.despesas.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.data.repository.ExpenseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewExpenseFragmentViewModel(private val expenseRepository: ExpenseRepository) : ViewModel() {

    fun insertExpense(expense: Expense) {
        viewModelScope.launch(Dispatchers.IO) {
            expenseRepository.insertExpense(expense)
        }
    }
}

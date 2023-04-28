package com.isaquesoft.despesas.presentation.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaquesoft.despesas.data.repository.ExpenseRepository
import com.isaquesoft.despesas.presentation.state.ExpenseResumeState
import kotlinx.coroutines.launch

class ResumeGraphicExpenseViewModel(private val expenseRepository: ExpenseRepository) :
    ViewModel() {

    private val _expenseState by lazy { MutableLiveData<ExpenseResumeState>() }
    val expenseState: LiveData<ExpenseResumeState>
        get() = _expenseState

    fun getExpenses() {
        viewModelScope.launch {
            val expenses = expenseRepository.getAllExpensesResume()

            _expenseState.postValue(ExpenseResumeState.ShowExpenses(expenses))
        }
    }
}

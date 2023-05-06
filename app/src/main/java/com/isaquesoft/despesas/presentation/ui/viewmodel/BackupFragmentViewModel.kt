package com.isaquesoft.despesas.presentation.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.data.repository.ExpenseRepository
import com.isaquesoft.despesas.presentation.state.BackupState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BackupFragmentViewModel(private val expenseRepository: ExpenseRepository) : ViewModel() {

    private val _backupState by lazy { MutableLiveData<BackupState>() }
    val backupState: LiveData<BackupState>
        get() = _backupState

    fun getAllExpenses() {
        viewModelScope.launch(Dispatchers.IO) {
            val expenses = expenseRepository.getExpenses()
            _backupState.postValue(BackupState.ShowExpenses(expenses))
        }
    }

    fun insertAllExpenses(expenses: List<Expense>) {
        viewModelScope.launch(Dispatchers.IO) {
            expenseRepository.insertAllExpenses(expenses)
        }
    }

    fun insertExpense(expense: Expense){
        viewModelScope.launch(Dispatchers.IO) {
            expenseRepository.insertExpense(expense)
        }
    }
}

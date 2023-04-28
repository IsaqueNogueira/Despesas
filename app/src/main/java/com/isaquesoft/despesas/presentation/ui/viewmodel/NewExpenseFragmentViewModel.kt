package com.isaquesoft.despesas.presentation.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.data.repository.ExpenseRepository
import com.isaquesoft.despesas.presentation.state.NewExpenseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewExpenseFragmentViewModel(private val expenseRepository: ExpenseRepository) : ViewModel() {

    private val _newExpenseState by lazy { MutableLiveData<NewExpenseState>() }
    val expenseState: LiveData<NewExpenseState>
        get() = _newExpenseState

    fun insertExpense(expense: Expense) {
        viewModelScope.launch(Dispatchers.IO) {
            expenseRepository.insertExpense(expense)
        }
    }

    fun getAllCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            val category = expenseRepository.getAllCategory()
            _newExpenseState.postValue(NewExpenseState.ShowAllCategory(category))
        }
    }
}

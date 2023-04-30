package com.isaquesoft.despesas.presentation.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.data.repository.ExpenseRepository
import com.isaquesoft.despesas.presentation.state.ExpenseDetailsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExpenseDetailsFramentViewModel(val expenseRepository: ExpenseRepository) : ViewModel() {

    private val _expenseDetailsState by lazy { MutableLiveData<ExpenseDetailsState>() }
    val expenseDetailsState: LiveData<ExpenseDetailsState>
        get() = _expenseDetailsState
    fun getExpenseRepeat(dateCreated: Long, value: String, repeat: Boolean, installments: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val expensesRepeat = expenseRepository.getExpenseRepeat(dateCreated, value, repeat, installments)
            _expenseDetailsState.postValue(ExpenseDetailsState.ShowExpenseRepeat(expensesRepeat))
        }
    }

}

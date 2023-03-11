package com.isaquesoft.despesas.presentation.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.data.repository.ExpenseRepository
import com.isaquesoft.despesas.presentation.state.ExpenseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class ExpenseFragmentViewModel(private val expenseRepository: ExpenseRepository) : ViewModel() {

    private val calendar = Calendar.getInstance()
    private val dateFormatter = java.text.SimpleDateFormat("MMMM/yyyy", Locale.getDefault()).apply {
        applyPattern("MMMM/yyyy")
        isLenient = false
    }

    private val _expenseState by lazy { MutableLiveData<ExpenseState>() }
    val expenseState: LiveData<ExpenseState>
        get() = _expenseState

    fun clickPrevButton() {
        calendar.add(Calendar.MONTH, -1)
        val month = dateFormatter.format(calendar.time)
        val formatMonth = month.capitalize()
        _expenseState.postValue(ExpenseState.DateText(formatMonth))
    }

    fun clickNextButton() {
        calendar.add(Calendar.MONTH, 1)
        val month = dateFormatter.format(calendar.time)
        val formatMonth = month.capitalize()
        _expenseState.postValue(ExpenseState.DateText(formatMonth))
    }

    fun getAllExpense(expense: Expense) {
        GlobalScope.launch(Dispatchers.IO) {
            val listExpense = expenseRepository.getAllExpense()
            _expenseState.postValue(ExpenseState.ShowExpenses(listExpense))
        }
    }

    fun deleteExpense(expense: Expense) {
        GlobalScope.launch(Dispatchers.IO) {
            expenseRepository.deleteExpense(expense)
        }
    }

    fun deleteAllExpense(expense: List<Expense>) {
        GlobalScope.launch(Dispatchers.IO) {
            expenseRepository.deleteAllExpense(expense)
        }
    }
}

package com.isaquesoft.despesas.presentation.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaquesoft.despesas.data.repository.ExpenseRepository
import com.isaquesoft.despesas.presentation.state.ExpenseResumeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar

class ResumeGraphicExpenseViewModel(private val expenseRepository: ExpenseRepository) :
    ViewModel() {

    private val _expenseState by lazy { MutableLiveData<ExpenseResumeState>() }
    val expenseState: LiveData<ExpenseResumeState>
        get() = _expenseState

    fun getFirstAndLastDayOfMonth(calendar: Calendar): ArrayList<Long> {
        val localDate =
            LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, 1)
        val firstDayOfMonth = localDate.toEpochDay() * 86400000
        val lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth)
        val lastDayOfMonthMillis = calendar.timeInMillis
        return arrayListOf(firstDayOfMonth, lastDayOfMonthMillis)
    }

    fun getAllExpense(minDate: Long, maxDate: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val expenses = expenseRepository.getAllExpense(minDate, maxDate)
            _expenseState.postValue(ExpenseResumeState.ShowExpenses(expenses))
        }
    }
}

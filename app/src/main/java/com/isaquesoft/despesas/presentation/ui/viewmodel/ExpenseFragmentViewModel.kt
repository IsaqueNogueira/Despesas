package com.isaquesoft.despesas.presentation.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.data.repository.ExpenseRepository
import com.isaquesoft.despesas.presentation.state.ExpenseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class ExpenseFragmentViewModel(private val expenseRepository: ExpenseRepository) : ViewModel() {

    private val calendar = Calendar.getInstance()
    private val _expenseState by lazy { MutableLiveData<ExpenseState>() }
    val expenseState: LiveData<ExpenseState>
        get() = _expenseState

    fun clickPrevButton() {
        calendar.add(Calendar.MONTH, -1)
        _expenseState.postValue(ExpenseState.DateText(calendar))
    }

    fun clickNextButton() {
        calendar.add(Calendar.MONTH, 1)
        _expenseState.postValue(ExpenseState.DateText(calendar))
    }

    fun getAllExpense(minDate: Long, maxDate: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val listExpense = expenseRepository.getAllExpense(minDate, maxDate)
            _expenseState.postValue(ExpenseState.ShowExpenses(listExpense))
        }
    }

    fun getFirstAndLastDayOfMonth(calendar: Calendar): ArrayList<Long> {
        val localDate = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, 1)
        val firstDayOfMonth = localDate.toEpochDay() * 86400000
        val lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth)
        val lastDayOfMonthMillis = calendar.timeInMillis
        _expenseState.postValue(ExpenseState.MinDateMaxDate(firstDayOfMonth, lastDayOfMonthMillis))
        return arrayListOf(firstDayOfMonth, lastDayOfMonthMillis)
    }

    fun fullExpenseSum(expenses: List<Expense>) {
        var totalValue = BigDecimal.ZERO
        var totalBalance = BigDecimal.ZERO
        val regex = Regex("""R\$\s*(\d{1,3}(?:\.\d{3})*(?:,\d{2}))""")
        for (expense in expenses) {
            val matchResult = regex.find(expense.value)
            if (matchResult != null) {
                val valueString = matchResult.groupValues[1].replace(".", "").replace(",", ".")
                val value = BigDecimal(valueString)
                totalValue += value

                if (expense.paidOut == false) {
                    totalBalance += value
                }
            }
        }
        val formattedFullValue = "R$ %.2f".format(totalValue)
        val formattedBalance = "R$ %.2f".format(totalBalance)
        _expenseState.postValue(ExpenseState.ShowValueEndBalance(formattedFullValue, formattedBalance))
    }

    fun deleteAllExpense(expense: List<Expense>) {
        viewModelScope.launch(Dispatchers.IO) {
            expenseRepository.deleteAllExpense(expense)
        }
    }
}

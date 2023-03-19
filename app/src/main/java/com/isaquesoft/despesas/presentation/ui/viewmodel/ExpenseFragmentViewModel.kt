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
import java.text.NumberFormat
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
        val localDate =
            LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, 1)
        val firstDayOfMonth = localDate.toEpochDay() * 86400000
        val lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, lastDayOfMonth)
        val lastDayOfMonthMillis = calendar.timeInMillis
        _expenseState.postValue(ExpenseState.MinDateMaxDate(firstDayOfMonth, lastDayOfMonthMillis))
        return arrayListOf(firstDayOfMonth, lastDayOfMonthMillis)
    }

    fun fullExpenseSum(expenses: List<Expense>) {
        val deviceLocale = Locale.getDefault()
        val currencySymbol = Currency.getInstance(deviceLocale).symbol
        var totalValue = BigDecimal.ZERO
        var totalBalance = BigDecimal.ZERO
        for (expense in expenses) {
            val originalString = expense.value
            val onlyLetters = originalString.replace(Regex("[\\d.,\\s]"), "")
            if (onlyLetters != currencySymbol) {
            } else {
                val valueString = expense.value.replace(currencySymbol, "").trim().replace(",", ".")
                val lastDotIndex = valueString.lastIndexOf('.')
                val formattedValueString = if (lastDotIndex >= 0) {
                    valueString.substring(0, lastDotIndex).replace(".", "") + valueString.substring(
                        lastDotIndex,
                    )
                } else {
                    valueString
                }
                val value = BigDecimal(formattedValueString)
                totalValue += value

                if (expense.paidOut == false) {
                    totalBalance += value
                }
            }
        }
        val numberFormat = NumberFormat.getCurrencyInstance(deviceLocale)
        val formattedFullValue = numberFormat.format(totalValue)
        val formattedBalance = numberFormat.format(totalBalance)
        _expenseState.postValue(
            ExpenseState.ShowValueEndBalance(
                formattedFullValue,
                formattedBalance,
            ),
        )
    }

    fun deleteAllExpense(expense: List<Expense>) {
        viewModelScope.launch(Dispatchers.IO) {
            expenseRepository.deleteAllExpense(expense)
        }
    }
}

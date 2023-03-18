package com.isaquesoft.despesas.presentation.state

import com.isaquesoft.despesas.data.model.Expense
import java.util.*

sealed class ExpenseState {
    data class DateText(val calendar: Calendar) : ExpenseState()
    data class ShowExpenses(val expense: List<Expense>) : ExpenseState()
    data class MinDateMaxDate(val minDate: Long, val maxDate: Long) : ExpenseState()
    data class ShowValueEndBalance(val fullValue: String, val fullBalance: String) : ExpenseState()
}

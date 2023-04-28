package com.isaquesoft.despesas.presentation.state

import com.isaquesoft.despesas.data.model.Category
import com.isaquesoft.despesas.data.model.Expense
import java.util.*

sealed class ExpenseState {
    data class DateText(val calendar: Calendar) : ExpenseState()
    data class ShowExpenses(val expense: List<Expense>) : ExpenseState()
    data class ShowValueEndBalance(val fullValue: String, val fullBalance: String) : ExpenseState()

    data class ShowAllCategory(val category: List<Category>) : ExpenseState()
}

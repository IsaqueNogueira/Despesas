package com.isaquesoft.despesas.presentation.state

import com.isaquesoft.despesas.data.model.Expense

sealed class ExpenseState {
    data class DateText(val month: String) : ExpenseState()
    data class ShowExpenses(val expense: List<Expense>) : ExpenseState()
}

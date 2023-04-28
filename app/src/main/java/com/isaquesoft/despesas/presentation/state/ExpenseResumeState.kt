package com.isaquesoft.despesas.presentation.state

import com.isaquesoft.despesas.data.model.Expense

sealed class ExpenseResumeState {
    data class ShowExpenses(val expenses: List<Expense>) : ExpenseResumeState()
}

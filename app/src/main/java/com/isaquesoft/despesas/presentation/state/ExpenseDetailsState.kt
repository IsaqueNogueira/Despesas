package com.isaquesoft.despesas.presentation.state

import com.isaquesoft.despesas.data.model.Expense

sealed class ExpenseDetailsState {

    data class ShowExpenseRepeat(val expensesRepeat: List<Expense>) : ExpenseDetailsState()
    data class ShowExpenseRepeatItNext(val expensesRepeatItNext: List<Expense>) : ExpenseDetailsState()
}

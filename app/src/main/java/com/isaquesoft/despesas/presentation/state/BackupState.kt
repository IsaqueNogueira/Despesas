package com.isaquesoft.despesas.presentation.state

import com.isaquesoft.despesas.data.model.Expense

sealed class BackupState {

    data class ShowExpenses(val expenses: List<Expense>) : BackupState()
}
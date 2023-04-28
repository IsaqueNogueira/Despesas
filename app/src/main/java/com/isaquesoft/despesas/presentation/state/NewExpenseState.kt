package com.isaquesoft.despesas.presentation.state

import com.isaquesoft.despesas.data.model.Category

sealed class NewExpenseState {
    data class ShowAllCategory(val category: List<Category>) : NewExpenseState()
}

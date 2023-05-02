package com.isaquesoft.despesas.presentation.state

import com.isaquesoft.despesas.data.model.Category

sealed class CategoryState {
    data class ShowCategory(val category: List<Category>) : CategoryState()
}

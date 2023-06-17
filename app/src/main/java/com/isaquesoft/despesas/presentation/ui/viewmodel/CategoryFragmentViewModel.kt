package com.isaquesoft.despesas.presentation.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isaquesoft.despesas.data.model.Category
import com.isaquesoft.despesas.data.repository.ExpenseRepository
import com.isaquesoft.despesas.presentation.state.CategoryState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryFragmentViewModel(private val expenseRepository: ExpenseRepository) : ViewModel() {

    private val _categoryState by lazy { MutableLiveData<CategoryState>() }
    val categoryState: LiveData<CategoryState>
        get() = _categoryState

    fun getAllCategory() {
        viewModelScope.launch(Dispatchers.IO) {
            val category = expenseRepository.getAllCategory()
            _categoryState.postValue(CategoryState.ShowCategory(category))
        }
    }

    fun newCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            expenseRepository.insertCategory(category)
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            expenseRepository.updateCategory(category)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            expenseRepository.deleteCategory(category)
        }
    }
}

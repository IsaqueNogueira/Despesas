package com.isaquesoft.despesas.data.repository

import com.isaquesoft.despesas.data.model.Category
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.database.dao.ExpenseDao

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    suspend fun insertExpense(expense: Expense) = expenseDao.insertExpense(expense)

    suspend fun insertAllExpenses(expenses: List<Expense>) = expenseDao.insertAllExpenses(expenses)

    suspend fun getExpense(id: Int) = expenseDao.getExpense(id)

    suspend fun getAllExpensesResume() = expenseDao.getAllExpensesResume()

    suspend fun getAllExpense(minDate: Long, maxDate: Long) = expenseDao.getAllExpense(minDate, maxDate)

    suspend fun getAllExpenseByDescriptionAsc(minDate: Long, maxDate: Long) = expenseDao.getAllExpenseByDescriptionAsc(minDate, maxDate)

    suspend fun getAllExpenseDateDesc(minDate: Long, maxDate: Long) = expenseDao.getAllExpenseDateDesc(minDate, maxDate)

    suspend fun getAllExpenseDateCres(minDate: Long, maxDate: Long) = expenseDao.getAllExpenseDateCres(minDate, maxDate)

    suspend fun getAllExpenseFilterCategory(minDate: Long, maxDate: Long, category: String) = expenseDao.getAllExpenseFilterCategory(minDate, maxDate, category)

    suspend fun getExpenses() = expenseDao.getExpenses()

    suspend fun getExpenseRepeat(dateCreated: Long, value: String, repeat: Boolean, installments: Int) = expenseDao.getExpenseRepeat(dateCreated, value, repeat, installments)

    suspend fun getExpenseRepeatItNext(dateCreated: Long, value: String, repeat: Boolean, installments: Int, date: Long): List<Expense> = expenseDao.getExpenseRepeatItNext(dateCreated, value, repeat, installments, date)

    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)

    suspend fun deleteAllExpense(expense: List<Expense>) = expenseDao.deleteAllExpense(expense)

    suspend fun updateExpense(expense: Expense) = expenseDao.updateExpense(expense)

    suspend fun updateAllExpense(expense: List<Expense>) = expenseDao.updateAllExpense(expense)

    // Category

    suspend fun insertCategory(category: Category) = expenseDao.insertCategory(category)

    suspend fun insertAllCategory(category: List<Category>) = expenseDao.insertAllCategory(category)
    suspend fun getCategory(id: Int) = expenseDao.getCategory(id)

    suspend fun deleteCategory(category: Category) = expenseDao.deleteCategory(category)

    suspend fun getAllCategory() = expenseDao.getAllCategory()
    suspend fun updateCategory(category: Category) = expenseDao.updateCategory(category)
}

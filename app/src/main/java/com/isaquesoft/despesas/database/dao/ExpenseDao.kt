package com.isaquesoft.despesas.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.isaquesoft.despesas.data.model.Category
import com.isaquesoft.despesas.data.model.Expense

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM Expense")
    suspend fun getAllExpensesResume(): List<Expense>

    @Query("SELECT * FROM Expense WHERE id = :id")
    suspend fun getExpense(id: Int): Expense

    @Query("SELECT * FROM Expense WHERE date >= :minDate AND date <= :maxDate")
    suspend fun getAllExpense(minDate: Long, maxDate: Long): List<Expense>

    @Query("SELECT * FROM Expense WHERE date >= :minDate AND date <= :maxDate ORDER BY description ASC")
    suspend fun getAllExpenseByDescriptionAsc(minDate: Long, maxDate: Long): List<Expense>

    @Query("SELECT * FROM Expense WHERE date >= :minDate AND date <= :maxDate ORDER BY date DESC")
    suspend fun getAllExpenseDateDesc(minDate: Long, maxDate: Long): List<Expense>

    @Query("SELECT * FROM Expense WHERE date >= :minDate AND date <= :maxDate ORDER BY date ASC")
    suspend fun getAllExpenseDateCres(minDate: Long, maxDate: Long): List<Expense>

    @Query("SELECT * FROM Expense")
    suspend fun getExpenses(): List<Expense>

    @Query("SELECT * FROM Expense WHERE dateCreated = :dateCreated AND value = :value AND repeat = :repeat AND installments = :installments")
    suspend fun getExpenseRepeat(dateCreated: Long, value: String, repeat: Boolean, installments: Int): List<Expense>

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Delete
    suspend fun deleteAllExpense(expense: List<Expense>)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Update
    suspend fun updateAllExpense(expense: List<Expense>)

    // Category

    @Insert
    suspend fun insertCategory(category: Category)

    @Insert
    suspend fun insertAllCategory(category: List<Category>)

    @Query("SELECT * FROM Category WHERE id = :id")
    suspend fun getCategory(id: Int): Category

    @Query("SELECT * FROM Category")
    suspend fun getAllCategory(): List<Category>

    @Delete
    suspend fun deleteCategory(category: Category)
}

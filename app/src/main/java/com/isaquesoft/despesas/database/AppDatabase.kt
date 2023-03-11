package com.isaquesoft.despesas.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.database.dao.ExpenseDao

@Database(entities = [Expense::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao

    companion object {
        fun instance(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "expensedb",
            )
                .build()
        }
    }
}

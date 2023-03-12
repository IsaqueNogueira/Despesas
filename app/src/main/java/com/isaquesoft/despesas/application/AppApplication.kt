package com.isaquesoft.despesas.application

import android.app.Application
import androidx.room.Room
import com.isaquesoft.despesas.database.AppDatabase
import com.isaquesoft.despesas.di.modules.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AppApplication)
            modules(appModules)
        }
        Room.databaseBuilder(this, AppDatabase::class.java, "expensedb").build()
    }
}

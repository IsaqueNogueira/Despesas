package com.isaquesoft.despesas.presentation.ui

import android.content.Context
import android.content.SharedPreferences

class SharedPreferences(context: Context) {

    companion object {
        private const val PREFERENCE_NAME = "MyPreference"
        private const val KEY_USERNAME = "ordemList"
        private const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
    }

    private val mSharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun setOrdemList(ordem: String) {
        val editor = mSharedPreferences.edit()
        editor.putString(KEY_USERNAME, ordem)
        editor.apply()
    }

    fun getOrdemList(): String? {
        return mSharedPreferences.getString(KEY_USERNAME, null)
    }

    fun setNotificationEnabled(enabled: Boolean) {
        val editor = mSharedPreferences.edit()
        editor.putBoolean(KEY_NOTIFICATION_ENABLED, enabled)
        editor.apply()
    }

    fun isNotificationEnabled(): Boolean {
        return mSharedPreferences.getBoolean(KEY_NOTIFICATION_ENABLED, true)
    }
}

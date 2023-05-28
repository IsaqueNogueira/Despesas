package com.isaquesoft.despesas.presentation.ui

import android.content.Context
import android.content.SharedPreferences

class SharedPreferences(context: Context) {

    companion object {
        private const val PREFERENCE_NAME = "MyPreference"
        private const val KEY_USERNAME = "ordemList"
        private const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
        private const val NOTIFICATION_PERMISSION_DIALOG = "notification_dialog"
        private const val KEY_ANUNCIO_INTERSTICIAL_PRINCIPAL = "intersticial_principal"
        private const val KEY_COMPRA_PRO_REALIZADO = "verification_signature"
        private const val KEY_QUANTITY_USE_APP = "quantidade_de_vezes_que_entrou_no_app"
        private const val KEY_ANUNCIO_INSTERSTICIAL_ATUALIZA_DESPESA = "quantidade_de_atualização_das_despesas"
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

    fun setNotificationDialogShow(boolean: Boolean) {
        val editor = mSharedPreferences.edit()
        editor.putBoolean(NOTIFICATION_PERMISSION_DIALOG, boolean)
        editor.apply()
    }

    fun getNotificationDialogShow(): Boolean {
        return mSharedPreferences.getBoolean(NOTIFICATION_PERMISSION_DIALOG, false)
    }

    fun setNotificationEnabled(enabled: Boolean) {
        val editor = mSharedPreferences.edit()
        editor.putBoolean(KEY_NOTIFICATION_ENABLED, enabled)
        editor.apply()
    }

    fun isNotificationEnabled(): Boolean {
        return mSharedPreferences.getBoolean(KEY_NOTIFICATION_ENABLED, true)
    }

    fun setIntersticialPrincipalExecute(enabled: Boolean) {
        val editor = mSharedPreferences.edit()
        editor.putBoolean(KEY_ANUNCIO_INTERSTICIAL_PRINCIPAL, enabled)
        editor.apply()
    }

    fun getIntersticialPrincipalExecute(): Boolean {
        return mSharedPreferences.getBoolean(KEY_ANUNCIO_INTERSTICIAL_PRINCIPAL, false)
    }

    fun setVerifyCompraAssinatura(enabled: Boolean) {
        val editor = mSharedPreferences.edit()
        editor.putBoolean(KEY_COMPRA_PRO_REALIZADO, enabled)
        editor.apply()
    }
    fun getVerifyCompraAssinatura(): Boolean {
        return mSharedPreferences.getBoolean(KEY_COMPRA_PRO_REALIZADO, false)
    }

    fun setQuantityUseApp(quantity: Int) {
        val editor = mSharedPreferences.edit()
        editor.putInt(KEY_QUANTITY_USE_APP, quantity)
        editor.apply()
    }

    fun getQuantityUseApp(): Int {
        return mSharedPreferences.getInt(KEY_QUANTITY_USE_APP, 0)
    }

    fun setQuantityAtualizaDespesa(quantity: Int){
        val editor = mSharedPreferences.edit()
        editor.putInt(KEY_ANUNCIO_INSTERSTICIAL_ATUALIZA_DESPESA, quantity)
        editor.apply()
    }

    fun getQuantityAtualizaDespesa(): Int {
        return mSharedPreferences.getInt(KEY_ANUNCIO_INSTERSTICIAL_ATUALIZA_DESPESA, 0)
    }
}

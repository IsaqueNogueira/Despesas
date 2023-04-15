package com.isaquesoft.despesas.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.NumberFormat
import java.util.*

object CoinUtils {
    fun addCurrencyMask(editText: EditText) {
        val deviceLocale = editText.resources.configuration.locale
        val numberFormat = NumberFormat.getCurrencyInstance(deviceLocale)
        val defaultLocale = Locale.getDefault()

        val currency = Currency.getInstance(Locale.getDefault())
        val format = NumberFormat.getCurrencyInstance()
        format.currency = currency
        val amount = 0.00
        val coinTxt = format.format(amount)

        editText.setText(coinTxt)

        editText.addTextChangedListener(object : TextWatcher {
            private var current = ""

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int,
            ) {
                // Não é necessário implementar
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int,
            ) {
                if (s.toString() != current) {
                    editText.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("[^\\d]".toRegex(), "")
                    val parsed = cleanString.toDouble() / 100

                    val formatted = numberFormat.format(parsed)

                    current = formatted
                    editText.setText(formatted)
                    editText.setSelection(formatted.length)

                    editText.addTextChangedListener(this)
                }
            }
        })
    }
}

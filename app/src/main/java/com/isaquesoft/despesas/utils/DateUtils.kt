package com.isaquesoft.despesas.utils

import android.app.DatePickerDialog
import android.content.Context
import android.widget.EditText
import java.util.*

object DateUtils {

    fun exibirDatePickerDialog(context: Context, editText: EditText) {
        val calendario = Calendar.getInstance()
        val ano = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH)
        val dia = calendario.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(context, { _, year, month, dayOfMonth ->
            val dataSelecionada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
            editText.setText(dataSelecionada)
        }, ano, mes, dia)

        datePicker.show()
    }

}

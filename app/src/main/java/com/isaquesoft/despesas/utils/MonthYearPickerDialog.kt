package com.isaquesoft.despesas.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.NumberPicker
import com.isaquesoft.despesas.R
import java.text.DateFormatSymbols
import java.util.*

class MonthYearPickerDialog(
    context: Context,
    private val listener: (Int, Int) -> Unit,
) : Dialog(context) {

    private val calendar = Calendar.getInstance()
    private lateinit var monthPicker: NumberPicker
    private lateinit var yearPicker: NumberPicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_month_year_picker)
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )

        monthPicker = findViewById(R.id.monthPicker)
        yearPicker = findViewById(R.id.yearPicker)

        // Configura o picker do mês
        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.value = calendar.get(Calendar.MONTH) + 1
        monthPicker.displayedValues = DateFormatSymbols().months

        // Configura o picker do ano
        yearPicker.minValue = calendar.get(Calendar.YEAR) - 10
        yearPicker.maxValue = calendar.get(Calendar.YEAR) + 10
        yearPicker.value = calendar.get(Calendar.YEAR)

        // Botão OK
        findViewById<Button>(R.id.okButton).setOnClickListener {
            listener.invoke(monthPicker.value, yearPicker.value)
            dismiss()
        }
    }
}

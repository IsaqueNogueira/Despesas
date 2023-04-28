package com.isaquesoft.despesas.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.databinding.ResumeGraphicExpensesBinding
import com.isaquesoft.despesas.presentation.state.ExpenseResumeState
import com.isaquesoft.despesas.presentation.ui.viewmodel.ComponentesVisuais
import com.isaquesoft.despesas.presentation.ui.viewmodel.EstadoAppViewModel
import com.isaquesoft.despesas.presentation.ui.viewmodel.ResumeGraphicExpenseViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.BigDecimal
import java.text.DateFormatSymbols
import java.util.Currency
import java.util.Date
import java.util.Locale

class ResumeGraphicExpenses : Fragment() {

    private lateinit var binding: ResumeGraphicExpensesBinding
    private val controlation by lazy { findNavController() }
    private val estadoAppViewModel: EstadoAppViewModel by sharedViewModel()
    private val viewModel: ResumeGraphicExpenseViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = ResumeGraphicExpensesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = getString(R.string.resumo)
        estadoAppViewModel.temComponentes = ComponentesVisuais(true, false)
        goToExpenseFragment()
        goToNewExpenseFragment()
        viewModel.getExpenses()
        viewModel.expenseState.observe(viewLifecycleOwner) {
            when (it) {
                is ExpenseResumeState.ShowExpenses -> showExpenses(it.expenses)
            }
        }
    }

    private fun showExpenses(expenses: List<Expense>) {
        val monthlyExpenses = MutableList(12) { 0.0 }
        expenses.forEach { expense ->

            val deviceLocale = Locale.getDefault()
            val currencySymbol = Currency.getInstance(deviceLocale).symbol
            val originalString = expense.value
            val onlyLetters = originalString.replace(Regex("[\\d.,\\s]"), "")
            if (onlyLetters != currencySymbol) {
            } else {
                val valueString = expense.value.replace(currencySymbol, "").trim().replace(",", ".")
                val lastDotIndex = valueString.lastIndexOf('.')
                val formattedValueString = if (lastDotIndex >= 0) {
                    valueString.substring(0, lastDotIndex).replace(".", "") + valueString.substring(
                        lastDotIndex,
                    )
                } else {
                    valueString
                }
                val value = BigDecimal(formattedValueString)
                val month = Date(expense.date).month
                monthlyExpenses[month] += value.toDouble()
            }
        }

        val barChart = binding.barChart

        val entries = monthlyExpenses.mapIndexed { index, value ->
            BarEntry(index.toFloat(), value.toFloat())
        }

        val dataSet = BarDataSet(entries, "Gastos por mês")

        val data = BarData(dataSet)
        barChart.data = data

        barChart.setDrawValueAboveBar(true)
        barChart.description.isEnabled = false
        barChart.xAxis.apply {
            setDrawGridLines(false)
            labelCount = 12
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val month = DateFormatSymbols().months[value.toInt()]
                    return month.substring(0, 3)
                }
            }
        }

        barChart.axisRight.isEnabled = false
        barChart.axisLeft.apply {
            setDrawGridLines(false)
            axisMinimum = 0f
        }

        barChart.invalidate()
    }

    private fun goToExpenseFragment() {
        binding.expenseMenuBottomExpensesIcon.setOnClickListener {
            val direcition =
                ResumeGraphicExpensesDirections.actionResumeGraphicExpensesToExpenseFragment()
            controlation.navigate(direcition)
        }
    }

    private fun goToNewExpenseFragment() {
        binding.expenseFloating.setOnClickListener {
            val direction =
                ResumeGraphicExpensesDirections.actionResumeGraphicExpensesToNewExpenseFragment()
            controlation.navigate(direction)
        }
    }
}

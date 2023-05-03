package com.isaquesoft.despesas.presentation.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
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
import java.text.Normalizer
import java.util.Currency
import java.util.Locale

class ResumeGraphicExpenses : Fragment() {

    private lateinit var binding: ResumeGraphicExpensesBinding
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
        setHasOptionsMenu(true)
        estadoAppViewModel.temComponentes = ComponentesVisuais(true, true)
        viewModel.getExpenses()
        viewModel.expenseState.observe(viewLifecycleOwner) {
            when (it) {
                is ExpenseResumeState.ShowExpenses -> showExpenses(it.expenses)
            }
        }
    }

    private fun showExpenses(expenses: List<Expense>) {
        val categoryTotals: MutableMap<String, Float> = mutableMapOf()

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
                    valueString.substring(0, lastDotIndex).replace(".", "") + valueString.substring(lastDotIndex)
                } else {
                    valueString
                }
                val category = expense.category

                val value = BigDecimal(formattedValueString).toFloat()

                if (categoryTotals.containsKey(category)) {
                    categoryTotals[category] = categoryTotals[category]!! + value
                } else {
                    categoryTotals[category] = value
                }
            }
        }

        val entries: MutableList<PieEntry> = mutableListOf()
        val colors: MutableList<Int> = mutableListOf()
        val totalValue = categoryTotals.values.sum()

        categoryTotals.forEach { (category, value) ->
            val percent = value / totalValue * 100
            entries.add(PieEntry(percent, ""))
            val categoryExpenses = expenses.filter { it.category == category }
            categoryExpenses.forEach { expense ->
                colors.add(Color.parseColor(expense.corIcon))
            }
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(10f)
        data.setValueTextColor(Color.BLACK)

        val pieChart = binding.pieChart
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = false
        pieChart.data = data
        pieChart.invalidate()

        // Configurar o formato dos valores das porcentagens e a cor das linhas de conexão
        dataSet.valueLineColor = Color.BLACK
        dataSet.valueLinePart1OffsetPercentage = 90f
        dataSet.valueLinePart1Length = 0.5f
        dataSet.valueLinePart2Length = 0.2f
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        dataSet.valueTextColor = Color.BLACK
        dataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (activity != null) {
                    activity?.onBackPressed()
                }
            }
        }
        return true
    }
}

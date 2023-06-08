package com.isaquesoft.despesas.presentation.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.CategoryGraphic
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.databinding.ResumeGraphicExpensesBinding
import com.isaquesoft.despesas.presentation.state.ExpenseResumeState
import com.isaquesoft.despesas.presentation.ui.adapter.AdapterCategoryGraphic
import com.isaquesoft.despesas.presentation.ui.viewmodel.ComponentesVisuais
import com.isaquesoft.despesas.presentation.ui.viewmodel.EstadoAppViewModel
import com.isaquesoft.despesas.presentation.ui.viewmodel.ResumeGraphicExpenseViewModel
import com.isaquesoft.despesas.utils.MonthYearPickerDialog
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
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
        initDate()
        val (startDate, endDate) = viewModel.getFirstAndLastDayOfMonth(Calendar.getInstance())
        viewModel.getAllExpense(startDate, endDate)
        initViewModel()
        binding.monthPickerView.setOnClickListener { buscaMes() }
    }

    private fun buscaMes() {
        val dialog = MonthYearPickerDialog(requireContext()) { month, year ->
            val calendar = Calendar.getInstance().apply {
                set(Calendar.MONTH, month - 1)
                set(Calendar.YEAR, year)
            }
            updateText(calendar)
            val (startDate, endDate) = viewModel.getFirstAndLastDayOfMonth(calendar)
            viewModel.getAllExpense(startDate, endDate)
        }
        dialog.show()
    }

    private fun updateText(calendar: Calendar) {
        val dateFormatter = SimpleDateFormat("MMMM/yyyy", Locale.getDefault()).apply {
            applyPattern("MMMM/yyyy")
            isLenient = false
        }
        val month = dateFormatter.format(calendar.time)
        val formatMonth = month.capitalize()
        binding.monthPickerView.text = formatMonth
    }

    private fun initDate() {
        val calendar = Calendar.getInstance()
        val dateFormatter = SimpleDateFormat("MMMM/yyyy", Locale.getDefault()).apply {
            applyPattern("MMMM/yyyy")
            isLenient = false
        }
        val month = dateFormatter.format(calendar.time)
        val formatMonth = month.capitalize()
        binding.monthPickerView.text = formatMonth
    }

    private fun initViewModel() {
        viewModel.expenseState.observe(viewLifecycleOwner) {
            when (it) {
                is ExpenseResumeState.ShowExpenses -> showExpenses(it.expenses)
            }
        }
    }

    private fun showExpenses(expenses: List<Expense>) {
        if (expenses.isEmpty()) {
            binding.resumeGraphicAnimationView.visibility = View.VISIBLE
            binding.resumeGraphicNoExpense.visibility = View.VISIBLE
        } else {
            binding.resumeGraphicAnimationView.visibility = View.GONE
            binding.resumeGraphicNoExpense.visibility = View.GONE
        }
        val entries: MutableList<PieEntry> = mutableListOf()
        val colors: MutableList<Int> = mutableListOf()
        val categoryMap: MutableMap<String, Float> = mutableMapOf()

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
                val category = expense.category

                val value = BigDecimal(formattedValueString).toFloat()

                if (categoryMap.containsKey(category)) {
                    categoryMap[category] = categoryMap[category]!! + value
                } else {
                    categoryMap[category] = value
                }
            }
        }

        val totalValue = categoryMap.values.sum()

        categoryMap.forEach { (category, value) ->
            val percent = value / totalValue * 100
            entries.add(PieEntry(percent, ""))
            val categoryExpenses = expenses.filter { it.category == category }
            if (!colors.contains(Color.parseColor(categoryExpenses[0].corIcon))) {
                colors.add(Color.parseColor(categoryExpenses[0].corIcon))
            }
        }
        val pieChart = binding.pieChart
        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(pieChart))
        data.setValueTextSize(10f)
        data.setValueTextColor(Color.BLACK)
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = false
        pieChart.data = data
        pieChart.invalidate()

        dataSet.valueLineColor = Color.BLACK
        dataSet.valueLinePart1OffsetPercentage = 90f
        dataSet.valueLinePart1Length = 0.3f
        dataSet.valueLinePart2Length = 0.6f
        dataSet.valueTextColor = Color.BLACK
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        dataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        val categorias = mutableMapOf<String, Triple<Double, Int, String>>()

        for (expense in expenses) {
            val categoria = expense.category
            val originalString = expense.value
            val onlyLetters = originalString.replace(Regex("[\\d.,\\s]"), "")
            val deviceLocale = Locale.getDefault()
            val currencySymbol = Currency.getInstance(deviceLocale).symbol

            if (onlyLetters == currencySymbol) {
                val valueString = expense.value.replace(currencySymbol, "").trim().replace(",", ".")
                val lastDotIndex = valueString.lastIndexOf('.')
                val formattedValueString = if (lastDotIndex >= 0) {
                    valueString.substring(0, lastDotIndex).replace(".", "") + valueString.substring(
                        lastDotIndex,
                    )
                } else {
                    valueString
                }
                val value = formattedValueString.toDouble()
                val iconPosition = expense.iconPosition
                val corIcon = expense.corIcon

                val currentValue = categorias.getOrDefault(categoria, Triple(0.0, 0, ""))
                categorias[categoria] = Triple(currentValue.first + value, iconPosition, corIcon)
            }
        }

        val listaCategoriasValores = mutableListOf<CategoryGraphic>()
        val deviceLocale = Locale.getDefault()
        val currencyFormatter = NumberFormat.getCurrencyInstance(deviceLocale)

        for ((categoria, valorIconCor) in categorias) {
            val valorTotalFormatado = currencyFormatter.format(valorIconCor.first)
            val iconPosition = valorIconCor.second
            val corIcon = valorIconCor.third
            listaCategoriasValores.add(
                CategoryGraphic(
                    categoria,
                    valorTotalFormatado,
                    iconPosition,
                    corIcon,
                ),
            )
        }

        binding.resumeGraphicRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.resumeGraphicRecyclerview.adapter =
            AdapterCategoryGraphic(requireContext(), listaCategoriasValores)
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

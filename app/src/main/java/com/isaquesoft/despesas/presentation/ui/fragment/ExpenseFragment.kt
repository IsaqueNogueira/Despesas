package com.isaquesoft.despesas.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.databinding.ExpenseFragmentBinding
import com.isaquesoft.despesas.presentation.state.ExpenseState
import com.isaquesoft.despesas.presentation.ui.adapter.AdapterExpense
import com.isaquesoft.despesas.presentation.ui.viewmodel.ComponentesVisuais
import com.isaquesoft.despesas.presentation.ui.viewmodel.EstadoAppViewModel
import com.isaquesoft.despesas.presentation.ui.viewmodel.ExpenseFragmentViewModel
import com.isaquesoft.despesas.utils.AlertDialogStandard
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class ExpenseFragment : Fragment() {

    private lateinit var binding: ExpenseFragmentBinding
    private val controlation by lazy { findNavController() }
    private val estadoAppViewModel: EstadoAppViewModel by sharedViewModel()
    private val viewModel: ExpenseFragmentViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = ExpenseFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        estadoAppViewModel.temComponentes = ComponentesVisuais(true, false)
        requireActivity().title = getString(R.string.title_my_expense)
        initViewModel()
        goToNewExpenseFragment()
        dateText()
    }

    private fun dateText() {
        val calendar = Calendar.getInstance()
        val dateFormatter = SimpleDateFormat("MMMM/yyyy", Locale.getDefault()).apply {
            applyPattern("MMMM/yyyy")
            isLenient = false
        }
        viewModel.getAllExpense(viewModel.getFirstAndLastDayOfMonth(calendar)[0], viewModel.getFirstAndLastDayOfMonth(calendar)[1])
        binding.expenseDateText.text = dateFormatter.format(calendar.time).replaceFirstChar { it.uppercase() }
        binding.expensePrevbutton.setOnClickListener {
            viewModel.clickPrevButton()
        }
        binding.expenseNextbutton.setOnClickListener {
            viewModel.clickNextButton()
        }
    }

    private fun initViewModel() {
        viewModel.expenseState.observe(
            viewLifecycleOwner,
        ) {
            when (it) {
                is ExpenseState.DateText -> showDateText(it)
                is ExpenseState.ShowExpenses -> showExpenses(it.expense)
                is ExpenseState.MinDateMaxDate -> minMaxDate(it.minDate, it.maxDate)
                is ExpenseState.ShowValueEndBalance -> showFullValueEndBalance(it.fullValue, it.fullBalance)
            }
        }
    }

    private fun showFullValueEndBalance(fullValue: String, fullBalance: String) {
        binding.expenseFullPayable.text = fullValue
        binding.expenseBalanceToPay.text = fullBalance
    }

    private fun showDateText(it: ExpenseState.DateText) {
        viewModel.getFirstAndLastDayOfMonth(it.calendar)
        val dateFormatter = SimpleDateFormat("MMMM/yyyy", Locale.getDefault()).apply {
            applyPattern("MMMM/yyyy")
            isLenient = false
        }
        val month = dateFormatter.format(it.calendar.time)
        val formatMonth = month.capitalize()
        binding.expenseDateText.text = formatMonth
    }

    private fun minMaxDate(minDate: Long, maxDate: Long) {
        viewModel.getAllExpense(minDate, maxDate)
    }

    private fun showExpenses(expenses: List<Expense>) {
        binding.expenseRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.expenseRecyclerview.adapter = AdapterExpense(expenses, this::clickItem)
        viewModel.fullExpenseSum(expenses)
        showNewCoin(expenses)
    }

    private fun showNewCoin(expenses: List<Expense>) {
        expenses.forEach {
            val deviceLocale = Locale.getDefault()
            val currencySymbol = Currency.getInstance(deviceLocale).symbol
            val originalString = it.value
            val onlyLetters = originalString.replace(Regex("[\\d.,\\s]"), "")
            if (onlyLetters != currencySymbol) {
                val title = getString(R.string.important)
                val message = getString(R.string.new_coin_info)
                AlertDialogStandard().sutupDialog(requireContext(), title, message)
            }
        }
    }

    private fun clickItem(expense: Expense) {
        goToExpenseDetailsFragmennt(expense)
    }

    private fun goToExpenseDetailsFragmennt(expense: Expense) {
        val direction = ExpenseFragmentDirections.actionExpenseFragmentToExpenseDetailsFragment(expense)
        controlation.navigate(direction)
    }

    private fun goToNewExpenseFragment() {
        binding.expenseFloating.setOnClickListener {
            val direction = ExpenseFragmentDirections.actionExpenseFragmentToNewExpenseFragment()
            controlation.navigate(direction)
        }
    }
}

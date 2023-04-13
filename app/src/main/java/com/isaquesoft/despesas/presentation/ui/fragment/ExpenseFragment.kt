package com.isaquesoft.despesas.presentation.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.databinding.ExpenseFragmentBinding
import com.isaquesoft.despesas.presentation.state.ExpenseState
import com.isaquesoft.despesas.presentation.ui.SharedPreferences
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
    private lateinit var expenses: List<Expense>

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
        setHasOptionsMenu(true)
        estadoAppViewModel.temComponentes = ComponentesVisuais(true, false)
        requireActivity().title = getString(R.string.title_my_expense)
        initViewModel()
        goToNewExpenseFragment()
        dateText()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_principal, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = getString(R.string.search)

        setupSearch(searchView)

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setupSearch(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Ação a ser executada quando o usuário submeter a busca

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Ação a ser executada quando o texto da busca mudar
                if (expenses.isNotEmpty()) {
                    val newExpenses =
                        expenses.filter {
                            it.description.toLowerCase(Locale.getDefault())
                                .contains(newText?.toLowerCase(Locale.getDefault()).toString()) || it.value.contains(newText.toString())
                        }
                    binding.expenseRecyclerview.layoutManager =
                        LinearLayoutManager(requireContext())
                    binding.expenseRecyclerview.adapter = AdapterExpense(newExpenses, this@ExpenseFragment::clickItem)
                }
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_item -> {
                goToSettingsFragment()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dateText() {
        viewModel.initCalendar()

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
                is ExpenseState.DateText -> showDateText(it.calendar)
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

    private fun showDateText(calendar: Calendar) {
        when (SharedPreferences(requireContext()).getOrdemList()) {
            "A-Z true" -> {
                viewModel.getAllExpenseByDescriptionAsc(viewModel.getFirstAndLastDayOfMonth(calendar)[0], viewModel.getFirstAndLastDayOfMonth(calendar)[1])
            }
            "Date Desc true" -> {
                viewModel.getAllExpenseDateDesc(viewModel.getFirstAndLastDayOfMonth(calendar)[0], viewModel.getFirstAndLastDayOfMonth(calendar)[1])
            }
            "Date Cres true" -> {
                viewModel.getAllExpenseDateCres(viewModel.getFirstAndLastDayOfMonth(calendar)[0], viewModel.getFirstAndLastDayOfMonth(calendar)[1])
            }
            else -> {
                viewModel.getAllExpense(viewModel.getFirstAndLastDayOfMonth(calendar)[0], viewModel.getFirstAndLastDayOfMonth(calendar)[1])
            }
        }
        viewModel.getFirstAndLastDayOfMonth(calendar)
        val dateFormatter = SimpleDateFormat("MMMM/yyyy", Locale.getDefault()).apply {
            applyPattern("MMMM/yyyy")
            isLenient = false
        }
        val month = dateFormatter.format(calendar.time)
        val formatMonth = month.capitalize()
        binding.expenseDateText.text = formatMonth
    }

    private fun minMaxDate(minDate: Long, maxDate: Long) {
        when (SharedPreferences(requireContext()).getOrdemList()) {
            "A-Z true" -> {
                viewModel.getAllExpenseByDescriptionAsc(minDate, maxDate)
            }
            "Date Desc true" -> {
                viewModel.getAllExpenseDateDesc(minDate, maxDate)
            }
            "Date Cres true" -> {
                viewModel.getAllExpenseDateCres(minDate, maxDate)
            }
            else -> {
                viewModel.getAllExpense(minDate, maxDate)
            }
        }
    }

    private fun showExpenses(expenses: List<Expense>) {
        this.expenses = expenses
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

    private fun goToSettingsFragment() {
        val direction = ExpenseFragmentDirections.actionExpenseFragmentToSettingsFragment()
        controlation.navigate(direction)
    }
}

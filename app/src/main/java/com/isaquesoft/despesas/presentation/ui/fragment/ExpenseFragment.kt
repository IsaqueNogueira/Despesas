package com.isaquesoft.despesas.presentation.ui.fragment

import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.Category
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.databinding.ExpenseFragmentBinding
import com.isaquesoft.despesas.presentation.state.ExpenseState
import com.isaquesoft.despesas.presentation.ui.SharedPreferences
import com.isaquesoft.despesas.presentation.ui.adapter.AdapterExpense
import com.isaquesoft.despesas.presentation.ui.viewmodel.ComponentesVisuais
import com.isaquesoft.despesas.presentation.ui.viewmodel.EstadoAppViewModel
import com.isaquesoft.despesas.presentation.ui.viewmodel.ExpenseFragmentViewModel
import com.isaquesoft.despesas.utils.AlertDialogStandard
import com.isaquesoft.despesas.utils.CategoryUtils
import com.isaquesoft.despesas.utils.CustomToast
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
        dateText()
        viewModel.getAllCategory()
        goToNewExpensesFragment()
        goToResumeGraphicFragment()
        setupSharePdf()
    }

    private fun setupSharePdf() {
        binding.expenseMenuBottomShareIcon.setOnClickListener {
            pdfShareButton()
        }
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
                                .contains(
                                    newText?.toLowerCase(Locale.getDefault()).toString(),
                                ) || it.value.contains(newText.toString())
                        }
                    binding.expenseRecyclerview.layoutManager =
                        LinearLayoutManager(requireContext())
                    binding.expenseRecyclerview.adapter =
                        AdapterExpense(newExpenses, this@ExpenseFragment::clickItem)
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

    private fun pdfShareButton() {
        if (expenses.isNotEmpty()) {
            val navigation =
                ExpenseFragmentDirections.actionExpenseFragmentToViewPdfFragment(expenses.toTypedArray())
            controlation.navigate(navigation)
        } else {
            val message = getString(R.string.no_expense_share)
            val customToast = CustomToast(requireContext(), message)
            customToast.show()
        }
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
                is ExpenseState.ShowValueEndBalance -> showFullValueEndBalance(
                    it.fullValue,
                    it.fullBalance,
                )

                is ExpenseState.ShowAllCategory -> showCategory(it.category)
            }
        }
    }

    private fun showCategory(category: List<Category>) {
        if (category.isEmpty()) {
            val listCategory = CategoryUtils().getListCategory(requireContext())
            viewModel.insertAllCategory(listCategory)
        }

        val roupas = resources.getIdentifier("roupas", "drawable", requireContext().packageName)
        val viagem = resources.getIdentifier("viagem", "drawable", requireContext().packageName)
        val cartao = resources.getIdentifier("cartao", "drawable", requireContext().packageName)
        val saude = resources.getIdentifier("saude", "drawable", requireContext().packageName)
        val filmes = resources.getIdentifier("filmes", "drawable", requireContext().packageName)
        val outros = resources.getIdentifier("outros", "drawable", requireContext().packageName)
        val comidabebida =
            resources.getIdentifier("comidabebida", "drawable", requireContext().packageName)
        val transporte =
            resources.getIdentifier("transporte", "drawable", requireContext().packageName)
        val eletronicos =
            resources.getIdentifier("eletronicos", "drawable", requireContext().packageName)
        val educacao =
            resources.getIdentifier("educacao", "drawable", requireContext().packageName)
        val entretenimento =
            resources.getIdentifier("entretenimento", "drawable", requireContext().packageName)

        val drawables = arrayOf(
            ContextCompat.getDrawable(requireContext(), roupas),
            ContextCompat.getDrawable(requireContext(), viagem),
            ContextCompat.getDrawable(requireContext(), cartao),
            ContextCompat.getDrawable(requireContext(), saude),
            ContextCompat.getDrawable(requireContext(), filmes),
            ContextCompat.getDrawable(requireContext(), outros),
            ContextCompat.getDrawable(requireContext(), comidabebida),
            ContextCompat.getDrawable(requireContext(), transporte),
            ContextCompat.getDrawable(requireContext(), eletronicos),
            ContextCompat.getDrawable(requireContext(), educacao),
            ContextCompat.getDrawable(requireContext(), entretenimento),
        )
    }

    private fun showFullValueEndBalance(fullValue: String, fullBalance: String) {
        binding.expenseFullPayable.text = fullValue
        binding.expenseBalanceToPay.text = fullBalance
    }

    private fun showDateText(calendar: Calendar) {
        when (SharedPreferences(requireContext()).getOrdemList()) {
            "A-Z true" -> {
                viewModel.getAllExpenseByDescriptionAsc(
                    viewModel.getFirstAndLastDayOfMonth(calendar)[0],
                    viewModel.getFirstAndLastDayOfMonth(calendar)[1],
                )
            }

            "Date Desc true" -> {
                viewModel.getAllExpenseDateDesc(
                    viewModel.getFirstAndLastDayOfMonth(calendar)[0],
                    viewModel.getFirstAndLastDayOfMonth(calendar)[1],
                )
            }

            "Date Cres true" -> {
                viewModel.getAllExpenseDateCres(
                    viewModel.getFirstAndLastDayOfMonth(calendar)[0],
                    viewModel.getFirstAndLastDayOfMonth(calendar)[1],
                )
            }

            else -> {
                viewModel.getAllExpense(
                    viewModel.getFirstAndLastDayOfMonth(calendar)[0],
                    viewModel.getFirstAndLastDayOfMonth(calendar)[1],
                )
            }
        }
        viewModel.getFirstAndLastDayOfMonth(calendar)
        val dateFormatter = SimpleDateFormat("MMMM/yyyy", Locale.getDefault()).apply {
            applyPattern("MMMM/yyyy")
            isLenient = false
        }
        val month = dateFormatter.format(calendar.time)
        val formatMonth = month.capitalize()
        binding.expenseDateText.startAnimation(
            AnimationUtils.loadAnimation(
                context,
                R.anim.zoom_in,
            ),
        )
        binding.expenseDateText.text = formatMonth
    }

    private fun showExpenses(expenses: List<Expense>) {
        this.expenses = expenses
        binding.expenseRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.expenseRecyclerview.adapter = AdapterExpense(expenses, this::clickItem)
        viewModel.fullExpenseSum(expenses)
        showNewCoin(expenses)
        checkExpenseRegister()
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
        val bottomSheetDialogFragment =
            BottomShettPrincipalFragment(expense, ::deleteExpense, ::updateExpense)
        fragmentManager?.let { it1 ->
            bottomSheetDialogFragment.show(
                it1,
                bottomSheetDialogFragment.tag,
            )
        }
    }

    private fun updateExpense(expense: Expense) {
        viewModel.updateExpense(expense)
    }

    private fun deleteExpense(expense: Expense) {
        viewModel.deleteExpense(expense)
    }

    private fun checkExpenseRegister() {
        if (expenses.isEmpty()) {
            binding.expenseAnimationView.visibility = View.VISIBLE
            binding.txtInfoNoExpenseRegister.visibility = View.VISIBLE
        } else {
            binding.expenseAnimationView.visibility = View.GONE
            binding.txtInfoNoExpenseRegister.visibility = View.GONE
        }
    }

    private fun goToExpenseDetailsFragmennt(expense: Expense) {
        val direction =
            ExpenseFragmentDirections.actionExpenseFragmentToExpenseDetailsFragment(expense)
        controlation.navigate(direction)
    }

    private fun goToNewExpensesFragment() {
        binding.expenseFloating.setOnClickListener {
            val direction = ExpenseFragmentDirections.actionExpenseFragmentToNewExpenseFragment()
            controlation.navigate(direction)
        }
    }

    private fun goToResumeGraphicFragment() {
        binding.expenseMenuBottomGraficoIcon.setOnClickListener {
            val direction = ExpenseFragmentDirections.actionExpenseFragmentToResumeGraphicExpenses()
            controlation.navigate(direction)
        }
    }

    private fun goToSettingsFragment() {
        val direction = ExpenseFragmentDirections.actionExpenseFragmentToSettingsFragment()
        controlation.navigate(direction)
    }
}

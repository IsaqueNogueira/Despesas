package com.isaquesoft.despesas.presentation.ui.fragment

import OnSwipeTouchListener
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.Category
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.databinding.ExpenseFragmentBinding
import com.isaquesoft.despesas.presentation.state.ExpenseState
import com.isaquesoft.despesas.presentation.ui.SharedPreferences
import com.isaquesoft.despesas.presentation.ui.activity.AssinaturaActivity
import com.isaquesoft.despesas.presentation.ui.adapter.AdapterExpense
import com.isaquesoft.despesas.presentation.ui.fragment.SettingsFragment.Companion.LISTA_AZ
import com.isaquesoft.despesas.presentation.ui.fragment.SettingsFragment.Companion.LISTA_CRES
import com.isaquesoft.despesas.presentation.ui.fragment.SettingsFragment.Companion.LISTA_DESC
import com.isaquesoft.despesas.presentation.ui.viewmodel.ComponentesVisuais
import com.isaquesoft.despesas.presentation.ui.viewmodel.EstadoAppViewModel
import com.isaquesoft.despesas.presentation.ui.viewmodel.ExpenseFragmentViewModel
import com.isaquesoft.despesas.utils.AlertDialogStandard
import com.isaquesoft.despesas.utils.CategoryUtils
import com.isaquesoft.despesas.utils.CustomToast
import com.isaquesoft.despesas.utils.shareExpensesAsExcel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Currency
import java.util.Locale

class ExpenseFragment : Fragment() {

    private lateinit var binding: ExpenseFragmentBinding
    private val controlation by lazy { findNavController() }
    private val estadoAppViewModel: EstadoAppViewModel by sharedViewModel()
    private val viewModel: ExpenseFragmentViewModel by viewModel()
    private var expenses: List<Expense> = mutableListOf()
    private var calendarUpdateValueEndBalance = Calendar.getInstance()
    private var calendarSelected: Calendar? = null
    private var mInterstitialAd: InterstitialAd? = null
    private var compartilhouPdf = false
    private lateinit var listCategory: List<Category>
    private var categoriaSelecionadaId = ""
    private var filtroAplicado = false

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
        setLado()
        carregaAnuncioIntersticial()
        binding.expenseDateText.setOnClickListener {
            buscaMes()
        }
    }

    private fun buscaMes() {
        val currentMonth = calendarUpdateValueEndBalance.get(Calendar.MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            null,
            calendarUpdateValueEndBalance.get(Calendar.YEAR),
            currentMonth,
            calendarUpdateValueEndBalance.get(Calendar.DAY_OF_MONTH),
        )
        datePickerDialog.datePicker.calendarViewShown = false
        datePickerDialog.datePicker.spinnersShown = true
        datePickerDialog.setButton(
            DialogInterface.BUTTON_POSITIVE,
            getString(R.string.ok),
        ) { dialog, which ->
            val selectedMonth = datePickerDialog.datePicker.month
            val selectedYear = datePickerDialog.datePicker.year
            val selectedCalendar = Calendar.getInstance().apply {
                set(Calendar.MONTH, selectedMonth)
                set(Calendar.YEAR, selectedYear)
            }
            calendarSelected = selectedCalendar
            viewModel.setCalendarSelected(selectedCalendar)
            showDateText(selectedCalendar)
        }

        datePickerDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_principal, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter -> {
                openBottomSheetFilter()
            }

            R.id.share_item -> {
                sharePdf()
            }

            R.id.share_excel -> {
                shareExcel()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun shareExcel() {
        if (expenses.isNotEmpty()) {
            if (SharedPreferences(requireContext()).getVerifyCompraAssinatura()) {
                shareExpensesAsExcel(requireActivity(), requireContext(), expenses)
            } else {
                Intent(requireActivity(), AssinaturaActivity::class.java).apply {
                    startActivity(this)
                }
            }
        } else {
            CustomToast(requireContext(), getString(R.string.no_expense_share)).show()
        }
    }

    private fun sharePdf() {
        if (expenses.isNotEmpty()) {
            val navigation =
                ExpenseFragmentDirections.actionExpenseFragmentToViewPdfFragment(expenses.toTypedArray())
            controlation.navigate(navigation)
        } else {
            CustomToast(requireContext(), getString(R.string.no_expense_share)).show()
        }
    }

    private fun dateText() {
        viewModel.initCalendar()

        binding.expensePrevbutton.setOnClickListener {
            viewModel.clickPrevButton()
            filtroAplicado = false
            categoriaSelecionadaId = ""
        }
        binding.expenseNextbutton.setOnClickListener {
            viewModel.clickNextButton()
            filtroAplicado = false
            categoriaSelecionadaId = ""
        }
    }

    private fun initViewModel() {
        viewModel.expenseState.observe(
            viewLifecycleOwner,
        ) {
            when (it) {
                is ExpenseState.DateText -> showDateText(it.calendar)
                is ExpenseState.ShowExpenses -> showExpenses(it.expense)
                is ExpenseState.ShowValueEndBalance -> {
                    showFullValueEndBalance(
                        it.fullValue,
                        it.fullBalance,
                    )
                    viewModel.getAllCategory()
                }

                is ExpenseState.ShowAllCategory -> showCategory(it.category)
            }
        }
    }

    private fun showCategory(category: List<Category>) = if (category.isEmpty()) {
        listCategory = CategoryUtils().getListCategory(requireContext())
        viewModel.insertAllCategory(listCategory)
    } else {
        listCategory = category
    }

    private fun showFullValueEndBalance(fullValue: String, fullBalance: String) {
        binding.expenseFullPayable.text = fullValue
        binding.expenseBalanceToPay.text = fullBalance
    }

    private fun showDateText(calendar: Calendar) {
        calendarUpdateValueEndBalance = calendar
        val (startDate, endDate) = viewModel.getFirstAndLastDayOfMonth(calendar)
        when (SharedPreferences(requireContext()).getOrdemList()) {
            LISTA_AZ -> viewModel.getAllExpenseByDescriptionAsc(startDate, endDate)
            LISTA_DESC -> viewModel.getAllExpenseDateDesc(startDate, endDate)
            LISTA_CRES -> viewModel.getAllExpenseDateCres(startDate, endDate)
            else -> viewModel.getAllExpense(startDate, endDate)
        }

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
        binding.expenseRecyclerview.adapter =
            AdapterExpense(requireContext(), expenses.toMutableList(), this::clickItem)
        viewModel.fullExpenseSum(expenses)
        showNewCoin(expenses)
        checkExpenseRegister()
    }

    private fun setLado() {
        binding.expenseRecyclerview.addOnItemTouchListener(object :
            RecyclerView.OnItemTouchListener {
            val swipeTouchListener = object : OnSwipeTouchListener() {
                override fun onSwipeLeft() {
                    val slideOutLeft = AnimationUtils.loadAnimation(context, R.anim.slide_in_right)
                    binding.expenseRecyclerview.startAnimation(slideOutLeft)
                    viewModel.clickNextButton()
                    filtroAplicado = false
                    categoriaSelecionadaId = ""
                }

                override fun onSwipeRight() {
                    val slideInLeft = AnimationUtils.loadAnimation(context, R.anim.slide_in_left)
                    binding.expenseRecyclerview.startAnimation(slideInLeft)
                    viewModel.clickPrevButton()
                    filtroAplicado = false
                    categoriaSelecionadaId = ""
                }
            }

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                swipeTouchListener.onTouch(rv, e)
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                swipeTouchListener.onTouch(rv, e)
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })
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

    private fun openBottomSheetFilter() {
        val bottomSheetFilterFragment = BottomSheetFilterFragment(listCategory, filtroAplicado, categoriaSelecionadaId, ::ordemAplicada, ::clickCategory, ::clickLimparFilter)
        fragmentManager?.let {
            bottomSheetFilterFragment.show(it, bottomSheetFilterFragment.tag)
        }
    }

    private fun clickCategory(category: Category) {
        categoriaSelecionadaId = category.id.toString()
        filtroAplicado = true
        val (startDate, endDate) = viewModel.getFirstAndLastDayOfMonth(calendarUpdateValueEndBalance)
        viewModel.getAllExpensesFilterCategory(startDate, endDate, category.category)
    }

    private fun clickLimparFilter() {
        categoriaSelecionadaId = ""
        filtroAplicado = false
        showDateText(calendarUpdateValueEndBalance)
    }

    private fun ordemAplicada() {
        showDateText(calendarUpdateValueEndBalance)
    }

    private fun clickItem(expense: Expense) {
        val bottomSheetDialogFragment =
            BottomShettPrincipalFragment(
                expense,
                ::deleteExpense,
                ::deleteAllExpense,
                ::deleteItNext,
                ::updateExpense,
            )
        fragmentManager?.let { it1 ->
            bottomSheetDialogFragment.show(
                it1,
                bottomSheetDialogFragment.tag,
            )
        }
    }

    private fun updateExpense(expense: Expense) {
        val (startDate, endDate) = viewModel.getFirstAndLastDayOfMonth(calendarUpdateValueEndBalance)
        viewModel.updateExpense(expense, startDate, endDate)
        (binding.expenseRecyclerview.adapter as AdapterExpense).atualiza(expense)

        val quantidadeDeAtualizacaoAtual = SharedPreferences(requireContext()).getQuantityAtualizaDespesa()
        if (quantidadeDeAtualizacaoAtual < 15) {
            val insereNovaQuantidade = quantidadeDeAtualizacaoAtual + 1
            SharedPreferences(requireContext()).setQuantityAtualizaDespesa(insereNovaQuantidade)
        } else if (quantidadeDeAtualizacaoAtual == 15) {
            if (!SharedPreferences(requireContext()).getVerifyCompraAssinatura()) {
                mInterstitialAd?.show(requireActivity())
                carregaAnuncioIntersticial()
            }
            val insereNovaQuantidade = 0
            SharedPreferences(requireContext()).setQuantityAtualizaDespesa(insereNovaQuantidade)
        }
    }

    private fun deleteExpense(expense: Expense) {
        val (startDate, endDate) = viewModel.getFirstAndLastDayOfMonth(calendarUpdateValueEndBalance)
        viewModel.deleteExpense(expense, startDate, endDate)
        (binding.expenseRecyclerview.adapter as AdapterExpense).remove(expense)
    }

    private fun deleteAllExpense(expenses: List<Expense>) {
        val (startDate, endDate) = viewModel.getFirstAndLastDayOfMonth(calendarUpdateValueEndBalance)
        viewModel.deleteAllExpense(expenses, startDate, endDate)
        (binding.expenseRecyclerview.adapter as AdapterExpense).removeAll(expenses)
    }

    private fun deleteItNext(expenses: List<Expense>) {
        val (startDate, endDate) = viewModel.getFirstAndLastDayOfMonth(calendarUpdateValueEndBalance)
        viewModel.deleteAllExpense(expenses, startDate, endDate)
        (binding.expenseRecyclerview.adapter as AdapterExpense).removeAll(expenses)
    }

    private fun checkExpenseRegister() {
        if (expenses.isEmpty() && !filtroAplicado) {
            binding.expenseAnimationView.visibility = View.VISIBLE
            binding.txtInfoNoExpenseRegister.visibility = View.VISIBLE
            binding.txtInfoNoExpenseFilter.visibility = View.GONE
            binding.expenseAnimationViewNoFilter.visibility = View.GONE
        } else if (expenses.isEmpty() && filtroAplicado) {
            binding.txtInfoNoExpenseFilter.visibility = View.VISIBLE
            binding.expenseAnimationViewNoFilter.visibility = View.VISIBLE
            binding.expenseAnimationView.visibility = View.GONE
            binding.txtInfoNoExpenseRegister.visibility = View.GONE
        } else {
            binding.expenseAnimationView.visibility = View.GONE
            binding.txtInfoNoExpenseRegister.visibility = View.GONE
            binding.txtInfoNoExpenseFilter.visibility = View.GONE
            binding.expenseAnimationViewNoFilter.visibility = View.GONE
        }
    }

    private fun carregaAnuncioIntersticial() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            requireContext(),
            "ca-app-pub-6470587668575312/5355919139",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }
            },
        )
    }
}

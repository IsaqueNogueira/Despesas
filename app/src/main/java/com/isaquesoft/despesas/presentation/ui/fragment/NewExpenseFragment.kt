package com.isaquesoft.despesas.presentation.ui.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.Category
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.databinding.NewExpenseFragmentBinding
import com.isaquesoft.despesas.presentation.state.NewExpenseState
import com.isaquesoft.despesas.presentation.ui.viewmodel.ComponentesVisuais
import com.isaquesoft.despesas.presentation.ui.viewmodel.EstadoAppViewModel
import com.isaquesoft.despesas.presentation.ui.viewmodel.NewExpenseFragmentViewModel
import com.isaquesoft.despesas.utils.CoinUtils
import com.isaquesoft.despesas.utils.CustomToast
import com.isaquesoft.despesas.utils.DateUtils
import com.isaquesoft.despesas.utils.IconsCategory
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class NewExpenseFragment : Fragment() {

    private lateinit var binding: NewExpenseFragmentBinding
    private val estadoAppViewModel: EstadoAppViewModel by sharedViewModel()
    private val viewModel: NewExpenseFragmentViewModel by viewModel()
    private var repeat: Boolean = false
    private var installments: Int = 0
    private var selectedInstallments: Boolean = false
    private lateinit var category: Category
    private var bottomNavigationView: BottomNavigationView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = NewExpenseFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        requireActivity().title = getString(R.string.new_expense)
        estadoAppViewModel.temComponentes = ComponentesVisuais(true, true)
        bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView)
        bottomNavigationView?.visibility = View.GONE

        getCoinFormatInput()
        setDateInputMaturity()
        clickRepeat()
        clickFixed()
        clickSave()
        initViewModel()
        initEditTextValueAutomatic()
        viewModel.getAllCategory()
    }

    override fun onDestroy() {
        super.onDestroy()
        bottomNavigationView?.visibility = View.VISIBLE
    }

    private fun initViewModel() {
        viewModel.expenseState.observe(viewLifecycleOwner) {
            when (it) {
                is NewExpenseState.ShowAllCategory -> showCategory(it.category)
            }
        }
    }

    private fun showCategory(category: List<Category>) {
        if (category.isNotEmpty()) {
            val listIcons = IconsCategory().listIcons(requireContext())
            this.category = category.first()
            val categoryTxt = binding.newExpenseCategoryText
            categoryTxt.text = category.first().category

            val iconId = listIcons[category.first().iconPosition]

            val iconCategory = binding.newExpenseIconCategory
            iconCategory.setImageDrawable(iconId)

            val background = categoryTxt.background as GradientDrawable
            background.setStroke(
                resources.getDimensionPixelSize(R.dimen.stroke_width),
                Color.parseColor(category.first().cor),
            )
            categoryTxt.background = background

            val iconImageView = binding.newExpenseIconCategory
            val drawable = iconImageView.background as GradientDrawable
            drawable.setColor(Color.parseColor(category.first().cor))
        }

        binding.newExpenseCategoryDescription.setOnClickListener {
            val bottomSheetDialogFragment =
                BottomSheetCategoryFragment(category, ::updateCategory)
            fragmentManager?.let { it1 ->
                bottomSheetDialogFragment.show(
                    it1,
                    bottomSheetDialogFragment.tag,
                )
            }
        }
    }

    private fun updateCategory(category: Category) {
        this.category = category

        val listIcons = IconsCategory().listIcons(requireContext())

        val iconId = listIcons[category.iconPosition]

        val categoryTxt = binding.newExpenseCategoryText
        categoryTxt.text = category.category

        val background = categoryTxt.background as GradientDrawable
        background.setStroke(
            resources.getDimensionPixelSize(R.dimen.stroke_width),
            Color.parseColor(category.cor),
        )
        categoryTxt.background = background

        val iconCategory = binding.newExpenseIconCategory
        iconCategory.setImageDrawable(iconId)

        val iconImageView = binding.newExpenseIconCategory
        val drawable = iconImageView.background as GradientDrawable
        drawable.setColor(Color.parseColor(category.cor))
    }

    private fun initEditTextValueAutomatic() {
        val editValueInitAutomatic = binding.newExpenseInputValue

        editValueInitAutomatic.requestFocus()
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editValueInitAutomatic, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun clickSave() {
        binding.newExpenseSave.setOnClickListener {
            val description = binding.newExpenseInputDescription.text.toString()
            val value = binding.newExpenseInputValue.text.toString()
            val maturity = binding.newExpenseInputMaturity.text.toString()
            val date = SimpleDateFormat("dd/MM/yyyy").parse(maturity)
            val calendar = Calendar.getInstance()
            calendar.time = date
            val dataCreated = Date().time
            val installmentsInput = binding.newExpenseInputInstallments.text.toString()

            val valorNumerico = value.replace(Regex("[^\\d]"), "").toDouble() / 100

            if (!TextUtils.isEmpty(description) && !TextUtils.isEmpty(value) && valorNumerico > 0) {
                if (selectedInstallments && TextUtils.isEmpty(installmentsInput)) {
                    binding.newExpenseInputInstallments.setError(getString(R.string.required_field_installments))
                    binding.newExpenseInputInstallments.requestFocus()
                    return@setOnClickListener
                } else if (selectedInstallments && !TextUtils.isEmpty(installmentsInput)) {
                    installments = installmentsInput.toInt()
                }

                if (installments == 0 && repeat) {
                    for (i in 1..420) {
                        val expense = Expense(
                            description = description,
                            value = value,
                            dateCreated = dataCreated,
                            date = calendar.timeInMillis,
                            repeat = repeat,
                            installments = 1,
                            category = category.category,
                            iconPosition = category.iconPosition,
                            corIcon = category.cor,
                        )
                        viewModel.insertExpense(expense)
                        calendar.add(Calendar.MONTH, 1)
                    }
                } else if (installments > 1) {
                    for (i in 0 until installments) {
                        val inforInstallment = "${i + 1}/$installments"
                        val expense = Expense(
                            description = description + " " + inforInstallment,
                            value = value,
                            dateCreated = dataCreated,
                            date = calendar.timeInMillis,
                            repeat = repeat,
                            installments = installments,
                            category = category.category,
                            iconPosition = category.iconPosition,
                            corIcon = category.cor,
                        )
                        viewModel.insertExpense(expense)
                        calendar.add(Calendar.MONTH, 1)
                    }
                } else {
                    val expense = Expense(
                        description = description,
                        value = value,
                        dateCreated = dataCreated,
                        date = calendar.timeInMillis,
                        repeat = repeat,
                        installments = installments,
                        category = category.category,
                        iconPosition = category.iconPosition,
                        corIcon = category.cor,
                    )
                    viewModel.insertExpense(expense)
                }
                requireActivity().onBackPressed()
            } else {
                if (TextUtils.isEmpty(description)) {
                    binding.newExpenseInputDescription.setError(getString(R.string.required_field_description))
                    binding.newExpenseInputDescription.requestFocus()
                } else {
                    binding.newExpenseInputValue.requestFocus()
                    val message = getString(R.string.required_field_value) + " (" + value + ")"
                    val customToast = CustomToast(requireContext(), message)
                    customToast.show()
                }
            }
        }
    }

    private fun clickRepeat() {
        binding.newExpenseNoRepeat.isChecked = true
        binding.newExpenseRadioGroup.setOnCheckedChangeListener { compoundButton, b ->
            when (b) {
                R.id.new_expense_repeat -> {
                    repeat = true
                    installments = 0
                    selectedInstallments = false
                    binding.newExpenseDiviser.visibility = View.VISIBLE
                    binding.newExpenseFixed.visibility = View.VISIBLE
                    binding.newExpenseInstallments.visibility = View.VISIBLE
                    binding.newExpenseFixed.isChecked = true
                }

                else -> {
                    repeat = false
                    installments = 0
                    selectedInstallments = false
                    binding.newExpenseDiviser.visibility = View.GONE
                    binding.newExpenseFixed.visibility = View.GONE
                    binding.newExpenseInstallments.visibility = View.GONE
                    binding.newExpenseQtdInstallments.visibility = View.GONE
                    binding.newExpenseInputInstallments.visibility = View.GONE
                    binding.newExpenseDiviser.visibility = View.GONE
                }
            }
        }
    }

    private fun clickFixed() {
        binding.newExpenseRadioGroup2.setOnCheckedChangeListener { compoundButton, b ->
            when (b) {
                R.id.new_expense_fixed -> {
                    repeat = true
                    installments = 0
                    binding.newExpenseQtdInstallments.visibility = View.GONE
                    binding.newExpenseInputInstallments.visibility = View.GONE
                    selectedInstallments = false
                }

                else -> {
                    repeat = true
                    binding.newExpenseQtdInstallments.visibility = View.VISIBLE
                    binding.newExpenseInputInstallments.visibility = View.VISIBLE
                    selectedInstallments = true
                }
            }
        }
    }

    private fun setDateInputMaturity() {
        binding.newExpenseInputMaturity.setText(
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault(),
            ).format(Date()),
        )
        binding.newExpenseInputMaturity.setOnClickListener {
            DateUtils.exibirDatePickerDialog(requireContext(), binding.newExpenseInputMaturity)
        }
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

    private fun getCoinFormatInput() {
        CoinUtils.addCurrencyMask(binding.newExpenseInputValue)
    }
}

package com.isaquesoft.despesas.presentation.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.databinding.NewExpenseFragmentBinding
import com.isaquesoft.despesas.presentation.ui.viewmodel.ComponentesVisuais
import com.isaquesoft.despesas.presentation.ui.viewmodel.EstadoAppViewModel
import com.isaquesoft.despesas.presentation.ui.viewmodel.NewExpenseFragmentViewModel
import com.isaquesoft.despesas.utils.CoinUtils
import com.isaquesoft.despesas.utils.DateUtils
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class NewExpenseFragment : Fragment() {

    private lateinit var binding: NewExpenseFragmentBinding
    private val estadoAppViewModel: EstadoAppViewModel by sharedViewModel()
    private val viewModel: NewExpenseFragmentViewModel by viewModel()
    private val controlation by lazy { findNavController() }
    private var repeat: Boolean = false
    private var installments: Int = 0

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
        getCoinFormatInput()
        setDateInputMaturity()
        clickRepeat()
        clickFixed()
        clickSave()
    }

    private fun clickSave() {
        binding.newExpenseSave.setOnClickListener {
            val description = binding.newExpenseInputDescription.text.toString()
            val value = binding.newExpenseInputValue.text.toString()
            val dataCreated = Date().time
            val maturity = binding.newExpenseInputMaturity.text.toString()
            val date = SimpleDateFormat("dd/MM/yyyy").parse(maturity)
            val installmentsInput = binding.newExpenseInputInstallments.text.toString()

            if (!TextUtils.isEmpty(description) && !TextUtils.isEmpty(value)
            ) {
                if (!TextUtils.isEmpty(installmentsInput)) {
                    installments = installmentsInput.toInt()
                }
                val expense = Expense(
                    description = description,
                    value = value,
                    dateCreated = dataCreated,
                    date = date.time,
                    repeat = repeat,
                    installments = installments,
                )

                viewModel.insertExpense(expense)
                goToExpenseFragment()
            } else {
                if (TextUtils.isEmpty(description)) {
                    binding.newExpenseInputDescription.setError(getString(R.string.required_field_description))
                    binding.newExpenseInputDescription.requestFocus()
                } else {
                    (TextUtils.isEmpty(value))
                    binding.newExpenseInputValue.setError(getString(R.string.required_field_value))
                    binding.newExpenseInputValue.requestFocus()
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
                    binding.newExpenseDiviser.visibility = View.VISIBLE
                    binding.newExpenseFixed.visibility = View.VISIBLE
                    binding.newExpenseInstallments.visibility = View.VISIBLE
                    binding.newExpenseFixed.isChecked = true
                } else -> {
                    repeat = false
                    installments = 0
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
                } else -> {
                    repeat = true
                    binding.newExpenseQtdInstallments.visibility = View.VISIBLE
                    binding.newExpenseInputInstallments.visibility = View.VISIBLE
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
                    goToExpenseFragment()
                }
            }
        }
        return true
    }

    private fun goToExpenseFragment() {
        val navigation = NewExpenseFragmentDirections.actionNewExpenseFragmentToExpenseFragment()
        controlation.navigate(navigation)
    }

    private fun getCoinFormatInput() {
        CoinUtils.addCurrencyMask(binding.newExpenseInputValue)
    }
}

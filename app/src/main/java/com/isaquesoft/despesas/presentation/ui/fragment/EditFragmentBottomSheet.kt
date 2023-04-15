package com.isaquesoft.despesas.presentation.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.databinding.EditFragmentBottomSheetBinding
import com.isaquesoft.despesas.utils.CoinUtils
import com.isaquesoft.despesas.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.*

class EditFragmentBottomSheet(private val expense: Expense, private val actionExpense: (expense: Expense) -> Unit = {}) : BottomSheetDialogFragment() {

    private lateinit var binding: EditFragmentBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = EditFragmentBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCoinFormatInput()
        setDateInputMaturity()

        binding.editExpenseInputDescription.setText(expense.description)
        binding.editExpenseInputValue.setText(expense.value)
        binding.editExpenseInputMaturity.setText(SimpleDateFormat("dd/MM/yyyy").format(expense.date))
        setupBtnSave()
    }

    private fun setupBtnSave() {
        binding.editExpenseSave.setOnClickListener {
            val description = binding.editExpenseInputDescription.text.toString()
            val value = binding.editExpenseInputValue.text.toString()
            val maturity = binding.editExpenseInputMaturity.text.toString()
            val date = SimpleDateFormat("dd/MM/yyyy").parse(maturity)
            val calendar = Calendar.getInstance()
            calendar.time = date

            if (!TextUtils.isEmpty(description) && !TextUtils.isEmpty(value)) {
                val newExpense = Expense(
                    id = expense.id,
                    description = description,
                    value = value,
                    dateCreated = expense.dateCreated,
                    date = calendar.timeInMillis,
                    repeat = expense.repeat,
                    installments = expense.installments,
                    paidOut = expense.paidOut,
                )

                actionExpense.invoke(newExpense)
                dismiss()
            } else {
                if (TextUtils.isEmpty(description)) {
                    binding.editExpenseInputDescription.setError(getString(R.string.required_field_description))
                    binding.editExpenseInputDescription.requestFocus()
                } else {
                    binding.editExpenseInputValue.setError(getString(R.string.required_field_value))
                    binding.editExpenseInputValue.requestFocus()
                }
            }
        }
    }

    private fun getCoinFormatInput() {
        CoinUtils.addCurrencyMask(binding.editExpenseInputValue)
    }

    private fun setDateInputMaturity() {
        binding.editExpenseInputMaturity.setText(
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault(),
            ).format(Date()),
        )
        binding.editExpenseInputMaturity.setOnClickListener {
            DateUtils.exibirDatePickerDialog(requireContext(), binding.editExpenseInputMaturity)
        }
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }
}

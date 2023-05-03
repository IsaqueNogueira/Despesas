package com.isaquesoft.despesas.presentation.ui.fragment

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.databinding.BottomShettPrincipalFragmentBinding
import com.isaquesoft.despesas.presentation.state.ExpenseDetailsState
import com.isaquesoft.despesas.presentation.ui.viewmodel.ExpenseDetailsFramentViewModel
import com.isaquesoft.despesas.utils.IconsCategory
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.Normalizer
import java.text.SimpleDateFormat

class BottomShettPrincipalFragment(
    private val expense: Expense,
    private val actionExpenseDelete: (expense: Expense) -> Unit = {},
    private val actionExpenseDeleteAll: (expense: List<Expense>) -> Unit = {},
    private val actionExpenseUpdate: (expense: Expense) -> Unit = {},
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomShettPrincipalFragmentBinding
    private val viewModel: ExpenseDetailsFramentViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = BottomShettPrincipalFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkExpenseRepeat()
        initTxt()
    }

    private fun checkExpenseRepeat() {
        if (expense.repeat) {
            viewModel.getExpenseRepeat(
                expense.dateCreated,
                expense.value,
                expense.repeat,
                expense.installments,
            )
        }
    }

    private fun String.unaccent(): String {
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return Regex("[^\\p{ASCII}]").replace(temp, "")
    }

    private fun initTxt() {
        binding.bottomSheetPrincipalTitle.text = expense.description
        binding.bottomSheetPrincipalValue.text = expense.value
        binding.bottomSheetPrincipalCategory.text = expense.category
        binding.bottomSheetPrincipalMaturity.text =
            SimpleDateFormat("dd/MM/yyyy").format(expense.date)
        binding.bottomSheetPrincipalEdit.setOnClickListener {
            val bottomSheetDialogFragment = EditFragmentBottomSheet(expense, ::newExpense)
            fragmentManager?.let { it1 ->
                bottomSheetDialogFragment.show(it1, bottomSheetDialogFragment.tag)
            }
        }

        val listIcons = IconsCategory().listIcons(requireContext())

        val itemCategoryIcon = binding.bottomSheetPrincipalIconCategory

        val iconId = listIcons[expense.iconPosition]
        itemCategoryIcon.setImageDrawable(iconId)


        val drawable = itemCategoryIcon.background as GradientDrawable
        drawable.setColor(Color.parseColor(expense.corIcon))

        binding.bottomSheetPrincipalDelete.setOnClickListener {
            if (!expense.repeat) {
                binding.bottomSheetPrincipalDelete.visibility = View.INVISIBLE
                binding.bottomSheetPrincipalDeleteInfo.visibility = View.VISIBLE
                binding.bottomSheetPrincipalBtnDelete.visibility = View.VISIBLE
                binding.bottomSheettBtnPago.visibility = View.GONE
                binding.bottomSheettBtnNaoPago.visibility = View.GONE
                binding.bottomSheetPrincipalClose.visibility = View.VISIBLE
            } else {
                binding.bottomSheetPrincipalDelete.visibility = View.INVISIBLE
                binding.bottomSheetPrincipalDeleteOne.visibility = View.VISIBLE
                binding.bottomSheetPrincipalDeleteAll.visibility = View.VISIBLE
                binding.bottomSheetPrincipalDeleteInfoAll.visibility = View.VISIBLE
                binding.bottomSheettBtnPago.visibility = View.GONE
                binding.bottomSheettBtnNaoPago.visibility = View.GONE
                binding.bottomSheetPrincipalClose.visibility = View.VISIBLE
            }
        }

        binding.bottomSheetPrincipalClose.setOnClickListener {
            binding.bottomSheetPrincipalClose.visibility = View.GONE
            binding.bottomSheetPrincipalDelete.visibility = View.VISIBLE
            binding.bottomSheetPrincipalDeleteInfo.visibility = View.GONE
            binding.bottomSheetPrincipalBtnDelete.visibility = View.GONE
            binding.bottomSheetPrincipalDeleteOne.visibility = View.GONE
            binding.bottomSheetPrincipalDeleteAll.visibility = View.GONE
            binding.bottomSheetPrincipalDeleteInfoAll.visibility = View.GONE
            if (expense.paidOut == true) {
                binding.bottomSheettBtnNaoPago.visibility = View.VISIBLE
                binding.bottomSheettBtnPago.visibility = View.GONE
            } else {
                binding.bottomSheettBtnNaoPago.visibility = View.GONE
                binding.bottomSheettBtnPago.visibility = View.VISIBLE
            }
        }

        when (expense.paidOut) {
            true -> {
                binding.bottomSheetPrincipalIconPago.visibility = View.VISIBLE
                binding.bottomSheetPrincipalIconNaoPago.visibility = View.INVISIBLE
                binding.bottomSheetPrincipalPaidout.text = getString(R.string.paidoutinfo)
                binding.bottomSheettBtnPago.visibility = View.INVISIBLE
                binding.bottomSheettBtnNaoPago.visibility = View.VISIBLE
                binding.bottomSheettBtnNaoPago.setOnClickListener {
                    val newExpense = Expense(
                        id = expense.id,
                        description = expense.description,
                        value = expense.value,
                        dateCreated = expense.dateCreated,
                        date = expense.date,
                        repeat = expense.repeat,
                        installments = expense.installments,
                        paidOut = false,
                        category = expense.category,
                        iconPosition = expense.iconPosition,
                        corIcon = expense.corIcon,
                    )
                    actionExpenseUpdate.invoke(newExpense)
                    dismiss()
                }
            }

            else -> {
                binding.bottomSheetPrincipalIconPago.visibility = View.INVISIBLE
                binding.bottomSheetPrincipalIconNaoPago.visibility = View.VISIBLE
                binding.bottomSheetPrincipalPaidout.text = getString(R.string.paidoutinfo2)
                binding.bottomSheettBtnPago.visibility = View.VISIBLE
                binding.bottomSheettBtnNaoPago.visibility = View.INVISIBLE
                binding.bottomSheettBtnPago.setOnClickListener {
                    val newExpense = Expense(
                        id = expense.id,
                        description = expense.description,
                        value = expense.value,
                        dateCreated = expense.dateCreated,
                        date = expense.date,
                        repeat = expense.repeat,
                        installments = expense.installments,
                        paidOut = true,
                        category = expense.category,
                        iconPosition = expense.iconPosition,
                        corIcon = expense.corIcon,
                    )
                    actionExpenseUpdate.invoke(newExpense)
                    dismiss()
                }
            }
        }

        deleteExpense()
    }

    private fun newExpense(expense: Expense) {
        actionExpenseUpdate.invoke(expense)
        dismiss()
    }

    private fun deleteExpense() {
        binding.bottomSheetPrincipalBtnDelete.setOnClickListener {
            actionExpenseDelete.invoke(expense)
            dismiss()
        }

        binding.bottomSheetPrincipalDeleteOne.setOnClickListener {
            actionExpenseDelete.invoke(expense)
            dismiss()
        }

        binding.bottomSheetPrincipalDeleteAll.setOnClickListener {
            viewModel.expenseDetailsState.observe(viewLifecycleOwner) {
                when (it) {
                    is ExpenseDetailsState.ShowExpenseRepeat -> {
                        actionExpenseDeleteAll.invoke(it.expensesRepeat)
                        dismiss()
                    }
                }
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }
}

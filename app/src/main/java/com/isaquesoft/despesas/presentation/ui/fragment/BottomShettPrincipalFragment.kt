package com.isaquesoft.despesas.presentation.ui.fragment

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.databinding.BottomShettPrincipalFragmentBinding
import java.text.Normalizer
import java.text.SimpleDateFormat

class BottomShettPrincipalFragment(
    private val expense: Expense,
    private val actionExpenseDelete: (expense: Expense) -> Unit = {},
    private val actionExpensePago: (expense: Expense) -> Unit = {},
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomShettPrincipalFragmentBinding
    private val controlation by lazy { findNavController() }
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
        initTxt()
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
            goToExpenseDetailsFragmennt(expense)
            dismiss()
        }

        val itemCategoryIcon = binding.bottomSheetPrincipalIconCategory
        var categoryName = expense.category.toLowerCase()
        categoryName = categoryName.unaccent()
        if (categoryName == "comida e bebida") {
            categoryName = "comidabebida"
        }

        val outros = resources.getIdentifier(
            categoryName,
            "drawable",
            requireContext().packageName,
        )
        itemCategoryIcon.setImageResource(outros)

        val color = resources.getIdentifier(
            categoryName,
            "color",
            requireContext().packageName,
        )
        val drawable = itemCategoryIcon.background as GradientDrawable
        drawable.setColor(ContextCompat.getColor(requireContext(), color))

        binding.bottomSheetPrincipalDelete.setOnClickListener {
            binding.bottomSheetPrincipalDelete.visibility = View.INVISIBLE
            binding.bottomSheetPrincipalDeleteInfo.visibility = View.VISIBLE
            binding.bottomSheetPrincipalBtnDelete.visibility = View.VISIBLE
            binding.bottomSheettBtnPago.visibility = View.GONE
            binding.bottomSheettBtnNaoPago.visibility = View.GONE
            binding.bottomSheetPrincipalClose.visibility = View.VISIBLE
        }

        binding.bottomSheetPrincipalClose.setOnClickListener {
            binding.bottomSheetPrincipalClose.visibility = View.GONE
            binding.bottomSheetPrincipalDelete.visibility = View.VISIBLE
            binding.bottomSheetPrincipalDeleteInfo.visibility = View.GONE
            binding.bottomSheetPrincipalBtnDelete.visibility = View.GONE
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
                    )
                    actionExpensePago.invoke(newExpense)
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
                    )
                    actionExpensePago.invoke(newExpense)
                    dismiss()
                }
            }
        }

        deleteExpense()
    }

    private fun deleteExpense() {
        binding.bottomSheetPrincipalBtnDelete.setOnClickListener {
            actionExpenseDelete.invoke(expense)
            dismiss()
        }
    }

    private fun goToExpenseDetailsFragmennt(expense: Expense) {
        val direction =
            ExpenseFragmentDirections.actionExpenseFragmentToExpenseDetailsFragment(expense)
        controlation.navigate(direction)
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }
}

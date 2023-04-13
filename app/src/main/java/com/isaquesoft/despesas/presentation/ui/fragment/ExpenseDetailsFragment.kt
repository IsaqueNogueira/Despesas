package com.isaquesoft.despesas.presentation.ui.fragment

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment.STYLE_NORMAL
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.databinding.ExpenseDetailsFragmentBinding
import com.isaquesoft.despesas.presentation.state.ExpenseDetailsState
import com.isaquesoft.despesas.presentation.ui.viewmodel.ComponentesVisuais
import com.isaquesoft.despesas.presentation.ui.viewmodel.EstadoAppViewModel
import com.isaquesoft.despesas.presentation.ui.viewmodel.ExpenseDetailsFramentViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat

class ExpenseDetailsFragment : Fragment() {

    private val argument by navArgs<ExpenseDetailsFragmentArgs>()
    private val expense by lazy {
        argument.expense
    }

    private val estadoAppViewModel: EstadoAppViewModel by sharedViewModel()
    private val viewModel: ExpenseDetailsFramentViewModel by viewModel()
    private val controlation by lazy { findNavController() }
    private lateinit var binding: ExpenseDetailsFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = ExpenseDetailsFragmentBinding.inflate(layoutInflater, container, false)
        binding.root.setBackgroundColor(Color.TRANSPARENT)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = getString(R.string.title_fragment_details)
        setHasOptionsMenu(true)
        estadoAppViewModel.temComponentes = ComponentesVisuais(true, true)
        checkExpenseRepeat()
        setTextExpenseDetails()
        setupCheckBox()
        setupButtonDelete()
        setupButtonEdit()
    }

    private fun setupButtonEdit() {
        binding.expenseDetailsButtonEdit.setOnClickListener {
            val bottomSheetDialogFragment = EditFragmentBottomSheet(expense, ::newExpense)
            bottomSheetDialogFragment.setStyle(STYLE_NORMAL, R.style.MyBottomSheetDialogTheme)
            fragmentManager?.let { it1 -> bottomSheetDialogFragment.show(it1, bottomSheetDialogFragment.tag) }
        }
    }

    private fun checkExpenseRepeat() {
        if (expense.repeat) {
            viewModel.getExpenseRepeat(expense.dateCreated, expense.value, expense.repeat, expense.installments)
        }
    }

    private fun newExpense(expense: Expense) {
        viewModel.updateExpense(expense)
        requireActivity().onBackPressed()
    }

    private fun setupCheckBox() {
        binding.expenseDetailsCheckboxPaidout.isChecked = expense.paidOut == true

        binding.expenseDetailsCheckboxPaidout.setOnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isChecked) {
                val newExpense = Expense(
                    id = expense.id,
                    description = expense.description,
                    value = expense.value,
                    dateCreated = expense.dateCreated,
                    date = expense.date,
                    repeat = expense.repeat,
                    installments = expense.installments,
                    paidOut = true,
                )
                viewModel.updateExpense(newExpense)
                requireActivity().onBackPressed()
            } else {
                val newExpense = Expense(
                    id = expense.id,
                    description = expense.description,
                    value = expense.value,
                    dateCreated = expense.dateCreated,
                    date = expense.date,
                    repeat = expense.repeat,
                    installments = expense.installments,
                    paidOut = false,
                )
                viewModel.updateExpense(newExpense)
                requireActivity().onBackPressed()
            }
        }
    }

    private fun setupButtonDelete() {
        binding.expenseDetailsButtonDelete.setOnClickListener {
            setupAlertDialogDelete()
        }
    }

    fun setupAlertDialogDelete() {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.alert_dialog_delet, null)
        val alertDialog = AlertDialog.Builder(context)
            .setView(view)
            .create()
        alertDialog.setOnShowListener {
            view.apply {
                val alertDeletTitle = findViewById<TextView>(R.id.alert_delet_title)
                val alertDeleteButtonIt = findViewById<Button>(R.id.alert_delet_button_it)
                val alertDeleteButtonAll = findViewById<Button>(R.id.alert_delet_button_all)
                val alertDeleteDescription = findViewById<TextView>(R.id.alert_delet_description)
                val alertBtnClose = findViewById<ImageButton>(R.id.alert_delet_btn_close)
                if (!expense.repeat) {
                    alertDeletTitle.text = getString(R.string.alert_delet_info)
                    alertDeleteButtonIt.text = getString(R.string.delete)
                    alertDeleteDescription.visibility = View.GONE
                } else {
                    alertDeletTitle.text = getString(R.string.expense_delete_infor_title)
                    alertDeleteButtonIt.text = getString(R.string.expense_delete_info_it)
                    alertDeleteButtonAll.visibility = View.VISIBLE
                    alertDeleteDescription.visibility = View.VISIBLE
                }
                val buttonPositiveIt = findViewById<Button>(R.id.alert_delet_button_it)
                val buttonPositiveAll = findViewById<Button>(R.id.alert_delet_button_all)
                buttonPositiveIt.setOnClickListener {
                    viewModel.deleteExpense(expense)
                    requireActivity().onBackPressed()
                    alertDialog.dismiss()
                }

                alertBtnClose.setOnClickListener {
                    alertDialog.dismiss()
                }

                buttonPositiveAll.setOnClickListener {
                    viewModel.expenseDetailsState.observe(viewLifecycleOwner) {
                        when (it) {
                            is ExpenseDetailsState.ShowExpenseRepeat -> {
                                viewModel.deleteAllExpenseRepeat(it.expensesRepeat)
                                requireActivity().onBackPressed()
                                alertDialog.dismiss()
                            }
                        }
                    }
                }
            }
        }

        alertDialog.show()
    }

    private fun setTextExpenseDetails() {
        binding.expenseDetailsDescription.text = expense.description
        binding.expenseDetailsValue.text = expense.value
        binding.expenseDetailsDate.text = SimpleDateFormat("dd/MM/yyyy").format(expense.date)
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

    private fun goToExpenseFragment() {
        val navigation =
            ExpenseDetailsFragmentDirections.actionExpenseDetailsFragmentToExpenseFragment()
        controlation.navigate(navigation)
    }
}

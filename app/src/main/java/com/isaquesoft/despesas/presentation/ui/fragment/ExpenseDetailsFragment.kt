package com.isaquesoft.despesas.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.databinding.ExpenseDetailsFragmentBinding
import com.isaquesoft.despesas.presentation.ui.viewmodel.ComponentesVisuais
import com.isaquesoft.despesas.presentation.ui.viewmodel.EstadoAppViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.SimpleDateFormat

class ExpenseDetailsFragment : Fragment() {

    private val argument by navArgs<ExpenseDetailsFragmentArgs>()
    private val expense by lazy {
        argument.expense
    }

    private val estadoAppViewModel: EstadoAppViewModel by sharedViewModel()
    private val controlation by lazy { findNavController() }
    private lateinit var binding: ExpenseDetailsFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = ExpenseDetailsFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = getString(R.string.title_fragment_details)
        setHasOptionsMenu(true)
        estadoAppViewModel.temComponentes = ComponentesVisuais(true, true)
        setTextExpenseDetails()
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
                    goToExpenseFragment()
                }
            }
        }
        return true
    }

    private fun goToExpenseFragment() {
        val navigation = ExpenseDetailsFragmentDirections.actionExpenseDetailsFragmentToExpenseFragment()
        controlation.navigate(navigation)
    }
}

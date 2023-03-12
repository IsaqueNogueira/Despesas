package com.isaquesoft.despesas.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.databinding.NewExpenseFragmentBinding
import com.isaquesoft.despesas.presentation.ui.viewmodel.ComponentesVisuais
import com.isaquesoft.despesas.presentation.ui.viewmodel.EstadoAppViewModel
import com.isaquesoft.despesas.utils.CoinUtils
import com.isaquesoft.despesas.utils.DateUtils
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.SimpleDateFormat
import java.util.*

class NewExpenseFragment : Fragment() {

    private lateinit var binding: NewExpenseFragmentBinding
    private val estadoAppViewModel: EstadoAppViewModel by sharedViewModel()
    private val controlation by lazy { findNavController() }
    private var repeat: Boolean = false

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

        binding.newExpenseNoRepeat.isChecked = true
        binding.newExpenseNoRepeat.setOnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isPressed) {
                if (compoundButton.isChecked) {
                    binding.newExpenseRepeat.isChecked = false
                    binding.newExpenseDiviser.visibility = View.GONE
                    binding.newExpenseFixed.visibility = View.GONE
                    binding.newExpenseInstallments.visibility = View.GONE
                    binding.newExpenseInputInstallments.visibility = View.GONE
                }
            }
        }
        binding.newExpenseRepeat.setOnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isPressed) {
                if (compoundButton.isChecked) {
                    binding.newExpenseNoRepeat.isChecked = false
                    binding.newExpenseDiviser.visibility = View.VISIBLE
                    binding.newExpenseFixed.visibility = View.VISIBLE
                    binding.newExpenseInstallments.visibility = View.VISIBLE
                    binding.newExpenseFixed.isChecked = true
                    binding.newExpenseInstallments.isChecked = false
                }
            }
        }

        binding.newExpenseFixed.setOnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isPressed) {
                if (compoundButton.isChecked) {
                    binding.newExpenseInstallments.isChecked = false
                    binding.newExpenseInputInstallments.visibility = View.GONE
                }
            }
        }

        binding.newExpenseInstallments.setOnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isPressed) {
                if (compoundButton.isChecked) {
                    binding.newExpenseInputInstallments.visibility = View.VISIBLE
                    binding.newExpenseFixed.isChecked = false
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

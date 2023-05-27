package com.isaquesoft.despesas.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.Category
import com.isaquesoft.despesas.databinding.BottomSheetFilterFragmentBinding
import com.isaquesoft.despesas.presentation.ui.SharedPreferences
import com.isaquesoft.despesas.presentation.ui.adapter.AdapterCategory

class BottomSheetFilterFragment(
    private val listCategory: List<Category>,
    private val filtroAplicado: Boolean,
    private val categoriaSelecionada: String,
    private val actionOrdemAplicada: () -> Unit = {},
    private val actionFilterCategory: (category: Category) -> Unit = {},
    private val actionFilterLimpar: () -> Unit,
) :
    BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetFilterFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BottomSheetFilterFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOrder()
        showCategory()
        setupLimparFiltro()
        binding.filterBottomSheetClose.setOnClickListener {
            dismiss()
        }
    }

    private fun setupLimparFiltro() {
        if (filtroAplicado) {
            (binding.filterCategoryRecyclerview.adapter as AdapterCategory).selected(categoriaSelecionada)
            binding.filterBtnLimpar.visibility = View.VISIBLE
            binding.filterBtnLimpar.setOnClickListener {
                actionFilterLimpar.invoke()
                dismiss()
            }
        } else {
            binding.filterBtnLimpar.visibility = View.GONE
        }
    }

    private fun showCategory() {
        binding.filterCategoryRecyclerview.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.filterCategoryRecyclerview.adapter =
            AdapterCategory(requireContext(), listCategory.toMutableList(), ::clickCategory)
    }

    private fun clickCategory(category: Category) {
        actionFilterCategory.invoke(category)
        dismiss()
    }

    private fun setupOrder() {
        val settingsOrdemDescription = binding.ordenmDescription
        val settingsOrdemDataDesc = binding.ordenmDateDesc
        val settingsOrdemDataCres = binding.ordenmDateCres

        val getOrdemSelected = SharedPreferences(requireContext()).getOrdemList()

        when (getOrdemSelected) {
            SettingsFragment.LISTA_AZ -> settingsOrdemDescription.isChecked = true
            SettingsFragment.LISTA_DESC -> settingsOrdemDataDesc.isChecked = true
            SettingsFragment.LISTA_CRES -> settingsOrdemDataCres.isChecked = true

            else -> {
                settingsOrdemDescription.isChecked = false
                settingsOrdemDataDesc.isChecked = false
                settingsOrdemDataCres.isChecked = false
            }
        }

        settingsOrdemDescription.setOnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isChecked) {
                SharedPreferences(requireContext()).setOrdemList(SettingsFragment.LISTA_AZ)
                actionOrdemAplicada.invoke()
            }
        }

        settingsOrdemDataDesc.setOnCheckedChangeListener { compoundButton, _ ->
            if (compoundButton.isChecked) {
                SharedPreferences(requireContext()).setOrdemList(SettingsFragment.LISTA_DESC)
                actionOrdemAplicada.invoke()
            }
        }

        settingsOrdemDataCres.setOnCheckedChangeListener { compoundButton, _ ->
            if (compoundButton.isChecked) {
                SharedPreferences(requireContext()).setOrdemList(SettingsFragment.LISTA_CRES)
                actionOrdemAplicada.invoke()
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }
}

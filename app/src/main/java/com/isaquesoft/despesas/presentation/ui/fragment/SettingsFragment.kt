package com.isaquesoft.despesas.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.databinding.SettingsFragmentBinding
import com.isaquesoft.despesas.presentation.ui.SharedPreferences
import com.isaquesoft.despesas.presentation.ui.viewmodel.ComponentesVisuais
import com.isaquesoft.despesas.presentation.ui.viewmodel.EstadoAppViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SettingsFragment : Fragment() {

    private lateinit var binding: SettingsFragmentBinding
    private val estadoAppViewModel: EstadoAppViewModel by sharedViewModel()
    private val controlation by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = SettingsFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        estadoAppViewModel.temComponentes = ComponentesVisuais(true, true)
        requireActivity().title = getString(R.string.settings)

        setupSettings()
        setupBackup()
    }

    private fun setupBackup() {
        binding.settingsCardviewBackup.setOnClickListener {
            goToBackupFragment()
        }
    }

    private fun setupSettings() {
        val settingsOrdemDescription = binding.settingsOrdenmDescription
        val settingsOrdemDataDesc = binding.settingsOrdenmDateDesc
        val settingsOrdemDataCres = binding.settingsOrdenmDateCres

        setupOrdemDescription(
            settingsOrdemDescription,
            settingsOrdemDataDesc,
            settingsOrdemDataCres,
        )

        setupOrdemDateDesc(settingsOrdemDescription, settingsOrdemDataDesc, settingsOrdemDataCres)
        setupOrdemDateCres(settingsOrdemDescription, settingsOrdemDataDesc, settingsOrdemDataCres)
    }

    private fun setupOrdemDescription(
        settingsOrdemDescription: SwitchCompat,
        settingsOrdemDataDesc: SwitchCompat,
        settingsOrdemDataCres: SwitchCompat,
    ) {
        if (SharedPreferences(requireContext()).getOrdemList() == LISTA_AZ) {
            settingsOrdemDescription.isChecked = true
            settingsOrdemDataDesc.isChecked = false
            settingsOrdemDataCres.isChecked = false
        }
        settingsOrdemDescription.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                SharedPreferences(requireContext()).setOrdemList(LISTA_AZ)
                settingsOrdemDataDesc.isChecked = false
                settingsOrdemDataCres.isChecked = false
            } else {
                SharedPreferences(requireContext()).setOrdemList("A-Z false")
            }
        }
    }

    private fun setupOrdemDateDesc(
        settingsOrdemDescription: SwitchCompat,
        settingsOrdemDataDesc: SwitchCompat,
        settingsOrdemDataCres: SwitchCompat,
    ) {
        if (SharedPreferences(requireContext()).getOrdemList() == LISTA_DESC) {
            settingsOrdemDescription.isChecked = false
            settingsOrdemDataDesc.isChecked = true
            settingsOrdemDataCres.isChecked = false
        }
        settingsOrdemDataDesc.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                SharedPreferences(requireContext()).setOrdemList(LISTA_DESC)
                settingsOrdemDescription.isChecked = false
                settingsOrdemDataCres.isChecked = false
            } else {
                SharedPreferences(requireContext()).setOrdemList("Date Desc false")
            }
        }
    }

    private fun setupOrdemDateCres(
        settingsOrdemDescription: SwitchCompat,
        settingsOrdemDataDesc: SwitchCompat,
        settingsOrdemDataCres: SwitchCompat,
    ) {
        if (SharedPreferences(requireContext()).getOrdemList() == LISTA_CRES) {
            settingsOrdemDescription.isChecked = false
            settingsOrdemDataDesc.isChecked = false
            settingsOrdemDataCres.isChecked = true
        }
        settingsOrdemDataCres.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                SharedPreferences(requireContext()).setOrdemList(LISTA_CRES)
                settingsOrdemDescription.isChecked = false
                settingsOrdemDataDesc.isChecked = false
            } else {
                SharedPreferences(requireContext()).setOrdemList("Date Cres false")
            }
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
        val navigation =
            SettingsFragmentDirections.actionSettingsFragmentToExpenseFragment()
        controlation.navigate(navigation)
    }

    private fun goToBackupFragment() {
        val navigation =
            SettingsFragmentDirections.actionSettingsFragmentToBackupFragment()
        controlation.navigate(navigation)
    }

    companion object {
        const val LISTA_AZ = "A-Z true"
        const val LISTA_DESC = "Date Desc true"
        const val LISTA_CRES = "Date Cres true"
    }
}

package com.isaquesoft.despesas.presentation.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.databinding.SettingsFragmentBinding
import com.isaquesoft.despesas.presentation.ui.SharedPreferences
import com.isaquesoft.despesas.presentation.ui.activity.AssinaturaActivity
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
        setupBackup()
        openSettingsNotificationSystem()
        goToAssinaturaActivity()
        compartilharApp()
        abrirPaginaClassificacao()

        // DESABILITADO ATÉ COLOCAR O APP EM PRODUÇÃO E CADASTRAR OS PRODUTOS DE ASSINATURA NO GOOGLE PLAY CONSOLE
        binding.settingsBtnPro.isEnabled = false
    }

    private fun openSettingsNotificationSystem() {
        binding.settingsCardviewNotification.setOnClickListener {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
            startActivity(intent)
        }
    }

    private fun setupBackup() {
        binding.settingsCardviewBackup.setOnClickListener {
            if (SharedPreferences(requireContext()).getVerifyCompraAssinatura()) {
                goToBackupFragment()
            } else {
                val intent = Intent(requireActivity(), AssinaturaActivity::class.java)
                startActivity(intent)
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

    private fun compartilharApp() {
        binding.settingsCardviewCompartilhe.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.message_compartilhar))
            startActivity(Intent.createChooser(intent, "Compartilhar via"))
        }
    }
    private fun abrirPaginaClassificacao() {
        binding.settingsCardviewAvalie.setOnClickListener {
            val uri =
                Uri.parse("https://play.google.com/store/apps/details?id=com.isaquesoft.despesas")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    private fun goToBackupFragment() {
        val navigation =
            SettingsFragmentDirections.actionSettingsFragmentToBackupFragment()
        controlation.navigate(navigation)
    }

    private fun goToAssinaturaActivity() {
        binding.settingsBtnPro.setOnClickListener {
            val intent = Intent(requireActivity(), AssinaturaActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        const val LISTA_AZ = "A-Z true"
        const val LISTA_DESC = "Date Desc true"
        const val LISTA_CRES = "Date Cres true"
    }
}

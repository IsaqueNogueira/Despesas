package com.isaquesoft.despesas.presentation.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.databinding.BackupFragmentBinding
import com.isaquesoft.despesas.presentation.state.BackupState
import com.isaquesoft.despesas.presentation.ui.viewmodel.BackupFragmentViewModel
import com.isaquesoft.despesas.presentation.ui.viewmodel.ComponentesVisuais
import com.isaquesoft.despesas.presentation.ui.viewmodel.EstadoAppViewModel
import com.isaquesoft.despesas.utils.CustomToast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader

class BackupFragment() : Fragment() {

    private lateinit var binding: BackupFragmentBinding
    private val estadoAppViewModel: EstadoAppViewModel by sharedViewModel()
    private val viewModel: BackupFragmentViewModel by viewModel()
    private val controlation by lazy { findNavController() }
    private var expensesExistentesVerify = mutableListOf<Expense>()

    private lateinit var expenseListJson: String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = BackupFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = getString(R.string.backup)
        setHasOptionsMenu(true)
        estadoAppViewModel.temComponentes = ComponentesVisuais(appBar = true, temBotaoBack = true)
        viewModel.getAllExpenses()
        initViewModel()
        binding.backupCardviewImportar.setOnClickListener {
        }
    }

    private fun initViewModel() {
        viewModel.backupState.observe(viewLifecycleOwner) {
            when (it) {
                is BackupState.ShowExpenses -> showExpenses(it.expenses)
            }
        }
    }

    private fun showExpenses(expenses: List<Expense>) {
        if (expenses.isNotEmpty()) {
            expensesExistentesVerify.addAll(expenses)
        }
        binding.backupCardviewExportar.setOnClickListener {
            if (expenses.isNotEmpty()) {
                exportExpenses(expenses)
            } else {
                CustomToast(requireContext(), getString(R.string.lista_backup_vazia)).show()
            }
        }

        binding.backupCardviewImportar.setOnClickListener {
            importExpenses()
        }
    }

    fun exportExpenses(expenses: List<Expense>) {
        val gson = Gson()
        expenseListJson = gson.toJson(expenses)

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, "expenses.txt")
        }
        startActivityForResult(intent, WRITE_REQUEST_CODE)
    }

    private fun importExpenses() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
        }
        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        if (requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            resultData?.data?.let { uri ->
                requireContext().contentResolver.openFileDescriptor(uri, "w")?.use { descriptor ->
                    FileOutputStream(descriptor.fileDescriptor).use { fos ->
                        fos.write(expenseListJson.toByteArray())
                        CustomToast(requireContext(), getString(R.string.exportado_sucesso))
                    }
                }
            }
        }

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            resultData?.data?.let { uri ->
                requireContext().contentResolver.openFileDescriptor(uri, "r")?.use { descriptor ->
                    BufferedReader(InputStreamReader(FileInputStream(descriptor.fileDescriptor))).use { reader ->
                        val expensesJson = reader.readText()
                        val expenses = Gson().fromJson<List<Expense>>(expensesJson, object : TypeToken<List<Expense>>() {}.type)
                        processImportedExpenses(expenses)
                    }
                }
            }
        }
    }

    private fun processImportedExpenses(expenses: List<Expense>) {
        if (expensesExistentesVerify.isEmpty()) {
            viewModel.insertAllExpenses(expenses)
            CustomToast(requireContext(), getString(R.string.importado_backup_sucesso)).show()
        } else {
            var itemDiferenteEncontrado = false
            for (item in expenses) {
                if (!expensesExistentesVerify.contains(item)) {
                    viewModel.insertExpense(item)
                    itemDiferenteEncontrado = true
                }
            }
            if (itemDiferenteEncontrado) {
                CustomToast(requireContext(), getString(R.string.importado_backup_sucesso)).show()
            } else {
                CustomToast(requireContext(), getString(R.string.importado_backup_existente)).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (activity != null) {
                    goToSettingsFragment()
                }
            }
        }
        return true
    }

    private fun goToSettingsFragment() {
        val navigation =
            BackupFragmentDirections.actionBackupFragmentToSettingsFragment()
        controlation.navigate(navigation)
    }

    companion object {
        private const val WRITE_REQUEST_CODE = 1
        private const val READ_REQUEST_CODE = 2
    }
}

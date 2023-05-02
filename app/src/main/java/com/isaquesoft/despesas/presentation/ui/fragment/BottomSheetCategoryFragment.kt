package com.isaquesoft.despesas.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.Category
import com.isaquesoft.despesas.databinding.BottomSheetCategoryFragmentBinding
import com.isaquesoft.despesas.presentation.ui.adapter.AdapterCategoryBottomSheet

class BottomSheetCategoryFragment(
    private val category: List<Category>,
    private val updateCategory: (category: Category) -> Unit,
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetCategoryFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = BottomSheetCategoryFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerview()
    }

    private fun initRecyclerview() {
        val recyclerView = binding.bottomSheetCategoryRecyclerview
        recyclerView.adapter = AdapterCategoryBottomSheet(requireContext(), category, ::clickItem)
    }

    private fun clickItem(category: Category) {
        updateCategory.invoke(category)
        dismiss()
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }
}

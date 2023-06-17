package com.isaquesoft.despesas.presentation.ui.fragment

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.Category
import com.isaquesoft.despesas.data.model.ColorString
import com.isaquesoft.despesas.databinding.CategoryFragmentBinding
import com.isaquesoft.despesas.presentation.state.CategoryState
import com.isaquesoft.despesas.presentation.ui.adapter.AdapterCategory
import com.isaquesoft.despesas.presentation.ui.adapter.AdapterColor
import com.isaquesoft.despesas.presentation.ui.adapter.AdapterIcons
import com.isaquesoft.despesas.presentation.ui.viewmodel.CategoryFragmentViewModel
import com.isaquesoft.despesas.presentation.ui.viewmodel.ComponentesVisuais
import com.isaquesoft.despesas.presentation.ui.viewmodel.EstadoAppViewModel
import com.isaquesoft.despesas.utils.ColorCategorys
import com.isaquesoft.despesas.utils.IconsCategory
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CategoryFragment : Fragment() {

    private lateinit var binding: CategoryFragmentBinding
    private val estadoAppViewModel: EstadoAppViewModel by sharedViewModel()
    private val viewModel: CategoryFragmentViewModel by viewModel()
    private lateinit var listColors: List<ColorString>
    private var color: String = "#299A9A"
    private lateinit var iconImage: Drawable
    private lateinit var iconColor: View
    private lateinit var iconView: View
    private lateinit var drawablesArray: MutableList<Drawable>
    private var positionIcon = 6
    private var editCategory = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = CategoryFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = getString(R.string.category)
        setHasOptionsMenu(true)
        estadoAppViewModel.temComponentes = ComponentesVisuais(appBar = true, temBotaoBack = true)
        viewModel.getAllCategory()
        initViewModel()
        initNewCategory()
        listColors = ColorCategorys().colors()
        drawablesArray = IconsCategory().listIcons(requireContext())
    }

    private fun initNewCategory() {
        binding.floatingActionButton.setOnClickListener {
            editCategory = false
            dialogNewCategory(null)
        }
    }

    private fun initViewModel() {
        viewModel.categoryState.observe(viewLifecycleOwner) {
            when (it) {
                is CategoryState.ShowCategory -> showCategory(it.category)
            }
        }
    }

    private fun showCategory(category: List<Category>) {
        binding.categoryRecyclerview.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.categoryRecyclerview.adapter =
            AdapterCategory(requireContext(), category.toMutableList(), ::clickCategory, ::clickLongCategory)
    }

    private fun clickCategory(category: Category) {
        editCategory = true
        dialogNewCategory(category)
    }

    private fun clickLongCategory(category: Category) {
        sutupDialogDelete(category)
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

    private fun dialogNewCategory(category: Category?) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_new_category, null)
        val alertDialog = AlertDialog.Builder(context)
            .setView(view)
            .create()
        alertDialog.setOnShowListener {
            view.apply {
                val buttonClose = findViewById<View>(R.id.alert_category_close)
                val buttonColor = findViewById<Button>(R.id.alert_category_button_color)
                val buttonIcon = findViewById<Button>(R.id.alert_category_button_icon)
                val editText = findViewById<EditText>(R.id.alert_category_edit_text)
                val txtTitle = findViewById<TextView>(R.id.alert_category_title)
                iconView = findViewById(R.id.alert_category_view_icon)
                iconColor = findViewById(R.id.alert_category_view_color)
                iconColor.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
                editText.requestFocus()
                buttonColor.setOnClickListener {
                    dialogColors()
                }

                buttonIcon.setOnClickListener {
                    dialogIcon()
                }

                if (editCategory) {
                    category?.let {
                        colorEditApply(it.cor)
                        iconEditApply(it.iconPosition)
                        editText.requestFocus()
                        editText.setText(it.category)
                        txtTitle.text = requireContext().getString(R.string.editar_categoria)
                    }
                }

                buttonClose.setOnClickListener { alertDialog.dismiss() }
                val buttonPositive = findViewById<Button>(R.id.alert_category_save_button)
                buttonPositive.setOnClickListener {
                    val nomeCategory = editText.text.toString()
                    if (!TextUtils.isEmpty(nomeCategory)) {
                        if (editCategory) {
                            category?.let {
                                val updateCategory = Category(category.id, nomeCategory, positionIcon, color)
                                viewModel.updateCategory(updateCategory)
                                (binding.categoryRecyclerview.adapter as AdapterCategory).updateCategory(
                                    updateCategory,
                                )
                            }
                        } else {
                            val newCategory = Category(
                                category = nomeCategory,
                                iconPosition = positionIcon,
                                cor = color,
                            )
                            viewModel.newCategory(newCategory)
                            (binding.categoryRecyclerview.adapter as AdapterCategory).atualiza(
                                newCategory,
                            )
                        }
                        alertDialog.dismiss()
                    } else {
                        editText.setError(getString(R.string.required))
                        editText.requestFocus()
                    }
                }
            }
        }

        alertDialog.show()
    }

    private fun dialogIcon() {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_icon, null)
        val alertDialog = AlertDialog.Builder(context)
            .setView(view)
            .create()

        alertDialog.setOnShowListener {
            view.apply {
                val btnClose = findViewById<View>(R.id.alert_icon_close)
                val recyclerView = findViewById<RecyclerView>(R.id.alert_icon_recyclerview)
                recyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
                recyclerView.adapter = AdapterIcons(drawablesArray) { icon, position ->
                    clickIcon(icon, alertDialog, position)
                }

                btnClose.setOnClickListener {
                    alertDialog.dismiss()
                }
            }
        }

        alertDialog.show()
    }

    private fun dialogColors() {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_colors, null)
        val alertDialog = AlertDialog.Builder(context)
            .setView(view)
            .create()
        alertDialog.setOnShowListener {
            view.apply {
                val recyclerView = findViewById<RecyclerView>(R.id.alert_color_recyclerview)
                recyclerView.layoutManager = GridLayoutManager(requireContext(), 6)
                recyclerView.adapter = AdapterColor(listColors) { colorString ->
                    clickItem(colorString, alertDialog)
                }
            }
        }

        alertDialog.show()
    }

    fun sutupDialogDelete(category: Category) {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.alert_dialog_standard, null)
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
        alertDialog.setOnShowListener {
            view.apply {
                val txtTitle = findViewById<TextView>(R.id.alert_title)
                val txtMessage = findViewById<TextView>(R.id.alert_message)
                val buttonClose = findViewById<View>(R.id.alert_close)

                txtTitle.text = getString(R.string.deletecategory)
                txtMessage.text = getString(R.string.nodesfeita)
                val buttonPositive = findViewById<Button>(R.id.alert_button)
                buttonPositive.setOnClickListener {
                    viewModel.deleteCategory(category)
                    (binding.categoryRecyclerview.adapter as AdapterCategory).remove(category)
                    alertDialog.dismiss()
                }

                buttonClose.setOnClickListener { alertDialog.dismiss() }
            }
        }

        alertDialog.show()
    }

    private fun clickItem(colorString: ColorString, alertDialog: AlertDialog) {
        color = colorString.color
        alertDialog.dismiss()
        iconColor.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
    }

    private fun clickIcon(iconImg: Drawable, alertDialog: AlertDialog, position: Int) {
        iconImage = iconImg
        positionIcon = position
        alertDialog.dismiss()
        iconView.background = iconImg
    }

    private fun colorEditApply(color: String) {
        this.color = color
        iconColor.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
    }

    private fun iconEditApply(iconPosition: Int) {
        this.iconImage = drawablesArray[iconPosition]
        positionIcon = iconPosition
        iconView.background = drawablesArray[iconPosition]
    }
}

package com.isaquesoft.despesas.presentation.ui.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.Category
import java.text.Normalizer

class AdapterCategoryBottomSheet(
    private val category: List<Category>,
    private val clickItem: (category: Category) -> Unit = {},
) :
    RecyclerView.Adapter<AdapterCategoryBottomSheet.ViewHolder>() {

    private fun String.unaccent(): String {
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return Regex("[^\\p{ASCII}]").replace(temp, "")
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val context = itemView.context
        val imageViewIcon = itemView.findViewById<ImageView>(R.id.item_category_bottom_sheet_icon)
        val categoryTxt = itemView.findViewById<TextView>(R.id.item_category_bottom_sheet_txt)
        val diviser = itemView.findViewById<View>(R.id.item_category_bottom_sheet_diviser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_bottom_sheet, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return category.size
    }

    override fun onBindViewHolder(holder: AdapterCategoryBottomSheet.ViewHolder, position: Int) {
        val category = category[position]
        var categoryName = category.category.toLowerCase()
        categoryName = categoryName.unaccent()
        if (categoryName == "comida e bebida") {
            categoryName = "comidabebida"
        }

        holder.categoryTxt.text = category.category

        val outros = holder.context.resources.getIdentifier(
            categoryName,
            "drawable",
            holder.context.packageName,
        )
        holder.imageViewIcon.setImageResource(outros)

        val color = holder.context.resources.getIdentifier(
            categoryName,
            "color",
            holder.context.packageName,
        )
        val drawable = holder.imageViewIcon.background as GradientDrawable
        drawable.setColor(ContextCompat.getColor(holder.context, color))

        holder.itemView.setOnClickListener {
            clickItem.invoke(category)
        }
    }
}

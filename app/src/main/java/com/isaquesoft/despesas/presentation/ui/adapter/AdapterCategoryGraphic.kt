package com.isaquesoft.despesas.presentation.ui.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.CategoryGraphic
import com.isaquesoft.despesas.utils.IconsCategory

class AdapterCategoryGraphic(
    context: Context,
    private val listCategory: MutableList<CategoryGraphic>,
) :
    RecyclerView.Adapter<AdapterCategoryGraphic.ViewHolder>() {

    private val listIcons = IconsCategory().listIcons(context)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val context = itemView.context
        val categoryTxt = itemView.findViewById<TextView>(R.id.item_category_graphich_txt)
        val iconCategory = itemView.findViewById<ImageView>(R.id.item_category_graphich_icon)
        val itemDiviserColorCategory = itemView.findViewById<View>(R.id.item_diviser_color_category_graphic)
        val itemCategoryValueTxt = itemView.findViewById<TextView>(R.id.item_category_graphich_value)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterCategoryGraphic.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_category_graphic, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterCategoryGraphic.ViewHolder, position: Int) {
        val category = listCategory[position]

        holder.categoryTxt.text = category.category

        holder.itemCategoryValueTxt.text = category.value

        val iconId = listIcons[category.iconPosition]

        holder.iconCategory.setImageDrawable(iconId)

        val drawable = holder.iconCategory.background as GradientDrawable
        drawable.setColor(Color.parseColor(category.corIcon))

        holder.itemDiviserColorCategory.setBackgroundColor(Color.parseColor(category.corIcon))

    }

    override fun getItemCount(): Int {
        return listCategory.size
    }

}
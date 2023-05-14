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
import com.isaquesoft.despesas.data.model.Category
import com.isaquesoft.despesas.utils.IconsCategory

class AdapterCategory(
    context: Context,
    private val listCategory: MutableList<Category>,
    private val clickCategory: (category: Category) -> Unit = {},
) :
    RecyclerView.Adapter<AdapterCategory.ViewHolder>() {

    private val listIcons = IconsCategory().listIcons(context)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val context = itemView.context
        val categoryTxt = itemView.findViewById<TextView>(R.id.item_category_txt)
        val iconCategory = itemView.findViewById<ImageView>(R.id.item_category_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterCategory.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterCategory.ViewHolder, position: Int) {
        val category = listCategory[position]

        holder.categoryTxt.text = category.category

        val iconId = listIcons[category.iconPosition]

        holder.iconCategory.setImageDrawable(iconId)

        val drawable = holder.iconCategory.background as GradientDrawable
        drawable.setColor(Color.parseColor(category.cor))

        holder.itemView.setOnLongClickListener{
            clickCategory(category)
            true
        }
    }

    override fun getItemCount(): Int {
        return listCategory.size
    }

    fun atualiza(newCategory: Category) {
        this.listCategory.add(newCategory)
        notifyDataSetChanged()
    }

    fun remove(category: Category) {
        this.listCategory.remove(category)
        notifyDataSetChanged()
    }
}

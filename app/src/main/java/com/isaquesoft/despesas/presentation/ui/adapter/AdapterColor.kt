package com.isaquesoft.despesas.presentation.ui.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.ColorString

class AdapterColor(
    private val listColor: List<ColorString>,
    private val clickItem: (expense: ColorString) -> Unit = {},
) : RecyclerView.Adapter<AdapterColor.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val context = itemView.context
        val colorView = itemView.findViewById<View>(R.id.item_color_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterColor.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_color, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterColor.ViewHolder, position: Int) {
        val color = listColor[position]
        val colorInt = Color.parseColor(color.color)
        holder.colorView.backgroundTintList = ColorStateList.valueOf(colorInt)

        holder.itemView.setOnClickListener {
            clickItem.invoke(color)
        }
    }

    override fun getItemCount(): Int {
        return listColor.size
    }
}

package com.isaquesoft.despesas.presentation.ui.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.isaquesoft.despesas.R

class AdapterIcons(
    private val listIcons: MutableList<Drawable>,
    private val clickIcon: (icon: Drawable, position : Int) -> Unit = { _, _ -> },
) : RecyclerView.Adapter<AdapterIcons.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val context = itemView.context
        val iconView = itemView.findViewById<View>(R.id.item_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterIcons.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_icon, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterIcons.ViewHolder, position: Int) {
        val icon = listIcons[position]
        holder.iconView.background = icon

        holder.itemView.setOnClickListener {
            clickIcon.invoke(icon, position)
        }
    }

    override fun getItemCount(): Int {
        return listIcons.size
    }
}

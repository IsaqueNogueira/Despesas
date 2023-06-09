package com.isaquesoft.despesas.presentation.ui.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.utils.IconsCategory
import java.text.SimpleDateFormat

class AdapterExpense(
    context: Context,
    private val listExpense: MutableList<Expense>,
    private val clickItem: (expense: Expense) -> Unit = {},
) : RecyclerView.Adapter<AdapterExpense.ViewHolder>() {

    private val listIcons = IconsCategory().listIcons(context)
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val context = itemView.context
        val txtDescription = itemView.findViewById<TextView>(R.id.item_expense_description)
        val txtValue = itemView.findViewById<TextView>(R.id.item_expense_value)
        val txtMaturity = itemView.findViewById<TextView>(R.id.item_expense_maturity)
        val itemCirculo = itemView.findViewById<ImageView>(R.id.item_expense_circulo)
        val txtCategory = itemView.findViewById<TextView>(R.id.item_expense_category)
        val itemExpensePaidOut = itemView.findViewById<View>(R.id.item_expense_paidout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listExpense.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val expense = listExpense[position]

        holder.txtMaturity.text = SimpleDateFormat("dd/MM/yyyy").format(expense.date)
        holder.txtCategory.text = expense.category
        holder.txtDescription.text = expense.description
        holder.txtValue.text = expense.value

        if (expense.paidOut == false) {
            val background: Drawable = holder.itemExpensePaidOut.getBackground()
            background.setColorFilter(holder.context.getColor(R.color.red), PorterDuff.Mode.SRC_IN)
        } else {
            val background: Drawable = holder.itemExpensePaidOut.getBackground()
            background.setColorFilter(holder.context.getColor(R.color.green), PorterDuff.Mode.SRC_IN)
        }

        val iconId = listIcons[expense.iconPosition]

        holder.itemCirculo.setImageDrawable(iconId)

        val drawable = holder.itemCirculo.background as GradientDrawable
        drawable.setColor(Color.parseColor(expense.corIcon))

        if (position == listExpense.size - 1) {
            // Se for o último item, defina a margem inferior maior
            val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
            layoutParams.setMargins(0, 0, 0, 20) // Defina a margem inferior maior aqui
            holder.itemView.layoutParams = layoutParams
        } else {
            // Se não for o último item, defina a margem inferior normal
            val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
            layoutParams.setMargins(0, 0, 0, 16) // Defina a margem inferior normal aqui
            holder.itemView.layoutParams = layoutParams
        }

        holder.itemView.setOnClickListener {
            clickItem.invoke(expense)
        }
        holder.itemView.setOnLongClickListener {
            Toast.makeText(holder.itemView.context, "ddd", Toast.LENGTH_SHORT).show()
            true
        }
    }
    fun atualiza(expense: Expense) {
        val index = listExpense.indexOf(expense)
        if (index != -1) {
            listExpense[index] = expense
            notifyItemChanged(index)
        }
    }

    fun remove(expense: Expense) {
        this.listExpense.remove(expense)
        notifyDataSetChanged()
    }

    fun removeAll(expenses: List<Expense>) {
        this.listExpense.removeAll(expenses)
        notifyDataSetChanged()
    }
}

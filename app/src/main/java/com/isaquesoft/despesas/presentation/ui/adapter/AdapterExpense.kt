package com.isaquesoft.despesas.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.Expense
import java.text.SimpleDateFormat

class AdapterExpense(
    private val listExpense: List<Expense>,
    private val clickItem: (expense: Expense) -> Unit = {},
) : RecyclerView.Adapter<AdapterExpense.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtDescription = itemView.findViewById<TextView>(R.id.item_expense_description)
        val txtValue = itemView.findViewById<TextView>(R.id.item_expense_value)
        val txtMaturity = itemView.findViewById<TextView>(R.id.item_expense_maturity)
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

        holder.txtDescription.text = expense.description
        holder.txtValue.text = expense.value
        holder.txtMaturity.text = SimpleDateFormat("dd/MM/yyyy").format(expense.date)

        holder.itemView.setOnClickListener {
            clickItem.invoke(expense)
        }
    }
}

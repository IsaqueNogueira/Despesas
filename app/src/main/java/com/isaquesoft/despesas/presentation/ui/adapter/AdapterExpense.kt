package com.isaquesoft.despesas.presentation.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.Expense
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class AdapterExpense(
    private val listExpense: List<Expense>,
    private val clickItem: (expense: Expense) -> Unit = {},
) : RecyclerView.Adapter<AdapterExpense.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtDescription = itemView.findViewById<TextView>(R.id.item_expense_description)
        val txtValue = itemView.findViewById<TextView>(R.id.item_expense_value)
        val txtMaturity = itemView.findViewById<TextView>(R.id.item_expense_maturity)
        val itemPaidOut = itemView.findViewById<TextView>(R.id.item_paidout)
        val itemIate = itemView.findViewById<TextView>(R.id.item_late)
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

        if (expense.paidOut == true) {
            holder.itemPaidOut.visibility = View.VISIBLE
            holder.txtValue.setTextColor(Color.parseColor("#229522"))
        } else {
            holder.itemPaidOut.visibility = View.GONE
            holder.txtValue.setTextColor(Color.parseColor("#676767"))
        }

        val currentDate = LocalDate.now()
        val formatDate = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val dateCurrentFormated = currentDate.format(formatDate)
        val date = SimpleDateFormat("dd/MM/yyyy").parse(dateCurrentFormated)
        val calendar = Calendar.getInstance()
        calendar.time = date

        if (expense.date < calendar.timeInMillis && expense.paidOut == false) {
            holder.itemIate.visibility = View.VISIBLE
            holder.txtValue.setTextColor(Color.parseColor("#d4221f"))
        } else {
            holder.itemIate.visibility = View.GONE
        }

        if (expense.repeat && expense.installments == 0) {
            holder.txtMaturity.text = SimpleDateFormat("dd/MM/yyyy").format(expense.date) + " - Despesa fixa"
        } else {
            holder.txtMaturity.text = SimpleDateFormat("dd/MM/yyyy").format(expense.date)
        }

        holder.txtDescription.text = expense.description
        holder.txtValue.text = expense.value

        holder.itemView.setOnClickListener {
            clickItem.invoke(expense)
        }
    }
}

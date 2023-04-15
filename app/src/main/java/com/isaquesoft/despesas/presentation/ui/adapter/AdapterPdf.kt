package com.isaquesoft.despesas.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.Expense
import java.text.SimpleDateFormat

class AdapterPdf(private val expenses: List<Expense>) :
    RecyclerView.Adapter<AdapterPdf.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val context = itemView.context
        val txtPdfDescription = itemView.findViewById<TextView>(R.id.item_pdf_description)
        val checkDefaultPdf = itemView.findViewById<View>(R.id.item_pdf_check_default)
        val checkPdf = itemView.findViewById<View>(R.id.item_pdf_check)
        val txtPdfMaturity = itemView.findViewById<TextView>(R.id.item_pdf_maturity)
        val textPdfValue = itemView.findViewById<TextView>(R.id.item_pdf_value)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pdf, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return expenses.size
    }

    override fun onBindViewHolder(holder: AdapterPdf.ViewHolder, position: Int) {
        val expense = expenses[position]

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.context, R.color.row_color_1))
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.context, R.color.row_color_2))
        }

        holder.txtPdfDescription.text = expense.description
        if (expense.paidOut == true) {
            holder.checkPdf.visibility =
                View.VISIBLE
        } else {
            holder.checkPdf.visibility = View.GONE
        }
        holder.txtPdfMaturity.text = SimpleDateFormat("dd/MM/yyyy").format(expense.date)
        holder.textPdfValue.text = expense.value
    }
}

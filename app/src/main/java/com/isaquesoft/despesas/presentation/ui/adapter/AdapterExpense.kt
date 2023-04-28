package com.isaquesoft.despesas.presentation.ui.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.Expense
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class AdapterExpense(
    private val listExpense: List<Expense>,
    private val clickItem: (expense: Expense) -> Unit = {},
) : RecyclerView.Adapter<AdapterExpense.ViewHolder>() {

    private fun String.unaccent(): String {
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return Regex("[^\\p{ASCII}]").replace(temp, "")
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val context = itemView.context
        val txtDescription = itemView.findViewById<TextView>(R.id.item_expense_description)
        val txtValue = itemView.findViewById<TextView>(R.id.item_expense_value)
        val txtMaturity = itemView.findViewById<TextView>(R.id.item_expense_maturity)
        val itemCirculo = itemView.findViewById<ImageView>(R.id.item_expense_circulo)
        val txtCategory = itemView.findViewById<TextView>(R.id.item_expense_category)
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
            holder.txtValue.setTextColor(Color.parseColor("#2A3AFF"))
        } else {
            holder.txtValue.setTextColor(Color.parseColor("#000000"))
        }

        val currentDate = LocalDate.now()
        val formatDate = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val dateCurrentFormated = currentDate.format(formatDate)
        val date = SimpleDateFormat("dd/MM/yyyy").parse(dateCurrentFormated)
        val calendar = Calendar.getInstance()
        calendar.time = date

        if (expense.date < calendar.timeInMillis && expense.paidOut == false) {
            holder.txtValue.setTextColor(Color.parseColor("#d4221f"))
        }

        var categoryName = expense.category.toLowerCase()
        categoryName = categoryName.unaccent()
        if (categoryName == "comida e bebida") {
            categoryName = "comidabebida"
        }

        val outros = holder.context.resources.getIdentifier(
            categoryName,
            "drawable",
            holder.context.packageName,
        )
        holder.itemCirculo.setImageResource(outros)

        val color = holder.context.resources.getIdentifier(
            categoryName,
            "color",
            holder.context.packageName,
        )
        val drawable = holder.itemCirculo.background as GradientDrawable
        drawable.setColor(ContextCompat.getColor(holder.context, color))

        holder.txtMaturity.text = SimpleDateFormat("dd/MM/yyyy").format(expense.date)
        holder.txtCategory.text = expense.category
        holder.txtDescription.text = expense.description
        holder.txtValue.text = expense.value

        holder.itemView.setOnClickListener {
            clickItem.invoke(expense)
        }
        holder.itemView.setOnLongClickListener {
            Toast.makeText(holder.itemView.context, "ddd", Toast.LENGTH_SHORT).show()
            true
        }
    }
}

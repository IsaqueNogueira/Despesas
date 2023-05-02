package com.isaquesoft.despesas.utils

import android.content.Context
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.Category

class CategoryUtils {

    fun getListCategory(context: Context): List<Category> {
        val categorys =
            listOf(
                Category(category = context.getString(R.string.roupas), iconPosition = 12, cor = "#1E2E81"),
                Category(category = context.getString(R.string.viagem), iconPosition = 12, cor = "#86A570"),
                Category(category = context.getString(R.string.cartao), iconPosition = 14, cor = "#D5B25A"),
                Category(category = context.getString(R.string.saude), iconPosition = 15, cor = "#833B7F"),
                Category(category = context.getString(R.string.filme), iconPosition = 16, cor = "#6C5B7B"),
                Category(category = context.getString(R.string.outros), iconPosition = 17, cor = "#0096A3"),
                Category(category = context.getString(R.string.comidabebida), iconPosition = 18, cor = "#497700"),
                Category(category = context.getString(R.string.transporte), iconPosition = 19, cor = "#652F2F"),
                Category(category = context.getString(R.string.eletronicos), iconPosition = 20, cor = "#007E56"),
                Category(category = context.getString(R.string.educacao), iconPosition = 21, cor = "#6C8CD5"),
                Category(category = context.getString(R.string.entretenimento), iconPosition = 22, cor = "#9C4444"),
            )

        return categorys
    }
}

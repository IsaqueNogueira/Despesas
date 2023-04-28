package com.isaquesoft.despesas.utils

import android.content.Context
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.Category

class CategoryUtils {

    fun getListCategory(context: Context): List<Category> {
        val categorys =
            listOf(
                Category(category = context.getString(R.string.roupas), icon = 0),
                Category(category = context.getString(R.string.viagem), icon = 1),
                Category(category = context.getString(R.string.cartao), icon = 2),
                Category(category = context.getString(R.string.saude), icon = 3),
                Category(category = context.getString(R.string.filme), icon = 4),
                Category(category = context.getString(R.string.outros), icon = 5),
                Category(category = context.getString(R.string.comidabebida), icon = 6),
                Category(category = context.getString(R.string.transporte), icon = 7),
                Category(category = context.getString(R.string.eletronicos), icon = 8),
                Category(category = context.getString(R.string.educacao), icon = 9),
                Category(category = context.getString(R.string.entretenimento), icon = 10),
            )

        return categorys
    }
}

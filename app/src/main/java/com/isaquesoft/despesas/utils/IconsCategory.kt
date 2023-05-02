package com.isaquesoft.despesas.utils

import android.content.Context
import android.graphics.drawable.Drawable
import com.isaquesoft.despesas.R

class IconsCategory {

    fun listIcons(context: Context): MutableList<Drawable> {
        val drawableList = mutableListOf<Drawable>()
        drawableList.add(context.getDrawable(R.drawable.aviao)!!)
        drawableList.add(context.getDrawable(R.drawable.bike)!!)
        drawableList.add(context.getDrawable(R.drawable.bus)!!)
        drawableList.add(context.getDrawable(R.drawable.cadeado)!!)
        drawableList.add(context.getDrawable(R.drawable.camera)!!)
        drawableList.add(context.getDrawable(R.drawable.car)!!)
        drawableList.add(context.getDrawable(R.drawable.cardn)!!)
        drawableList.add(context.getDrawable(R.drawable.farmacia)!!)
        drawableList.add(context.getDrawable(R.drawable.foneouvido)!!)
        drawableList.add(context.getDrawable(R.drawable.impressora)!!)
        drawableList.add(context.getDrawable(R.drawable.moto)!!)
        drawableList.add(context.getDrawable(R.drawable.piscina)!!)
        drawableList.add(context.getDrawable(R.drawable.roupas)!!)
        drawableList.add(context.getDrawable(R.drawable.viagem)!!)
        drawableList.add(context.getDrawable(R.drawable.cartao)!!)
        drawableList.add(context.getDrawable(R.drawable.saude)!!)
        drawableList.add(context.getDrawable(R.drawable.filmes)!!)
        drawableList.add(context.getDrawable(R.drawable.outros)!!)
        drawableList.add(context.getDrawable(R.drawable.comidabebida)!!)
        drawableList.add(context.getDrawable(R.drawable.transporte)!!)
        drawableList.add(context.getDrawable(R.drawable.eletronicos)!!)
        drawableList.add(context.getDrawable(R.drawable.educacao)!!)
        drawableList.add(context.getDrawable(R.drawable.entretenimento)!!)
        return drawableList
    }
}

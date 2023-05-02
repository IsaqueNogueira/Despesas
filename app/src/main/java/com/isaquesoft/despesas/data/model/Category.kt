package com.isaquesoft.despesas.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val category: String,
    val iconPosition: Int,
    val cor: String
)

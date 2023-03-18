package com.isaquesoft.despesas.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val description: String,
    val value: String,
    val dateCreated: Long,
    val date: Long,
    val repeat: Boolean,
    val installments: Int,
    val paidOut: Boolean? = false
) : Parcelable

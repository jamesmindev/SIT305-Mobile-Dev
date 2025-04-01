package com.example.task7.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "adverts")
data class Advert(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val postType: String,
    val name: String,
    val phoneNumber: String,
    val description: String,
    val date: Long,
    val location: String
) : Parcelable
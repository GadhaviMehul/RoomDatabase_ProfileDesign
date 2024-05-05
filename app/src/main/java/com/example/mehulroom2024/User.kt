package com.example.mehulroom2024

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Entity(tableName = "UserTable")
@Parcelize
data class User(

    @PrimaryKey(autoGenerate = true)
    var id : Int,
    var firstName: String,
    var lastName: String,
    var email: String,
    var password: String,
    var date: LocalDate = LocalDate.now(),
    var image: String,
    var gender: String,
    var mobileNumber: String

) : Parcelable
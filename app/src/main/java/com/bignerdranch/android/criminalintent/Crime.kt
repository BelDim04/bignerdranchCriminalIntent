package com.bignerdranch.android.criminalintent

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity
data class Crime(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var title: String = "",
    var date: Date = Date(),
    var isSolved: Boolean = false,
    var suspect: String = ""/*,
    var requiresPolice: Boolean = false*/
) {
    val photoFileName
        get() = "IMG_$id.jpg"


    override fun equals(other: Any?): Boolean {
        val otherCrime = other as Crime
        return (id == otherCrime.id && title == otherCrime.title && date == otherCrime.date && isSolved == otherCrime.isSolved)
    }
}


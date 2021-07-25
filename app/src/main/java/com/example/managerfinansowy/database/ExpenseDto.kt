package com.example.managerfinansowy.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.managerfinansowy.model.Expense

@Entity(tableName = "expense")
data class ExpenseDto(
        @PrimaryKey(autoGenerate = true)
        val id: Long = 0,
        val localization: String,
        val amount: Double,
        val type: String,
        val date: String
) {
    fun toModel() = Expense(
            id, localization, amount, type, date
    )
}
package com.example.managerfinansowy.model

data class Expense(
    val id: Long,
    val localization : String,
    val amount: Double,
    val type: String,
    val date: String
)

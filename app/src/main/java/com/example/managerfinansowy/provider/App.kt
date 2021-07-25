package com.example.managerfinansowy.provider

import android.app.Application
import com.example.managerfinansowy.database.ExpenseDatabase

class App : Application() {
    val database by lazy { ExpenseDatabase.open(this)}
}
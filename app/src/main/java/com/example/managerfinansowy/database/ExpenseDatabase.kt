package com.example.managerfinansowy.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.managerfinansowy.database.dao.ExpenseDao

private const val NAME = "expense"

@Database(
        entities = [ExpenseDto::class],
        version = 1
)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract val expenses: ExpenseDao

    companion object {
        fun open(context: Context) = Room.databaseBuilder(
                context,
                ExpenseDatabase::class.java,
                NAME
        ).build()
    }
}
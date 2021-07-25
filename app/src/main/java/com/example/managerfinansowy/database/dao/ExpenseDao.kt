package com.example.managerfinansowy.database.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.managerfinansowy.database.ExpenseDto

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expense;")
    fun getAll(): List<ExpenseDto>

    @Insert
    fun insert(expense: ExpenseDto): Long

    @Query("SELECT * FROM expense WHERE date LIKE :date ORDER BY expense.date")
    fun getByDate(date: String): List<ExpenseDto>

    @Query("SELECT SUM(amount) FROM expense WHERE date LIKE :date;")
    fun getSumByDate(date: String): Double

    @Query ("DELETE FROM expense WHERE id = :id")
    fun deleteById(id: Long)

    @Update
    fun update(expense: ExpenseDto)

    @Query("SELECT * FROM expense ORDER BY :sortOrder")
    fun getAllCursor(sortOrder: String): Cursor

    @Query("SELECT * FROM expense WHERE id = :id")
    fun getByIdCursor(id: Long): Cursor

    @Query("SELECT * FROM expense WHERE id = :id")
    fun getById(id: Long): Long
}

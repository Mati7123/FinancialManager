package com.example.managerfinansowy.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.content.res.Resources
import android.database.Cursor
import android.net.Uri
import com.example.managerfinansowy.database.ExpenseDto
import java.lang.IllegalArgumentException

const val EXPENSE_PROVIDER_AUTHORITY = "com.example.managerfinansowy"
const val EXPENSES = 1
const val EXPENSES_ID = 2
const val ONE_ROW_AFFECTED = 1;

class ExpenseProvider : ContentProvider() {
    private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(EXPENSE_PROVIDER_AUTHORITY, "expense", EXPENSES)
        addURI(EXPENSE_PROVIDER_AUTHORITY, "expense/#", EXPENSES_ID)
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
            uri: Uri, projection: Array<String>?, selection: String?,
            selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val db = (context?.applicationContext as? App)?.database
                ?: throw RuntimeException("Cannot get app context")

        return when (sUriMatcher.match(uri)) {
            EXPENSES -> {
                if (!sortOrder.isNullOrEmpty()) {
                    db.expenses.getAllCursor(sortOrder)
                } else {
                    db.expenses.getAllCursor("date")
                }
            }
            EXPENSES_ID -> {
                val id = ContentUris.parseId(uri)
                db.expenses.getByIdCursor(id)
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun getType(uri: Uri): String? = when (sUriMatcher.match(uri)) {
        EXPENSES -> "vnd.android.cursor.dir/vnd.${EXPENSE_PROVIDER_AUTHORITY}.expense"
        EXPENSES_ID -> "vnd.android.cursor.item/vnd.${EXPENSE_PROVIDER_AUTHORITY}.expense"
        else -> throw  IllegalArgumentException()
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        when (sUriMatcher.match(uri)) {
            EXPENSES -> throw IllegalArgumentException("INCORRECT URI")
            EXPENSES_ID -> {
                val db = (context?.applicationContext as? App)?.database
                        ?: throw RuntimeException("Cannot get app context")
                val expense = values?.let {
                    ExpenseDto(
                            0,
                            values.getAsString("Localization"),
                            values.getAsDouble("Amount"),
                            values.getAsString("Type"),
                            values.getAsString("Date")
                    )
                }
                if (expense != null) {
                    val id = db.expenses.insert(expense)
                    return Uri.parse("expense/${id}")
                } else {
                    throw IllegalArgumentException()
                }
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        when (sUriMatcher.match(uri)) {
            EXPENSES -> throw IllegalArgumentException("NOT SUPPORTED URI")
            EXPENSES_ID -> {
                val db = (context?.applicationContext as? App)?.database
                        ?: throw RuntimeException("Cannot get app context")
                val idString = uri.lastPathSegment
                var id: Long = 0
                if (idString != null) try {
                    if (db.expenses.getById(id) != 0L) {
                        id = idString.toLong()
                    } else {
                        throw Resources.NotFoundException()
                    }
                } catch (e1: Exception) {
                    throw IllegalArgumentException()
                }
                db.expenses.deleteById(id)
                return ONE_ROW_AFFECTED
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        when (sUriMatcher.match(uri)) {
            EXPENSES -> throw IllegalArgumentException("NOT SUPPORTED URI")
            EXPENSES_ID -> {
                val db = (context?.applicationContext as? App)?.database
                        ?: throw RuntimeException("Cannot get app context")
                val idString = uri.lastPathSegment
                var id: Long = 0
                if (idString != null) try {
                    if (db.expenses.getById(id) != 0L) {
                        id = idString.toLong()
                    } else {
                        throw Resources.NotFoundException()
                    }
                } catch (e1: Exception) {
                    throw IllegalArgumentException()
                }
                val expense = values?.let {
                    ExpenseDto(
                            id,
                            values.getAsString("Localization"),
                            values.getAsDouble("Amount"),
                            values.getAsString("Type"),
                            values.getAsString("Date")
                    )
                }
                if (expense != null) {
                    db.expenses.update(expense)
                    return ONE_ROW_AFFECTED
                } else {
                    throw IllegalArgumentException()
                }
            }
            else -> throw IllegalArgumentException()
        }
    }
}
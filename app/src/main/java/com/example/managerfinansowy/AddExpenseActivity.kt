package com.example.managerfinansowy

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.managerfinansowy.database.ExpenseDatabase
import com.example.managerfinansowy.database.ExpenseDto
import com.example.managerfinansowy.databinding.ActivityAddExpenseBinding
import com.google.android.material.snackbar.Snackbar
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class AddExpenseActivity() : AppCompatActivity() {

    private val view by lazy { ActivityAddExpenseBinding.inflate(layoutInflater) }
    private val db by lazy {
        ExpenseDatabase.open(this)
    }
    private val pool by lazy { Executors.newSingleThreadExecutor() }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
        val itemId = intent.getLongExtra("id", 0)
        if (itemId != 0L) {
            view.addLocalization.setText(intent.getStringExtra("localization"))
            view.addType.setText(intent.getStringExtra("type"))
            view.addDate.setText(intent.getStringExtra("date"))
            view.addAmount.setText(intent.getStringExtra("amount"))
        }
        view.addDate.setOnClickListener(View.OnClickListener { showDateDialog(view.addDate) })
        setupSaveButton(itemId)
        setupShareButton()
    }

    private fun setupShareButton() = view.shareButton.setOnClickListener {
        if (emptyTextValidation()) {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            var sb = StringBuilder()
            sb.append("Mój wydatek\n")
                    .append("Kwota: ").append(view.addAmount.text)
                    .append("\nLokalizacja: ").append(view.addLocalization.text)
                    .append("\nKategoria : ").append(view.addType.text)
                    .append("\nData: ").append(view.addDate.text)
            intent.putExtra(Intent.EXTRA_TEXT, sb.toString())
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Udostępnij:"))
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupSaveButton(itemId: Long) = view.saveButton.setOnClickListener {

        if (emptyTextValidation()) {
            var amount = 0.0;
            val amountText: String = view.addAmount.text.toString()
            if (!amountText.isEmpty()) try {
                amount = amountText.toDouble()
            } catch (e1: Exception) {
                e1.printStackTrace()
            }

            val expenseDto = ExpenseDto(
                    itemId,
                    view.addLocalization.text.toString(),
                    amount,
                    view.addType.text.toString(),
                    view.addDate.text.toString()
            )
            if (itemId != 0L) {
                pool.submit {
                    db.expenses.update(expenseDto)
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            } else if (itemId == 0L) {
                pool.submit {
                    db.expenses.insert(expenseDto)
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }
    }

    private fun showDateDialog(add_date: EditText) {
        val calendar = Calendar.getInstance()
        val dateSetListener =
                OnDateSetListener { datePicker, year, month, day ->
                    calendar[Calendar.YEAR] = year
                    calendar[Calendar.MONTH] = month
                    calendar[Calendar.DAY_OF_MONTH] = day
                    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
                    add_date.setText(simpleDateFormat.format(calendar.time))
                }
        DatePickerDialog(
                this, dateSetListener,
                calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]
        ).show()
    }

    private fun emptyTextValidation(): Boolean {
        return if (!view.addLocalization.text.isNullOrBlank() && !view.addType.text.isNullOrBlank() && !view.addAmount.text.isNullOrBlank() && !view.addDate.text.isNullOrBlank()) {
            true
        } else {
            Snackbar.make(view.layout, "Wszystkie pola muszą być wyepłnione", Snackbar.LENGTH_SHORT).show()
            false
        }
    }

}
package com.example.managerfinansowy

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.managerfinansowy.adapter.ExpenseAdapter
import com.example.managerfinansowy.database.ExpenseDatabase
import com.example.managerfinansowy.databinding.ActivityMainBinding
import java.time.LocalDate

private const val REQUEST_ADD_EXPENSE = 1;

class MainActivity : AppCompatActivity() {

    private val view by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val db by lazy {
        ExpenseDatabase.open(this)
    }
    private var date: LocalDate = LocalDate.now()
    private val expenseAdapter by lazy { ExpenseAdapter(db, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
        view.expasneDate.text = setDate(LocalDate.now())
        setupBackButton(date)
        setupNextButton(date)
        setupAddButton()
        setupExpenseList()
        setupChartButton()
    }

    private fun setupNextButton(date: LocalDate) = view.mainNextButton.setOnClickListener {
        view.expasneDate.text = setDate(date.plusMonths(1))
        this.date = date.plusMonths(1)
        reloadButtons(this.date)
    }

    private fun setupBackButton(date: LocalDate) = view.mainBackButton.setOnClickListener {
        view.expasneDate.text = setDate(date.minusMonths(1))
        this.date = date.minusMonths(1)
        reloadButtons(this.date)
    }

    private fun setupExpenseList() {
        view.expenseList.apply {
            adapter = expenseAdapter
            layoutManager = LinearLayoutManager(context)
        }
        view.sum.apply {

        }
        expenseAdapter.load(view.expasneDate.text.toString())
    }

    private fun setupAddButton() = view.addButton.setOnClickListener {
        val intent = Intent(this, AddExpenseActivity::class.java)
        startActivityForResult(
                intent, REQUEST_ADD_EXPENSE
        )
    }

    private fun setupChartButton() = view.chartButton.setOnClickListener {
        val intent = Intent(this, ExpenseChartActivity::class.java)
        intent.putExtra("date", view.expasneDate.text.toString())
        startActivityForResult(
                intent, REQUEST_ADD_EXPENSE
        )
    }

    override fun onResume() {
        expenseAdapter.load(view.expasneDate.text.toString())
        super.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_ADD_EXPENSE && resultCode == Activity.RESULT_OK) {
            expenseAdapter.load(view.expasneDate.text.toString())
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun reloadButtons(date: LocalDate) {
        setupBackButton(date)
        setupNextButton(date)
        expenseAdapter.load(view.expasneDate.text.toString())
    }


    private fun setDate(localDate: LocalDate): String {
        val month = localDate.monthValue
        val year = localDate.year
        var monthString: String
        if (month < 10) {
            monthString = month.toString()
            monthString = "0$monthString"
        } else {
            monthString = month.toString()
        }
        return "$monthString-$year"
    }

    fun setSumTextView(value: String) {
        this.runOnUiThread {
            view.sum.text = value
        }
    }
}
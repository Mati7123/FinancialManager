package com.example.managerfinansowy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.managerfinansowy.chart.ExpanseChartPoint
import com.example.managerfinansowy.database.ExpenseDatabase
import com.example.managerfinansowy.databinding.ActivityExpenseChartBinding
import com.example.managerfinansowy.model.Expense
import java.lang.StringBuilder
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread


class ExpenseChartActivity : AppCompatActivity() {
    private val view by lazy { ActivityExpenseChartBinding.inflate(layoutInflater) }
    private val db by lazy {
        ExpenseDatabase.open(this)
    }
    private var date: LocalDate = LocalDate.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
        var dateString = intent.getStringExtra("date")
        view.month.text = dateString
        val dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        dateString = "01-$dateString"
        date = LocalDate.parse(dateString, dtf)
        setupBackButton(date)
        setupNextButton(date)
        thread {
            val expenseList = getDataFromDb(setDate(date))
            view.graphView.setChartInputs(createExpenseChartInputList(expenseList))
        }
    }

    private fun setupNextButton(date: LocalDate) = view.next.setOnClickListener {
        view.month.text = setDate(date.plusMonths(1))
        this.date = date.plusMonths(1)
        reloadButtons(this.date)
    }

    private fun setupBackButton(date: LocalDate) = view.back.setOnClickListener {
        view.month.text = setDate(date.minusMonths(1))
        this.date = date.minusMonths(1)
        reloadButtons(this.date)
    }

    private fun reloadButtons(date: LocalDate) {
        setupBackButton(date)
        setupNextButton(date)
        thread {
            val expenseList = getDataFromDb(setDate(date))
            view.graphView.setChartInputs(createExpenseChartInputList(expenseList))
        }
    }

    private fun createExpenseChartInputList(expenseList: List<Expense>): List<ExpanseChartPoint> {
        var sb = StringBuilder()
        sb.append("Saldo końcowe: ")
        if (expenseList.isEmpty()) {
            sb.append("0.0")
            this.runOnUiThread {
                view.bilans.text = sb.toString()
            }
            return emptyList()
        } else if (expenseList.size < 2) {
            sb.append(expenseList.first().amount.toString())
            this.runOnUiThread {
                view.bilans.text = sb.toString()
            }
            return emptyList()
        }
        var firstDataPoint = ExpanseChartPoint(expenseList.first().date.substring(8).toFloat(), expenseList.first().amount.toFloat())
        var datePointList = mutableListOf(firstDataPoint)
        var currentBilians = 0F
        expenseList.forEachIndexed { index, expense ->

            var amountPoint: Float = expense.amount.toFloat()
            currentBilians += amountPoint
            if (index < expenseList.size - 1) {
                val nextExpense = expenseList[index + 1]
                val nextAmountPoint = nextExpense.amount.toFloat() + currentBilians
                val nextDayPoint = nextExpense.date.substring(8).toFloat()
                val x = ExpanseChartPoint(nextDayPoint, nextAmountPoint.toFloat())
                datePointList.add(x)

            }
        }
        sb.append(datePointList.last().yAmount)
        this.runOnUiThread {
            view.bilans.text = sb.toString()
        }
        return datePointList
    }

    private fun getDataFromDb(date: String): List<Expense> {
        return db.expenses.getByDate(createWhereString(date)).map { it.toModel() }
    }

    private fun createWhereString(date: String): String {
        val month = date.substring(0, 2)
        val year = date.substring(3, 7)
        return "$year-$month-%%"

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
}
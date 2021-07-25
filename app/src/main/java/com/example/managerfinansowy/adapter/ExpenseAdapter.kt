package com.example.managerfinansowy.adapter

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.HandlerCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.managerfinansowy.AddExpenseActivity
import com.example.managerfinansowy.MainActivity
import com.example.managerfinansowy.database.ExpenseDatabase
import com.example.managerfinansowy.databinding.ItemExpenseBinding
import com.example.managerfinansowy.model.Expense
import kotlin.concurrent.thread

class  ExpenseAdapter(private val db: ExpenseDatabase, private val context: MainActivity) : RecyclerView.Adapter<ExpenseAdapter.ExpenseVh>() {

    private var data: List<Expense> = emptyList()
    private var sum: Double = 0.0

    private val mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())

    inner class ExpenseVh(private val view: ItemExpenseBinding) : RecyclerView.ViewHolder(view.root) {
        fun bind(expense: Expense, index: Int) {
            with(view) {
                expenseLocalization.text = expense.localization
                expenseAmount.text = expense.amount.toString()
                expenseDate.text = expense.date
                expenseType.text = expense.type
                if (expenseAmount.text.startsWith("-")) {
                    expenseAmount.setTextColor(Color.RED)
                } else {
                    expenseAmount.setTextColor(Color.GREEN)
                }
                itemView.setOnLongClickListener {

                    val builder = AlertDialog.Builder(context)
                    builder.setMessage("Czy na pewno chcesz usunąć?")
                            .setCancelable(false)
                            .setPositiveButton("Tak") { dialog, id ->
                                val year = expense.date.substring(0, 4)
                                val month = expense.date.substring(5, 7)
                                deleteItem(index, "$month-$year")
                            }
                            .setNegativeButton("Nie") { dialog, id ->
                                dialog.dismiss()
                            }
                    val alert = builder.create()
                    alert.show()
                    return@setOnLongClickListener true
                }
                itemView.setOnClickListener {
                    val intent = Intent(context, AddExpenseActivity::class.java)
                    intent.putExtra("id", expense.id)
                    intent.putExtra("localization", expense.localization)
                    intent.putExtra("type", expense.type)
                    intent.putExtra("date", expense.date)
                    intent.putExtra("amount", expense.amount.toString())
                    context.startActivity(intent)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseVh {
        val view = ItemExpenseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        )
        return ExpenseVh(view)
    }

    override fun onBindViewHolder(holder: ExpenseVh, position: Int) {
        holder.bind(data[position], position)
    }

    override fun getItemCount(): Int = data.size

    fun load(date: String) = thread {
        data = db.expenses.getByDate(createWhereString(date)).map { it.toModel() }
        mainHandler.post {
            notifyDataSetChanged()
            getSum(date)
        }
    }


    private fun getSum(date: String) = thread {
        sum = db.expenses.getSumByDate(createWhereString(date))
        context.setSumTextView(sum.toString())
    }

    private fun createWhereString(date: String): String {
        val month = date.substring(0, 2)
        val year = date.substring(3, 7)
        return "$year-$month-%%"
    }

    fun deleteItem(position: Int, date: String) {
        val item = data[position]
        thread {
            db.expenses.deleteById(item.id)
            load(date)
        }
        notifyDataSetChanged()
    }
}
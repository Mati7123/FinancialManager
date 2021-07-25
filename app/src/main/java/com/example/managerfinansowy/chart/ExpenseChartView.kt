package com.example.managerfinansowy.chart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class ExpenseChartView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var expensePointList = mutableListOf<ExpanseChartPoint>()
    private var xDayMaxConvert = 0F
    private var yAmountMinConvert = 0F
    private var yAmountMaxConvert = 0F
    private var axisYMoved = 0F
    private var scaleYAmount = 1F
    private var scaleXAmount = 1F
    private var xDayMax = 30
    private var yAmountMin = 0
    private var yAmountMax = 0

    private var chartLinePaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 10f
    }

    private val axisPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 10f
    }

    private val axisDescriptionPaint = Paint().apply {
        color = Color.BLACK
        textSize = 40f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        scaleYAmount = (height) / (yAmountMaxConvert - yAmountMinConvert)
        scaleXAmount = (width - 170) / xDayMaxConvert

        convertExpanseTable()
        drawAxis(canvas)
        drawAxisDescription(canvas)
        drawChart(canvas)
    }

    fun setChartInputs(expensePointList: List<ExpanseChartPoint>) {
        yAmountMinConvert = -600F
        yAmountMaxConvert = 2000F
        xDayMaxConvert = 30F
        if (expensePointList.isNotEmpty()) {
            if ((expensePointList.maxOf { it.xDay }) > xDayMaxConvert) {
                xDayMaxConvert = 31f
            }
            var yMaxPoint = (expensePointList.maxOf { it.yAmount })
            var yMinPoint = (expensePointList.minOf { it.yAmount })
            if (yMaxPoint > yAmountMaxConvert) {
                yAmountMaxConvert = yMaxPoint
            }
            if (yMinPoint < yAmountMinConvert) {
                yAmountMinConvert = yMinPoint
            }
        }
        axisYMoved = -yAmountMinConvert
        yAmountMin = yAmountMaxConvert.toInt()
        yAmountMax = yAmountMinConvert.toInt()
        xDayMax = xDayMaxConvert.toInt()
        invalidate()
        this.expensePointList.clear()
        this.expensePointList.addAll(expensePointList)
    }

    private fun convertExpanseTable() {
        for (data in expensePointList) {
            data.yAmount += axisYMoved
            data.yAmount *= scaleYAmount
            data.xDay *= scaleXAmount
            data.xDay += 120
        }
    }

    private fun drawChart(canvas: Canvas) {
        expensePointList.forEachIndexed { index, expensePoint ->
            if (index < expensePointList.size - 1) {
                val nextExpensePoint = expensePointList[index + 1]
                val yAxis = axisYMoved * scaleYAmount
                val x1 = expensePoint.xDay
                val y1 = expensePoint.yAmount
                val x2 = nextExpensePoint.xDay
                val y2 = nextExpensePoint.yAmount
                val a = (y2 - y1) / (x2 - x1)
                val b = y1 - a * (x1)
                val x0 = ((yAxis - b) / a)

                if (x0 != yAxis && x0 > x1 && x0 < x2) {
                    if (y1 > yAxis) {
                        setChartColor(Color.GREEN)
                    } else {
                        setChartColor(Color.RED)
                    }
                    canvas.drawLine(x1, height - y1, x0, height - yAxis, chartLinePaint)

                    if (y2 > yAxis) {
                        setChartColor(Color.GREEN)
                    } else {
                        setChartColor(Color.RED)
                    }
                    canvas.drawLine(x0, height - yAxis, x2, height - y2, chartLinePaint)
                } else {
                    if (y2 > yAxis) {
                        setChartColor(Color.GREEN)
                    } else {
                        setChartColor(Color.RED)
                    }
                    canvas.drawLine(x1, height - y1, x2, height - y2, chartLinePaint)
                }
            }
        }
    }

    private fun drawAxisDescription(canvas: Canvas) {
        for (i in 0..xDayMax) {
            if (i % 2 != 0) {
                canvas.drawText(i.toString(), 110 + i.toFloat() * ((width - 150) / xDayMax), height - axisYMoved * scaleYAmount + 40f, axisDescriptionPaint)
            }
        }
        for (i in yAmountMax..(yAmountMin)) {
            if (i % 500 == 0) {
                canvas.drawText(i.toString(), 10f, height.toFloat() - (i + axisYMoved) * ((height) / (yAmountMin + axisYMoved)), axisDescriptionPaint)
            }
        }
        canvas.drawText(yAmountMin.toString(), 10f, (height.toFloat() - (yAmountMin + axisYMoved) * ((height) / (yAmountMin + axisYMoved)) + 30), axisDescriptionPaint)
    }

    private fun drawAxis(canvas: Canvas) {
        canvas.drawLine(120f, 0f, 120f, height.toFloat(), axisPaint)
        canvas.drawLine(120f, height - axisYMoved * scaleYAmount, width.toFloat(), height - axisYMoved * scaleYAmount, axisPaint)
    }

    private fun setChartColor(color: Int) {
        chartLinePaint.color = color
    }
}


package com.isaquesoft.despesas.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.Expense
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.util.CellRangeAddress
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun shareExpensesAsExcel(activity: Activity, context: Context, expenses: List<Expense>) {
    // Criar um novo arquivo Excel
    val workbook: Workbook = HSSFWorkbook()
    val sheet: Sheet = workbook.createSheet("Despesas")

    // Obter o mês e ano da primeira despesa
    val firstExpenseDate = expenses.firstOrNull()?.date ?: System.currentTimeMillis()
    val monthYearTitle = formatMonthYear(firstExpenseDate)

    // Cabeçalho das colunas
    val headerRow: Row = sheet.createRow(0)
    headerRow.createCell(0).setCellValue("${context.getString(R.string.mes)} $monthYearTitle")
    sheet.addMergedRegion(CellRangeAddress(0, 0, 0, 4)) // Combinar as células para o título

    val headerRow2: Row = sheet.createRow(1)
    headerRow2.createCell(0).setCellValue(context.getString(R.string.description))
    headerRow2.createCell(1).setCellValue(context.getString(R.string.value))
    headerRow2.createCell(2).setCellValue(context.getString(R.string.maturity))
    headerRow2.createCell(3).setCellValue(context.getString(R.string.paidout))
    headerRow2.createCell(4).setCellValue(context.getString(R.string.categoria))

    // Preencher os dados das despesas
    var rowIndex = 2
    for (expense in expenses) {
        val row: Row = sheet.createRow(rowIndex)
        row.createCell(0).setCellValue(expense.description)
        row.createCell(1).setCellValue(expense.value)
        row.createCell(2).setCellValue(formatDate(expense.date))
        row.createCell(3).setCellValue(if (expense.paidOut == true) "Sim" else "Não")
        row.createCell(4).setCellValue(expense.category)
        rowIndex++
    }

    val deviceLocale = Locale.getDefault()
    val currencySymbol = Currency.getInstance(deviceLocale).symbol
    var totalValue = BigDecimal.ZERO
    var totalPaid = BigDecimal.ZERO
    var totalUnpaid = BigDecimal.ZERO

    for (expense in expenses) {
        val valueString = expense.value.replace(currencySymbol, "").trim().replace(",", ".")
        val value = BigDecimal(valueString)

        totalValue += value

        if (expense.paidOut == true) {
            totalPaid += value
        } else {
            totalUnpaid += value
        }
    }

    val numberFormat = NumberFormat.getCurrencyInstance(deviceLocale)
    val formattedFullValue = numberFormat.format(totalPaid)
    val formattedBalance = numberFormat.format(totalUnpaid)
    val formattedTotalValue = numberFormat.format(totalValue)

// Adicionar informações adicionais
    rowIndex += 2
    val summaryRow: Row = sheet.createRow(rowIndex)
    summaryRow.createCell(0).setCellValue(context.getString(R.string.pago))
    summaryRow.createCell(2).setCellValue(formattedFullValue)
    rowIndex++

    val unpaidRow: Row = sheet.createRow(rowIndex)
    unpaidRow.createCell(0).setCellValue(context.getString(R.string.nopago))
    unpaidRow.createCell(2).setCellValue(formattedBalance)
    rowIndex++

    val totalRow: Row = sheet.createRow(rowIndex)
    totalRow.createCell(0).setCellValue(context.getString(R.string.amount))
    totalRow.createCell(2).setCellValue(formattedTotalValue)
    rowIndex++


    // Salvar o arquivo Excel
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "despesas.xls")
    val fos = FileOutputStream(file)
    workbook.write(fos)
    fos.close()

    // Compartilhar o arquivo Excel
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    val intent = ShareCompat.IntentBuilder.from(activity)
        .setType("application/vnd.ms-excel")
        .setStream(uri)
        .intent
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    context.startActivity(Intent.createChooser(intent, "Compartilhar despesas"))
}

private fun formatMonthYear(date: Long): String {
    val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    return sdf.format(Date(date))
}

private fun formatDate(date: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(date))
}




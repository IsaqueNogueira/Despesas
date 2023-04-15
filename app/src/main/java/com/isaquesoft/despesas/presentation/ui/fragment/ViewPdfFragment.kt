package com.isaquesoft.despesas.presentation.ui.fragment

import android.content.Intent
import android.content.Intent.*
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.isaquesoft.despesas.R
import com.isaquesoft.despesas.data.model.Expense
import com.isaquesoft.despesas.databinding.ViewPdfFragmentBinding
import com.isaquesoft.despesas.presentation.ui.adapter.AdapterPdf
import org.koin.android.ext.android.get
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ViewPdfFragment : Fragment() {

    private lateinit var binding: ViewPdfFragmentBinding
    private val argument by navArgs<ViewPdfFragmentArgs>()
    private val expenses by lazy { argument.expenses }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = ViewPdfFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.visibility = View.INVISIBLE
        initRecyclerView()
        fullExpenseSum(expenses.toList())
        setupTitleDate()
        val bitmap = view.setupScreenshot(view)
        sharePdf(bitmap)
    }

    private fun setupTitleDate() {
        val monthLong = expenses[0].date
        val date = Date(monthLong)
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(date)
        binding.titlePdf.text = getString(R.string.app_name) +" "+ formattedDate
    }

    private fun initRecyclerView() {
        val recyclerView = binding.recyclerviewPdf
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = AdapterPdf(expenses.toList())
    }

    fun fullExpenseSum(expenses: List<Expense>) {
        val deviceLocale = Locale.getDefault()
        val currencySymbol = Currency.getInstance(deviceLocale).symbol
        var totalValue = BigDecimal.ZERO
        var totalPaidValue = BigDecimal.ZERO
        var totalUnpaidValue = BigDecimal.ZERO
        for (expense in expenses) {
            val originalString = expense.value
            val onlyLetters = originalString.replace(Regex("[\\d.,\\s]"), "")
            if (onlyLetters != currencySymbol) {
                // ignore this expense, as its value is not expressed in the device's currency
            } else {
                val valueString = expense.value.replace(currencySymbol, "").trim().replace(",", ".")
                val lastDotIndex = valueString.lastIndexOf('.')
                val formattedValueString = if (lastDotIndex >= 0) {
                    valueString.substring(0, lastDotIndex).replace(".", "") + valueString.substring(
                        lastDotIndex,
                    )
                } else {
                    valueString
                }
                val value = BigDecimal(formattedValueString)
                totalValue += value

                if (expense.paidOut == true) {
                    totalPaidValue += value
                } else {
                    totalUnpaidValue += value
                }
            }
        }
        val numberFormat = NumberFormat.getCurrencyInstance(deviceLocale)
        val formattedFullValue = numberFormat.format(totalValue)
        val formattedPaidValue = numberFormat.format(totalPaidValue)
        val formattedUnpaidValue = numberFormat.format(totalUnpaidValue)

        binding.totalPagoPdf.text = formattedPaidValue
        binding.totalNoPagoPdf.text = formattedUnpaidValue
        binding.totalPdf.text = formattedFullValue
    }

    private fun View.setupScreenshot(view: View): Bitmap {
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireContext().display?.getRealMetrics(displayMetrics)
            displayMetrics.densityDpi
        } else {
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        }

        this.measure(
            View.MeasureSpec.makeMeasureSpec(
                displayMetrics.widthPixels,
                View.MeasureSpec.EXACTLY,
            ),
            View.MeasureSpec.makeMeasureSpec(
                displayMetrics.heightPixels,
                View.MeasureSpec.EXACTLY,
            ),
        )
        this.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        val bitmap =
            Bitmap.createBitmap(
                this.measuredWidth,
                view.height,
                Bitmap.Config.ARGB_8888,
            )
        val canvas = Canvas(bitmap)
        this.draw(canvas)

        return Bitmap.createScaledBitmap(bitmap, this.measuredWidth, view.height, true)
    }

    private fun sharePdf(bitmap: Bitmap) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(
            bitmap.width,
            bitmap.height,
            1,
        ).create()

        val page = pdfDocument.startPage(pageInfo)
        page.canvas.drawBitmap(bitmap, 0F, 0F, null)
        pdfDocument.finishPage(page)

        val filePath = File(requireActivity().cacheDir, "Despesas.pdf")
        pdfDocument.writeTo(FileOutputStream(filePath))
        pdfDocument.close()
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "com.isaquesoft.despesas.fileprovider",
            filePath,
        )
        Intent().let {
            it.putExtra(
                EXTRA_STREAM,
                uri,
            )
            it.action = ACTION_SEND
            it.type = "application/pdf"
            it.flags = FLAG_ACTIVITY_NEW_TASK
            it.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
            it.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION)
            try {
                startActivity(
                    createChooser(
                        it,
                        "Compartilhar via",
                    ),
                )
            } catch (e: Exception) {}
        }
        requireActivity().onBackPressed()
    }
}

package com.isaquesoft.despesas.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.isaquesoft.despesas.R

class AlertDialogStandard {

    fun sutupDialog(context: Context, title: String, message: String) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.alert_dialog_standard, null)
        val alertDialog = AlertDialog.Builder(context)
            .setView(view)
            .create()
        alertDialog.setOnShowListener {
            view.apply {
                val txtTitle = findViewById<TextView>(R.id.alert_title)
                val txtMessage = findViewById<TextView>(R.id.alert_message)

                txtTitle.text = title
                txtMessage.text = message
                val buttonPositive = findViewById<Button>(R.id.alert_button)
                buttonPositive.setOnClickListener {
                    alertDialog.dismiss()
                }
            }
        }

        alertDialog.show()
    }

}

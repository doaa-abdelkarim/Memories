package com.example.memories.util

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import com.example.memories.R

class Message {
        lateinit var alertDialog: AlertDialog

        fun showMessage (
            context: Context,
            title: String? = null,
            message: String? = null,
            positiveBtnName: String? = null,
            positiveBtnAction: DialogInterface.OnClickListener? = null,
            negativeBtnName: String? = null,
            negativeBtnAction: DialogInterface.OnClickListener? = null
        ) {
            alertDialog = AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveBtnName, positiveBtnAction)
                .setNegativeButton(negativeBtnName, negativeBtnAction)
                .show()
            alertDialog
                .getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(
                    Color.parseColor(context.getString(R.string.dialog_button_text_color))
                )

            alertDialog
                .getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(
                    Color.parseColor(context.getString(R.string.dialog_button_text_color))
                )

        }
}
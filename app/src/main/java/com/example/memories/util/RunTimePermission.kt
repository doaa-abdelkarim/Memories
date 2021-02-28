package com.example.memories.util

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.memories.R

class RunTimePermission(val context: Context, val requestCode: Int, val permission: String) {

    fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(message: String) =
        if (ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permission)) {
            Message().showMessage(
                context,
                context.getString(R.string.permission_needed),
                message,
                context.getString(R.string.ok),
                DialogInterface.OnClickListener { _, _ ->
                    ActivityCompat.requestPermissions(context, arrayOf(permission), requestCode)
                },
                context.getString(R.string.cancel),
                DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                    context.finish()
                }
            )
        } else
            ActivityCompat.requestPermissions(context, arrayOf(permission), requestCode)

    fun manageResponse(requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray, packageName: String): Boolean {
        if (requestCode == this.requestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                return true
            else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity, permission)) {
                    Message().showMessage(
                        context,
                        message = context.getString(R.string.allow_all_permissions),
                        positiveBtnName = context.getString(R.string.ok),
                        positiveBtnAction = DialogInterface.OnClickListener {
                                _, _ ->
                            val intent = Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts(
                                    context.getString(R.string.package_name),
                                    packageName,
                                    null
                                )
                            )
                            context.startActivityForResult(
                                intent,
                                this.requestCode
                            )
                        },
                        negativeBtnName = context.getString(R.string.cancel),
                        negativeBtnAction = DialogInterface.OnClickListener {
                                dialog, _ ->
                            dialog.dismiss()
                            context.finish()
                        }
                    )
                } else
                    context.finish()
            }
        }
        return false
    }
}
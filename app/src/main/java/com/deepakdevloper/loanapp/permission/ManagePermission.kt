package com.deepakdevloper.loanapp.permission

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.deepakdevloper.loanapp.signup.signinHome


class ManagePermission (val activity: Activity,val list: List<String>,val code:Int) {

    // Check permissions at runtime
    fun checkPermissions() {
        if (isPermissionsGranted() != PackageManager.PERMISSION_GRANTED) {
            showAlert()
        } else {
            val i = Intent(activity,signinHome::class.java)
            activity.startActivity(i)
            activity.finish()
        }
    }

    fun checkPermissionsstart() {
        if (isPermissionsGranted() == PackageManager.PERMISSION_GRANTED) {
            val i = Intent(activity,signinHome::class.java)
            activity.startActivity(i)
            activity.finish()
        }
    }


    // Check permissions status
    private fun isPermissionsGranted(): Int {
        // PERMISSION_GRANTED : Constant Value: 0
        // PERMISSION_DENIED : Constant Value: -1
        var counter = 0;
        for (permission in list) {
            counter += ContextCompat.checkSelfPermission(activity, permission)
        }
        return counter
    }


    // Find the first denied permission
    private fun deniedPermission(): String {
        for (permission in list) {
            if (ContextCompat.checkSelfPermission(activity, permission)
                == PackageManager.PERMISSION_DENIED) return permission
        }
        return ""
    }


    // Show alert dialog to request permissions
    private fun showAlert() {
//        val builder = AlertDialog.Builder(activity)
//        builder.setTitle("Need permission(s)")
//        builder.setMessage("Some permissions are required to do the task.")
//        builder.setPositiveButton("OK", { dialog, which -> requestPermissions() })
//        builder.setNeutralButton("Cancel", null)
//        val dialog = builder.create()
//        dialog.show()
        requestPermissions()
    }


    // Request the permissions at run time
    private fun requestPermissions() {
        val permission = deniedPermission()
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            // Show an explanation asynchronously
            // we are displaying an alert dialog for permissions
            val builder = AlertDialog.Builder(activity)

            // below line is the title for our alert dialog.
            builder.setTitle("Need Permissions")

            // below line is our message for our dialog
            builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
            builder.setPositiveButton("GOTO SETTINGS") { dialog, which ->
                // this method is called on click on positive button and on clicking shit button
                // we are redirecting our user from our app to the settings page of our app.
                dialog.cancel()
                // below is the intent from which we are redirecting our user.
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                activity.startActivityForResult(intent, 123)
            }
            builder.setNegativeButton("Cancel") { dialog, which ->
                // this method is called when user click on negative button.
                dialog.cancel()
            }
            // below line is used to display our dialog
            builder.show()

        } else {
            ActivityCompat.requestPermissions(activity, list.toTypedArray(), code)
        }
    }


    // Process permissions result
    fun processPermissionsResult(requestCode: Int, permissions: Array<String>,
                                 grantResults: IntArray): Boolean {
        var result = 0
        if (grantResults.isNotEmpty()) {
            for (item in grantResults) {
                result += item
            }
        }
        if (result == PackageManager.PERMISSION_GRANTED) return true
        return false
    }
}
package com.deepakdevloper.loanapp.permission

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.deepakdevloper.loanapp.R

class TermandCondition : AppCompatActivity() {
    private val PermissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermission
    private lateinit var requestPermissionbtn:AppCompatButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_termand_condition)
        val list = listOf<String>(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        // Initialize a new instance of ManagePermissions class
        managePermissions = ManagePermission(this,list,PermissionsRequestCode)
        //our logic in this page when app is start if user all permission is granted then directly transfer to login activity other wise we stay in this activity
        requestPermissionbtn = findViewById(R.id.btnContinueToPermissions)
        requestPermissionbtn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                managePermissions.checkPermissions()
        }
        }
    }

    override fun onStart() {
        super.onStart()
        managePermissions.checkPermissionsstart()
    }
}
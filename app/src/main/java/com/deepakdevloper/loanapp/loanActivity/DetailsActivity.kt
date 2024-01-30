package com.deepakdevloper.loanapp.loanActivity

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.deepakdevloper.loanapp.Help.help
import com.deepakdevloper.loanapp.Notification.notificationActivity
import com.deepakdevloper.loanapp.R
import com.deepakdevloper.loanapp.databinding.ActivityDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private var REFERENCE = ""
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.msc1.setOnClickListener {
            val i = Intent(this, help::class.java)
            startActivity(i)
        }

        binding.msc.setOnClickListener {
            val i = Intent(this, notificationActivity::class.java)
            startActivity(i)
        }
        try{
            FirebaseDatabase.getInstance().getReference("notif")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString()).addValueEventListener(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            binding.msc.setImageResource(R.drawable.ic_notifications_active)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}

                })
        }
        catch (e:Exception){
        }

        binding.ms.setOnClickListener {
            val i = Intent(this,LoanOfferActivity::class.java)
            startActivity(i)
            finish()
        }

        binding.num2.setOnClickListener {
           val c = Calendar.getInstance()
           val year = c.get(Calendar.YEAR)
           val month = c.get(Calendar.MONTH)
           val day = c.get(Calendar.DAY_OF_MONTH)
           val datePickerDialog = DatePickerDialog(this,{ view, year, monthOfYear, dayOfMonth ->
             val dat = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
               binding.num2.setText(dat)
           },year,month,day
           )
           datePickerDialog.show()
        }

        val business = intent.getStringExtra("business")
        val home = intent.getStringExtra("home")
        val pocket = intent.getStringExtra("pocket")

        if (business == "BUSINESS_CODE") {
            REFERENCE = "BUSINESS_LOAN"

        } else if (home == "HOME_CODE") {
            REFERENCE = "HOME_LOAN"

        } else {
            REFERENCE = "POCKET_LOAN"

        }

        binding.btnGetStarted.setOnClickListener {
            val name = binding.num.text.toString()
            val pan = binding.num4.text.toString()
            val pin = binding.num5.text.toString()
            val date = binding.num2.text.toString()
            val gender = binding.rgrp.checkedRadioButtonId
            if (name.isEmpty()) {
                binding.num.error = "required"
                binding.num.requestFocus()
            }
            else if (date.isEmpty()) {
                binding.num2.error = "required"
                binding.num5.requestFocus()
            }
            else if (pan.isEmpty()) {
                binding.num4.error = "required"
                binding.num4.requestFocus()
            } 
            else if (pin.isEmpty()) {
                binding.num5.error = "required"
                binding.num5.requestFocus()
            }
              else if (gender == -1)
            {
                  binding.gu.error = ""
                  binding.rgrp.requestFocus()
            }
            //just we need last check for radio btn are selected or not
            else {
                //first we store user data in db
                saveUserData()
                if (business == "BUSINESS_CODE") {
                    val i = Intent(this, Businessdetails::class.java)
                    startActivity(i)

                } else if (home == "HOME_CODE") {
                    val i = Intent(this, Homedetails::class.java)
                    startActivity(i)

                } else {
                    val i = Intent(this, Pocketdetails::class.java)
                    startActivity(i)

                }
                saveUserData()
            }
        }
    }

    private fun saveUserData() {
        val hashMap = HashMap<String, Any>()
        hashMap.put("uid", FirebaseAuth.getInstance().currentUser?.uid.toString())
        hashMap.put("name", FirebaseAuth.getInstance().currentUser?.displayName.toString())
        hashMap.put("email", FirebaseAuth.getInstance().currentUser?.email.toString())
        hashMap.put("phn", FirebaseAuth.getInstance().currentUser?.phoneNumber.toString())
        hashMap.put("Full name as per PAN",binding.num.text.toString())
        hashMap.put("Date of birth",binding.num2.text.toString())
        hashMap.put("PAN",binding.num4.text.toString())
        hashMap.put("PIN_CODE",binding.num5.text.toString())
        hashMap.put("child",true)
        FirebaseDatabase.getInstance().getReference(REFERENCE).child(FirebaseAuth.getInstance().currentUser?.uid.toString()).setValue(hashMap)
    }

    override fun onResume() {
        super.onResume()
        try{
            val business = intent.getStringExtra("business")
            val home = intent.getStringExtra("home")
            val pocket = intent.getStringExtra("pocket")
            if (business == "BUSINESS_CODE") {
                REFERENCE = "BUSINESS_LOAN"

                FirebaseDatabase.getInstance().getReference(REFERENCE)
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString()).addValueEventListener(object:ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            try{
                                if (snapshot.exists()){
                                    if (snapshot.child("child").value == true){
                                        val i = Intent(this@DetailsActivity,Businessdetails::class.java)
                                        startActivity(i)
                                        finish()
                                    }
                                    else{
                                        binding.pg.visibility = View.GONE
                                    }
                                }
                                else{
                                    binding.pg.visibility = View.GONE
                                }
                            }catch (e:Exception){
                                Toast.makeText(this@DetailsActivity,e.message.toString(),Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError)
                        {
                            Toast.makeText(this@DetailsActivity, error.message,Toast.LENGTH_SHORT).show()
                        }

                    })


            } else if (home == "HOME_CODE") {
                REFERENCE = "HOME_LOAN"

                FirebaseDatabase.getInstance().getReference(REFERENCE)
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString()).addValueEventListener(object:ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            try{
                                if (snapshot.exists()){
                                    if (snapshot.child("child").value == true){
                                        val i = Intent(this@DetailsActivity, Homedetails::class.java)
                                        startActivity(i)
                                        finish()
                                    }
                                    else{
                                        binding.pg.visibility = View.GONE
                                    }
                                }
                                else{
                                    binding.pg.visibility = View.GONE
                                }
                            }catch (e:Exception){
                                Toast.makeText(this@DetailsActivity,e.message.toString(),Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError)
                        {
                            Toast.makeText(this@DetailsActivity, error.message,Toast.LENGTH_SHORT).show()
                        }

                    })

            } else {
                REFERENCE = "POCKET_LOAN"

                FirebaseDatabase.getInstance().getReference(REFERENCE)
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString()).addValueEventListener(object:ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            try{
                                if (snapshot.exists()){
                                    if (snapshot.child("child").value == true){
                                        val i = Intent(this@DetailsActivity, Pocketdetails::class.java)
                                        startActivity(i)
                                        finish()
                                    }
                                    else{
                                        binding.pg.visibility = View.GONE
                                    }
                                }
                                else{
                                    binding.pg.visibility = View.GONE
                                }
                            }catch (e:Exception){
                                Toast.makeText(this@DetailsActivity,e.message.toString(),Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError)
                        {
                            Toast.makeText(this@DetailsActivity, error.message,Toast.LENGTH_SHORT).show()
                        }

                    })

            }
        }
        catch (e:Exception)
        {
            Toast.makeText(this@DetailsActivity,e.message.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(this,LoanOfferActivity::class.java)
        startActivity(i)
        finish()
    }
}
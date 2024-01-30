package com.deepakdevloper.loanapp.loanActivity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.deepakdevloper.loanapp.databinding.ActivityLoanlimitBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap

class Loanlimit : AppCompatActivity() {
    private lateinit var binding:ActivityLoanlimitBinding
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoanlimitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val b = intent.getStringExtra("business")
        val h = intent.getStringExtra("home")
        val p = intent.getStringExtra("pocket")

        if(b == "BUSINESS_DETAILS"){
            val value = 30
            binding.i1.setText("₹1,00,000")
            binding.i3.setText("₹1,00,000")
            binding.i4.setText("₹25,000")
            binding.i4xx.setText("*Include 25% intrest")
            binding.i5.setText("₹1,25,000")

            repaymentdate(value)

        }else if(h == "HOME_DETAILS"){
            val value = 15
            binding.i1.setText("₹50,000")
            binding.i3.setText("₹50,000")
            binding.i4.setText("₹7,500")
            binding.i4xx.setText("*Include 15% intrest")
            binding.i5.setText("₹57,500")
            repaymentdate(value)

        }else{
            val value = 7
            binding.i1.setText("₹30,000")
            binding.i3.setText("₹30,000")
            binding.i4.setText("₹4,500")
            binding.i4xx.setText("*Include 15% intrest")
            binding.i5.setText("₹34,500")
            repaymentdate(value)
        }

        binding.btnGetStarted22.setOnClickListener {

          if(b == "BUSINESS_DETAILS"){
              val AlphaNumericString = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                      + "0123456789"
                      + "abcdefghijklmnopqrstuvxyz")
              val sb: StringBuilder = StringBuilder(5)
              for (i in 0 until 5) {
                  // generate a random number between
                  // 0 to AlphaNumericString variable length
                  val index = (AlphaNumericString.length
                          * Math.random()).toInt()
                  // add Character one by one in end of sb
                  sb.append(AlphaNumericString[index])
              }
              val sdf = SimpleDateFormat("MM/dd/yyyy")
              val curDate = sdf.format(Date())
              val hashMap = HashMap<String,Any>()
              hashMap.put("verify",false)
              hashMap.put("securityCodeforLoan",sb.toString())
              hashMap.put("loanApplydate",curDate)
              hashMap.put("repayDate",binding.i55.text.toString())
              hashMap.put("totalpayable",binding.i5.text.toString())
              FirebaseDatabase.getInstance().getReference("BUSINESS_LOAN").child(FirebaseAuth.getInstance().currentUser?.uid.toString()).updateChildren(hashMap)

          }else if(h == "HOME_DETAILS"){
              val AlphaNumericString = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                      + "0123456789"
                      + "abcdefghijklmnopqrstuvxyz")
              val sb: StringBuilder = StringBuilder(5)
              for (i in 0 until 5) {
                  // generate a random number between
                  // 0 to AlphaNumericString variable length
                  val index = (AlphaNumericString.length
                          * Math.random()).toInt()
                  // add Character one by one in end of sb
                  sb.append(AlphaNumericString[index])
              }
              val sdf = SimpleDateFormat("MM/dd/yyyy")
              val curDate = sdf.format(Date())
              val hashMap = HashMap<String,Any>()
              hashMap.put("verify",false)
              hashMap.put("securityCodeforLoan",sb.toString())
              hashMap.put("loanApplydate",curDate)
              hashMap.put("repayDate",binding.i55.text.toString())
              hashMap.put("totalpayable",binding.i5.text.toString())
              FirebaseDatabase.getInstance().getReference("HOME_LOAN").child(FirebaseAuth.getInstance().currentUser?.uid.toString()).updateChildren(hashMap)


          }else{
              val AlphaNumericString = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                      + "0123456789"
                      + "abcdefghijklmnopqrstuvxyz")
              val sb: StringBuilder = StringBuilder(5)
              for (i in 0 until 5) {
                  // generate a random number between
                  // 0 to AlphaNumericString variable length
                  val index = (AlphaNumericString.length
                          * Math.random()).toInt()
                  // add Character one by one in end of sb
                  sb.append(AlphaNumericString[index])
              }
              val sdf = SimpleDateFormat("MM/dd/yyyy")
              val curDate = sdf.format(Date())
              val hashMap = HashMap<String,Any>()
              hashMap.put("verify",false)
              hashMap.put("securityCodeforLoan",sb.toString())
              hashMap.put("loanApplydate",curDate)
              hashMap.put("repayDate",binding.i55.text.toString())
              hashMap.put("totalpayable",binding.i5.text.toString())
              FirebaseDatabase.getInstance().getReference("POCKET_LOAN").child(FirebaseAuth.getInstance().currentUser?.uid.toString()).updateChildren(hashMap)

          }
        }
        try {
            FirebaseDatabase.getInstance().getReference("BUSINESS_LOAN").child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            binding.btnGetStarted.visibility = View.VISIBLE
                            val loanLimit = snapshot.child("verify").value
                            val loanLimitcode = snapshot.child("securityCodeforLoan").value.toString()
                            if (loanLimit == false){
                                binding.linearColumnprice.visibility = View.GONE
                                binding.linearColumnprice1.visibility = View.VISIBLE
                                binding.limitText2.text = "Save this secret code for verification $loanLimitcode"

                                binding.btnGetStarted.setOnClickListener {
                                    val i = Intent(this@Loanlimit,LoanOfferActivity::class.java)
                                    startActivity(i)
                                    finish()
                                }
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }catch (e:Exception){
            e.printStackTrace()
        }
        try {
            FirebaseDatabase.getInstance().getReference("HOME_LOAN").child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            binding.btnGetStarted.visibility = View.VISIBLE
                            val loanLimit = snapshot.child("verify").value
                            val loanLimitcode = snapshot.child("securityCodeforLoan").value.toString()
                            if (loanLimit == false){
                                binding.linearColumnprice.visibility = View.GONE
                                binding.linearColumnprice1.visibility = View.VISIBLE
                                binding.limitText2.text = "Save this secret code for verification $loanLimitcode"

                                binding.btnGetStarted.setOnClickListener {
                                    val i = Intent(this@Loanlimit,LoanOfferActivity::class.java)
                                    startActivity(i)
                                    finish()
                                }
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }catch (e:Exception){
            e.printStackTrace()
        }
        try {
            FirebaseDatabase.getInstance().getReference("POCKET_LOAN").child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            binding.btnGetStarted.visibility = View.VISIBLE
                            val loanLimit = snapshot.child("verify").value
                            val loanLimitcode = snapshot.child("securityCodeforLoan").value.toString()
                            if (loanLimit == false){
                                binding.linearColumnprice.visibility = View.GONE
                                binding.linearColumnprice1.visibility = View.VISIBLE
                                binding.limitText2.text = "Save this secret code for verification $loanLimitcode"

                                binding.btnGetStarted.setOnClickListener {
                                    val i = Intent(this@Loanlimit,LoanOfferActivity::class.java)
                                    startActivity(i)
                                    finish()
                                }

                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun repaymentdate(value:Int) {

        val currentDate = LocalDate.now()

        // Add 30 days to the current date
        val newDate = currentDate.plusDays(value.toLong())

        // Format the date as a string (you can change the format as needed)
        val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
        val dateString = newDate.format(dateFormat)

        // Set the formatted date to the TextView
        binding.i55.text = dateString

    }
    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(this,LoanOfferActivity::class.java)
        startActivity(i)
        finish()
    }
}
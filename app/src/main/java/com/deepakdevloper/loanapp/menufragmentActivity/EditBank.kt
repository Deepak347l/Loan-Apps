package com.deepakdevloper.loanapp.menufragmentActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.deepakdevloper.loanapp.Help.help
import com.deepakdevloper.loanapp.MainActivity
import com.deepakdevloper.loanapp.Notification.notificationActivity
import com.deepakdevloper.loanapp.R
import com.deepakdevloper.loanapp.databinding.ActivityEditBankBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.HashMap

class EditBank : AppCompatActivity() {
    private lateinit var binding: ActivityEditBankBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBankBinding.inflate(layoutInflater)
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
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString()).addValueEventListener(object:
                    ValueEventListener {
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
            val i = Intent(this,MainActivity::class.java)
            startActivity(i)
            finish()
        }
        binding.btnGetStarted.setOnClickListener {
            val bAcountnumber = binding.num.text.toString()
            val isfcCode = binding.numa.text.toString()
            val bName = binding.numb.text.toString()
            val upiId = binding.numc.text.toString()
            if (bAcountnumber.isEmpty()) {
                binding.num.error = "required"
                binding.num.requestFocus()
            } else if (isfcCode.isEmpty()) {
                binding.numa.error = "required"
                binding.numa.requestFocus()
            } else if (bName.isEmpty()) {
                binding.numb.error = "required"
                binding.numb.requestFocus()
            } else if (upiId.isEmpty()) {
                binding.numc.error = "required"
                binding.numc.requestFocus()
            }
            //just we need last check for radio btn are selected or not
            else {
                //first we store user data in db
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
                finish()
                Toast.makeText(this,"Update Your ALL Details", Toast.LENGTH_SHORT).show()
                saveUserData()
            }
        }
    }

    private fun saveUserData() {
        val hashMap = HashMap<String, Any>()
        hashMap.put("Bank Acount Number",binding.num.text.toString())
        hashMap.put("ISFC Code",binding.numa.text.toString())
        hashMap.put("Bank Name",binding.numb.text.toString())
        hashMap.put("Upi ID",binding.numc.text.toString())
        hashMap.put("child",true)
        FirebaseDatabase.getInstance().getReference("BUSINESS_LOAN").child(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .child("BUSINESS_DETAILS").child(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .child("KYC_DETAILS")
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("BANK_DETAILS")
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .updateChildren(hashMap)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(this,MainActivity::class.java)
        startActivity(i)
        finish()
    }
}
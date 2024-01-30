package com.deepakdevloper.loanapp.loanActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.deepakdevloper.loanapp.Help.help
import com.deepakdevloper.loanapp.Notification.notificationActivity
import com.deepakdevloper.loanapp.R
import com.deepakdevloper.loanapp.databinding.ActivityHomedetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.HashMap

class Homedetails : AppCompatActivity() {
    private lateinit var binding: ActivityHomedetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomedetailsBinding.inflate(layoutInflater)
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

        binding.btnGetStarted.setOnClickListener {
            val name = binding.num.text.toString()
            val monthlyIncome = binding.num4.text.toString()
            val emergancyContact1 = binding.num5C1.text.toString()
            val emergancyContact2 = binding.num5C2.text.toString()
            if (name.isEmpty()) {
                binding.num.error = "required"
                binding.num.requestFocus()
            }  else if (monthlyIncome.isEmpty()) {
                binding.num4.error = "required"
                binding.num4.requestFocus()
            } else if (emergancyContact1.isEmpty()) {
                binding.num5C1.error = "required"
                binding.num5C1.requestFocus()
            } else if (emergancyContact2.isEmpty()) {
                binding.num5C2.error = "required"
                binding.num5C2.requestFocus()
            }
            //just we need last check for radio btn are selected or not
            else {
                //first we store user data in db
                val i = Intent(this,FaseVarification::class.java)
                i.putExtra("home","HOME_DETAILS")
                startActivity(i)
                saveUserData()
            }
        }
    }
    private fun saveUserData() {
        val hashMap = HashMap<String, Any>()
        hashMap.put("Home Adres",binding.num.text.toString())
        hashMap.put("Montly Income",binding.num4.text.toString())
        hashMap.put("phn1",binding.num5C1.text.toString())
        hashMap.put("phn2",binding.num5C2.text.toString())
        hashMap.put("child",true)
        FirebaseDatabase.getInstance().getReference("HOME_LOAN").child(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .child("HOME_DETAILS").child(FirebaseAuth.getInstance().currentUser?.uid.toString()).updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()
        try{
            FirebaseDatabase.getInstance().getReference("HOME_LOAN")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("HOME_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString()).addValueEventListener(object:
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try{
                            if (snapshot.exists()){
                                if (snapshot.child("child").value == true){
                                    val i = Intent(this@Homedetails,FaseVarification::class.java)
                                    i.putExtra("home","HOME_DETAILS")
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
                            Toast.makeText(this@Homedetails,e.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError)
                    {
                        Toast.makeText(this@Homedetails, error.message, Toast.LENGTH_SHORT).show()
                    }

                })
        }
        catch (e:Exception)
        {
            Toast.makeText(this@Homedetails,e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(this,LoanOfferActivity::class.java)
        startActivity(i)
        finish()
    }
}
package com.deepakdevloper.loanapp.Help

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.deepakdevloper.loanapp.databinding.ActivityHelpBinding
import com.deepakdevloper.loanapp.loanActivity.FaseVarification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.HashMap

class help : AppCompatActivity() {
    private lateinit var binding:ActivityHelpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGetStarted.setOnClickListener {
            val name = binding.num.text.toString()
            val monthlyIncome = binding.num4.text.toString()
            val emergancyContact1 = binding.num5C1.text.toString()
            if (name.isEmpty()) {
                binding.num.error = "required"
                binding.num.requestFocus()
            }  else if (monthlyIncome.isEmpty()) {
                binding.num4.error = "required"
                binding.num4.requestFocus()
            } else if (emergancyContact1.isEmpty()) {
                binding.num5C1.error = "required"
                binding.num5C1.requestFocus()
            }
            //just we need last check for radio btn are selected or not
            else {
                //first we store user data in db
                Toast.makeText(this,"sumbited",Toast.LENGTH_SHORT).show()
                saveUserData()
            }
        }
    }

    private fun saveUserData() {
        val hashMap = HashMap<String, Any>()
        hashMap.put("name",binding.num.text.toString())
        hashMap.put("number",binding.num4.text.toString())
        hashMap.put("issue",binding.num5C1.text.toString())
        FirebaseDatabase.getInstance().getReference("help").child(FirebaseAuth.getInstance().currentUser?.uid.toString())
            .setValue(hashMap)
    }
}
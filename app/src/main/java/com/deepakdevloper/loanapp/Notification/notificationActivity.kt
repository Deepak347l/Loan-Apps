package com.deepakdevloper.loanapp.Notification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.deepakdevloper.loanapp.databinding.ActivityNotificationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class notificationActivity : AppCompatActivity() {
    private lateinit var binding:ActivityNotificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnGetStarted22.setOnClickListener {
            Toast.makeText(this,"thanks for response",Toast.LENGTH_SHORT).show()
            FirebaseDatabase.getInstance().getReference("notif")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString()).removeValue()
        }
        try{
        FirebaseDatabase.getInstance().getReference("notif")
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString()).addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val notfMsg = snapshot.child("msg").value.toString()
                        binding.txtVerfiiedyoure.text = notfMsg
                    }
                }

                override fun onCancelled(error: DatabaseError) {}

            })
    }
        catch (e:Exception){
        }
    }
}
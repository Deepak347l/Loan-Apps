package com.deepakdevloper.loanapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import com.deepakdevloper.loanapp.databinding.ActivityMainBinding
import com.deepakdevloper.loanapp.fragmentActivity.Home
import com.deepakdevloper.loanapp.fragmentActivity.Menu
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.deepakdevloper.loanapp.Help.help
import com.deepakdevloper.loanapp.Notification.notificationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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

        binding.txtWelcome.text = Html.fromHtml("<font color=${Color.BLACK}>LOAN </font>" + "" + "<font color=${Color.parseColor("#ffa500")}>CASH</font>")
        val f = Home()
        val s = Menu()
        setCurrentFragment(f)
        binding.tabLayout.setOnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.page_1-> setCurrentFragment(f)
                R.id.page_2-> setCurrentFragment(s)
            }
            true
        }
    }
    private fun setCurrentFragment(f: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            android.R.anim.slide_in_left,
            android.R.anim.slide_out_right
        )
        transaction.replace(binding.frameLayout.id, f)
        transaction.commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
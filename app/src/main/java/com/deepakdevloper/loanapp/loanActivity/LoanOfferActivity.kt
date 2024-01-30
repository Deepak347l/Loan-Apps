package com.deepakdevloper.loanapp.loanActivity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import com.deepakdevloper.loanapp.Help.help
import com.deepakdevloper.loanapp.MainActivity
import com.deepakdevloper.loanapp.Notification.notificationActivity
import com.deepakdevloper.loanapp.R
import com.deepakdevloper.loanapp.databinding.ActivityLoanOfferBinding
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_loan_offer.view.*

class LoanOfferActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoanOfferBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoanOfferBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.msc.setOnClickListener {
            val i = Intent(this,notificationActivity::class.java)
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

        binding.msc1.setOnClickListener {
            val i = Intent(this, help::class.java)
            startActivity(i)
        }


        val imageList = ArrayList<SlideModel>()

        imageList.add(SlideModel(R.drawable.banertwonew,ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banerone,ScaleTypes.FIT))

        binding.imageCalculator.setImageList(imageList)
        binding.txtWelcome.text = Html.fromHtml("<font color=${Color.BLACK}>LOAN </font>" + "" + "<font color=${Color.parseColor("#ffa500")}>CASH</font>")
        binding.b1.setOnClickListener {
            val i = Intent(this,DetailsActivity::class.java)
            i.putExtra("business","BUSINESS_CODE")
            startActivity(i)
        }
        binding.h1.setOnClickListener {
            val i = Intent(this,DetailsActivity::class.java)
            i.putExtra("home","HOME_CODE")
            startActivity(i)
        }
        binding.p1.setOnClickListener {
            val i = Intent(this,DetailsActivity::class.java)
            i.putExtra("pocket","POCKET_CODE")
            startActivity(i)
        }
    }
        override fun onStart()
        {
            super.onStart()

            try {
                FirebaseDatabase.getInstance().getReference("BUSINESS_LOAN").child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()){
                                val loanLimit = snapshot.child("verify").value
                                if (loanLimit == true){
                                    val i = Intent(this@LoanOfferActivity,MainActivity::class.java)
                                    startActivity(i)
                                    finish()
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
                                val loanLimit = snapshot.child("verify").value
                                if (loanLimit == true){
                                    val i = Intent(this@LoanOfferActivity,MainActivity::class.java)
                                    startActivity(i)
                                    finish()
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
                                val loanLimit = snapshot.child("verify").value
                                if (loanLimit == true){
                                    val i = Intent(this@LoanOfferActivity,MainActivity::class.java)
                                    startActivity(i)
                                    finish()
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
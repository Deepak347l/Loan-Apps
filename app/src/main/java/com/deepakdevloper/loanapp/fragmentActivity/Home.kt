package com.deepakdevloper.loanapp.fragmentActivity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.deepakdevloper.loanapp.R
import com.deepakdevloper.loanapp.databinding.FragmentHomeBinding
import com.deepakdevloper.loanapp.databinding.FragmentMenuBinding
import com.deepakdevloper.loanapp.homefragmentActivity.activitydone
import com.deepakdevloper.loanapp.loanActivity.BankdetailsActivity
import com.deepakdevloper.loanapp.loanActivity.LoanOfferActivity
import com.deepakdevloper.loanapp.loanActivity.Loanlimit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class Home : Fragment() {
    private var _binding:FragmentHomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.btnGetStarted2.setOnClickListener {

        }

        try {
            FirebaseDatabase.getInstance().getReference("BUSINESS_LOAN").child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            val loanLimit = snapshot.child("verify").value
                            val repayDate = snapshot.child("repayDate").value.toString()
                            val totalpayable = snapshot.child("totalpayable").value.toString()
                            if (loanLimit == true){
                                binding.i52.text = totalpayable
                                binding.i552.text = repayDate
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
                            val repayDate = snapshot.child("repayDate").value.toString()
                            val totalpayable = snapshot.child("totalpayable").value.toString()
                            if (loanLimit == true){
                                binding.i52.text = totalpayable
                                binding.i552.text = repayDate
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
                            val repayDate = snapshot.child("repayDate").value.toString()
                            val totalpayable = snapshot.child("totalpayable").value.toString()
                            if (loanLimit == true){
                                binding.i52.text = totalpayable
                                binding.i552.text = repayDate
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }catch (e:Exception){
            e.printStackTrace()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
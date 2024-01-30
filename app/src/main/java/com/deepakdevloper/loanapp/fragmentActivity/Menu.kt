package com.deepakdevloper.loanapp.fragmentActivity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.deepakdevloper.loanapp.Help.help
import com.deepakdevloper.loanapp.MainActivity
import com.deepakdevloper.loanapp.R
import com.deepakdevloper.loanapp.databinding.FragmentMenuBinding
import com.deepakdevloper.loanapp.menufragmentActivity.EditBank
import com.deepakdevloper.loanapp.signup.signinHome
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.net.URL


class Menu : Fragment() {

    private var _binding:FragmentMenuBinding? = null
    private val binding get() = _binding!!

    var url = "https//www.google.com"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        //profile image
        try{
            FirebaseDatabase.getInstance().getReference("otherDetails")
                .child("golbal")
                .addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            if (snapshot.exists()) {
                                url = snapshot.child("privacyurl").value.toString()
                            }
                        } catch (e: Exception) {
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })

        }catch (e:Exception){}
        try{
            FirebaseDatabase.getInstance().getReference("POCKET_LOAN")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("POCKET_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("KYC_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            if (snapshot.exists()) {
                                val profileImage = snapshot.child("selfie").value.toString()
                                Glide.with(context!!).load(profileImage).circleCrop().into(binding.imgView)
                            }
                        } catch (e: Exception) {
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
                }catch(e:Exception){}
        try{
            FirebaseDatabase.getInstance().getReference("HOME_LOAN")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("HOME_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("KYC_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            if (snapshot.exists()) {
                                val profileImage = snapshot.child("selfie").value.toString()
                                Glide.with(context!!).load(profileImage).circleCrop().into(binding.imgView)
                            }
                        } catch (e: Exception) {
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }catch(e:Exception){}
        try{
            FirebaseDatabase.getInstance().getReference("BUSINESS_LOAN")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("BUSINESS_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("KYC_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            if (snapshot.exists()) {
                                val profileImage = snapshot.child("selfie").value.toString()
                                Glide.with(context!!).load(profileImage).circleCrop().into(binding.imgView)
                            }
                        } catch (e: Exception) {
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }catch(e:Exception){}
        //NAME
        try{
            FirebaseDatabase.getInstance().getReference("BUSINESS_LOAN")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            if (snapshot.exists()) {
                                val name = snapshot.child("Full name as per PAN").value.toString()
                                binding.name.text = name
                            }
                        } catch (e: Exception) {
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }catch(e:Exception){}

        try{
            FirebaseDatabase.getInstance().getReference("HOME_LOAN")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            if (snapshot.exists()) {
                                val name = snapshot.child("Full name as per PAN").value.toString()
                                binding.name.text = name
                            }
                        } catch (e: Exception) {
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }catch(e:Exception){}

        try{
            FirebaseDatabase.getInstance().getReference("POCKET_LOAN")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            if (snapshot.exists()) {
                                val name = snapshot.child("Full name as per PAN").value.toString()
                                binding.name.text = name
                            }
                        } catch (e: Exception) {
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }catch(e:Exception){}

        binding.btn1.setOnClickListener {
            val i = Intent(context, EditBank::class.java)
            context?.startActivity(i)
        }
        binding.btn4.setOnClickListener {
            val i = Intent(context, signinHome::class.java)
            context?.startActivity(i)
           FirebaseAuth.getInstance().signOut()
        }

        binding.btn3.setOnClickListener {
            //privacy policy
            val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(i)
        }

        binding.btn2.setOnClickListener {
            //help activity
            val i = Intent(context,help::class.java)
            context?.startActivity(i)
        }

        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
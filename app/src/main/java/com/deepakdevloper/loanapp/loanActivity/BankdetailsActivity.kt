package com.deepakdevloper.loanapp.loanActivity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.deepakdevloper.loanapp.Help.help
import com.deepakdevloper.loanapp.Notification.notificationActivity
import com.deepakdevloper.loanapp.R
import com.deepakdevloper.loanapp.databinding.ActivityBankdetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.HashMap

class BankdetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBankdetailsBinding
    private  var pdfUri: Uri? = null
    private lateinit var storageReference: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBankdetailsBinding.inflate(layoutInflater)
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
        binding.pg.visibility = View.GONE
        storageReference = FirebaseStorage.getInstance().reference
        binding.btnGetStarted.setOnClickListener {
            val bAcountnumber = binding.num.text.toString()
            val isfcCode = binding.numa.text.toString()
            val bName = binding.numb.text.toString()
            if (bAcountnumber.isEmpty()) {
                binding.num.error = "required"
                binding.num.requestFocus()
            } else if (isfcCode.isEmpty()) {
                binding.numa.error = "required"
                binding.numa.requestFocus()
            } else if (bName.isEmpty()) {
                binding.numb.error = "required"
                binding.numb.requestFocus()
            } else if (pdfUri == null){
                Toast.makeText(this,"Please upload bank statement",Toast.LENGTH_SHORT).show()
            }
            //just we need last check for radio btn are selected or not
            else {
                binding.pg.visibility = View.VISIBLE
                uploadPdf(pdfUri!!)
            }
        }
        binding.btnHelpc.setOnClickListener {
            selectPdf()
        }
    }

    private fun selectPdf() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {
            pdfUri = data.data
        }
    }

    private fun uploadPdf(pdfUri: Uri) {
        val pdfName = "pdf_${System.currentTimeMillis()}.pdf"
        val pdfRef = storageReference.child("pdfs/$pdfName")
        pdfRef.putFile(pdfUri)
            .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                // Get the download URL from the task snapshot
                pdfRef.downloadUrl.addOnSuccessListener { uri ->
                    val pdfUrl = uri.toString()
                    saveUserData(pdfName, pdfUrl)
                }.addOnFailureListener { exception ->
                    showToast("Failed to get PDF URL$exception")
                }
            }
            .addOnFailureListener { exception: Exception ->
                Toast.makeText(this, "Failed to upload PDF $exception", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showToast(message: String) {
        // Helper function to show Toast messages
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun saveUserData(pdfName: String, pdfUrl: String) {
        val b = intent.getStringExtra("business")
        val h = intent.getStringExtra("home")
        val p = intent.getStringExtra("pocket")
        if (b == "BUSINESS_DETAILS") {
            val hashMap = HashMap<String, Any>()
            hashMap.put("Bank Acount Number", binding.num.text.toString())
            hashMap.put("ISFC Code", binding.numa.text.toString())
            hashMap.put("Bank Name", binding.numb.text.toString())
            hashMap.put("bankStatement", pdfUrl)
            hashMap.put("child", true)
            FirebaseDatabase.getInstance().getReference("BUSINESS_LOAN")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("BUSINESS_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("KYC_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("BANK_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .updateChildren(hashMap).addOnSuccessListener {
                    binding.pg.visibility = View.GONE
                    val i = Intent(this, Loanlimit::class.java)
                    i.putExtra("business","BUSINESS_DETAILS")
                    startActivity(i)
                }.addOnFailureListener {
                    binding.pg.visibility = View.GONE
                    showToast("Failed to upload ${it.message}")
                }
        }
        else if(h == "HOME_DETAILS"){

            val hashMap = HashMap<String, Any>()
            hashMap.put("Bank Acount Number", binding.num.text.toString())
            hashMap.put("ISFC Code", binding.numa.text.toString())
            hashMap.put("Bank Name", binding.numb.text.toString())
            hashMap.put("bankStatement", pdfUrl)
            hashMap.put("child", true)
            FirebaseDatabase.getInstance().getReference("HOME_LOAN")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("HOME_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("KYC_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("BANK_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .updateChildren(hashMap).addOnSuccessListener {
                    binding.pg.visibility = View.GONE
                    val i = Intent(this, Loanlimit::class.java)
                    i.putExtra("home","HOME_DETAILS")
                    startActivity(i)
                }.addOnFailureListener {
                    binding.pg.visibility = View.GONE
                    showToast("Failed to upload ${it.message}")
                }

        }
        else{

            val hashMap = HashMap<String, Any>()
            hashMap.put("Bank Acount Number", binding.num.text.toString())
            hashMap.put("ISFC Code", binding.numa.text.toString())
            hashMap.put("Bank Name", binding.numb.text.toString())
            hashMap.put("bankStatement", pdfUrl)
            hashMap.put("child", true)
            FirebaseDatabase.getInstance().getReference("POCKET_LOAN")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("POCKET_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("KYC_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("BANK_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .updateChildren(hashMap).addOnSuccessListener {
                    binding.pg.visibility = View.GONE
                    val i = Intent(this, Loanlimit::class.java)
                    i.putExtra("pocket","POCKET_DETAILS")
                    startActivity(i)
                }.addOnFailureListener {
                    binding.pg.visibility = View.GONE
                    showToast("Failed to upload ${it.message}")
                }

        }
    }

    override fun onResume()
    {
        super.onResume()
        try{
            val b = intent.getStringExtra("business")
            val h = intent.getStringExtra("home")
            val p = intent.getStringExtra("pocket")

            if ( b == "BUSINESS_DETAILS" ) {
                FirebaseDatabase.getInstance().getReference("BUSINESS_LOAN")
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .child("BUSINESS_DETAILS")
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .child("KYC_DETAILS")
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("BANK_DETAILS")
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .addValueEventListener(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            try {
                                if (snapshot.exists()) {
                                    if (snapshot.child("child").value == true) {
                                        val i = Intent(this@BankdetailsActivity, Loanlimit::class.java)
                                        i.putExtra("business","BUSINESS_DETAILS")
                                        startActivity(i)
                                        finish()
                                    } else {
                                        binding.pg.visibility = View.GONE
                                    }
                                } else {
                                    binding.pg.visibility = View.GONE
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@BankdetailsActivity,
                                    e.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@BankdetailsActivity, error.message, Toast.LENGTH_SHORT)
                                .show()
                        }

                    })
            } else if (h == "HOME_DETAILS") {

                FirebaseDatabase.getInstance().getReference("HOME_LOAN")
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .child("HOME_DETAILS")
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .child("KYC_DETAILS")
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("BANK_DETAILS")
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .addValueEventListener(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            try {
                                if (snapshot.exists()) {
                                    if (snapshot.child("child").value == true) {
                                        val i = Intent(this@BankdetailsActivity, Loanlimit::class.java)
                                        i.putExtra("home","HOME_DETAILS")
                                        startActivity(i)
                                        finish()
                                    } else {
                                        binding.pg.visibility = View.GONE
                                    }
                                } else {
                                    binding.pg.visibility = View.GONE
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@BankdetailsActivity,
                                    e.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@BankdetailsActivity, error.message, Toast.LENGTH_SHORT)
                                .show()
                        }

                    })

            }
            else{


                FirebaseDatabase.getInstance().getReference("POCKET_LOAN")
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .child("POCKET_DETAILS")
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .child("KYC_DETAILS")
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString()).child("BANK_DETAILS")
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .addValueEventListener(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            try {
                                if (snapshot.exists()) {
                                    if (snapshot.child("child").value == true) {
                                        val i = Intent(this@BankdetailsActivity, Loanlimit::class.java)
                                        i.putExtra("pocket","POCKET_DETAILS")
                                        startActivity(i)
                                        finish()
                                    } else {
                                        binding.pg.visibility = View.GONE
                                    }
                                } else {
                                    binding.pg.visibility = View.GONE
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@BankdetailsActivity,
                                    e.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@BankdetailsActivity, error.message, Toast.LENGTH_SHORT)
                                .show()
                        }

                    })


            }
        }
        catch (e:Exception)
        {
            Toast.makeText(this@BankdetailsActivity,e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

        override fun onBackPressed() {
            super.onBackPressed()
            val i = Intent(this,LoanOfferActivity::class.java)
            startActivity(i)
            finish()
        }
}
package com.deepakdevloper.loanapp.loanActivity

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import com.deepakdevloper.loanapp.Help.help
import com.deepakdevloper.loanapp.Notification.notificationActivity
import com.deepakdevloper.loanapp.R
import com.deepakdevloper.loanapp.databinding.ActivityFaseVarificationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class FaseVarification : AppCompatActivity() {
    private lateinit var binding: ActivityFaseVarificationBinding
    private val IMAGE_CAPTURE_REQUEST = 1
    private lateinit var storageReference: StorageReference
    private  var imageBitmap: Bitmap ? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaseVarificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.msc1.setOnClickListener {
            val i = Intent(this, help::class.java)
            startActivity(i)
        }

        binding.ms.setOnClickListener {
            val i = Intent(this,LoanOfferActivity::class.java)
            startActivity(i)
            finish()
        }

        binding.pg.visibility = View.GONE
         storageReference = FirebaseStorage.getInstance().reference
         binding.btnGetStarted.setOnClickListener {
            val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(captureIntent, IMAGE_CAPTURE_REQUEST)
        }

        binding.btnGetStarted2.setOnClickListener {
            if(imageBitmap != null){
                uploadFirebase(imageBitmap!!)
                binding.pg.visibility = View.VISIBLE
            }else{
                Toast.makeText(this,"Please Upload selfe  First Click On selfe button",Toast.LENGTH_SHORT).show()
            }
            //now we upload details
        }

    }

    private fun uploadFirebase(imageBitmap: Bitmap) {
        val name = "selfie"
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageData = baos.toByteArray()


        // Upload image to Firebase Storage
        val imageRef = storageReference.child("images/${System.currentTimeMillis()}_IMG.jpg")
        val uploadTask = imageRef.putBytes(imageData)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Image uploaded successfully, get the download URL
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                // Save the download URL to Firestore
                saveUrlToFirestore(uri.toString(),name)
            }.addOnFailureListener {
                // Handle failure to get download URL
                showToast("Failed to get download URL")
            }
        }.addOnFailureListener {
            // Handle failure to upload image
            showToast("Failed to upload image")
        }
    }

    private fun saveUrlToFirestore(uri: String,name:String) {
        val b = intent.getStringExtra("business")
        val h = intent.getStringExtra("home")
        val p = intent.getStringExtra("pocket")

        if ( b == "BUSINESS_DETAILS" ) {
            val hashMap = HashMap<String, Any>()
            hashMap.put(name, uri)
            FirebaseDatabase.getInstance().getReference("BUSINESS_LOAN")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("BUSINESS_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("KYC_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .updateChildren(hashMap).addOnSuccessListener {
                    showToast("Uploaded")
                    binding.pg.visibility = View.GONE
                    val i = Intent(this,PanVerification::class.java)
                    i.putExtra("business","BUSINESS_DETAILS")
                    startActivity(i)
                }.addOnFailureListener {
                    showToast("Something wrong retry")
                    binding.pg.visibility = View.GONE
                }
        }

        else if (h == "HOME_DETAILS"){
            val hashMap = HashMap<String, Any>()
            hashMap.put(name, uri)
            FirebaseDatabase.getInstance().getReference("HOME_LOAN")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("HOME_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("KYC_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .updateChildren(hashMap).addOnSuccessListener {
                    showToast("Uploaded")
                    binding.pg.visibility = View.GONE
                        val i = Intent(this, PanVerification::class.java)
                        i.putExtra("home", "HOME_DETAILS")
                        startActivity(i)
                }.addOnFailureListener {
                    showToast("Something wrong retry")
                    binding.pg.visibility = View.GONE
                }
        }
        else{
            val hashMap = HashMap<String, Any>()
            hashMap.put(name, uri)
            FirebaseDatabase.getInstance().getReference("POCKET_LOAN")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("POCKET_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("KYC_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .updateChildren(hashMap).addOnSuccessListener {
                    showToast("Uploaded")
                    binding.pg.visibility = View.GONE
                        val i = Intent(this, PanVerification::class.java)
                        i.putExtra("pocket", "POCKET_DETAILS")
                        startActivity(i)
                }.addOnFailureListener {
                    showToast("Something wrong retry")
                    binding.pg.visibility = View.GONE
                }
        }

    }

    private fun showToast(message: String) {
        // Helper function to show Toast messages
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CAPTURE_REQUEST && resultCode == RESULT_OK) {
            // Get the image bitmap from the camera intent result
            val extras = data?.extras
            imageBitmap = extras?.get("data") as Bitmap
            binding.i3.setImageBitmap(imageBitmap)
            // Convert bitmap to byte array
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
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .child("PRIVATE_DETAILS")
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .addValueEventListener(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            try {
                                if (snapshot.exists()) {
                                    if (snapshot.child("child").value == true) {
                                        val i = Intent(this@FaseVarification, BankdetailsActivity::class.java)
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
                                    this@FaseVarification,
                                    e.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@FaseVarification, error.message, Toast.LENGTH_SHORT)
                                .show()
                        }

                    })
            } else if (h == "HOME_DETAILS") {

                FirebaseDatabase.getInstance().getReference("HOME_LOAN")
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .child("HOME_DETAILS")
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .child("KYC_DETAILS")
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .child("PRIVATE_DETAILS")
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .addValueEventListener(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            try {
                                if (snapshot.exists()) {
                                    if (snapshot.child("child").value == true) {
                                        val i = Intent(this@FaseVarification, BankdetailsActivity::class.java)
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
                                    this@FaseVarification,
                                    e.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@FaseVarification, error.message, Toast.LENGTH_SHORT)
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
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .child("PRIVATE_DETAILS")
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .addValueEventListener(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            try {
                                if (snapshot.exists()) {
                                    if (snapshot.child("child").value == true) {
                                        val i = Intent(this@FaseVarification, BankdetailsActivity::class.java)
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
                                    this@FaseVarification,
                                    e.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@FaseVarification, error.message, Toast.LENGTH_SHORT)
                                .show()
                        }

                    })


            }
        }
        catch (e:Exception)
        {
            Toast.makeText(this@FaseVarification,e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(this,LoanOfferActivity::class.java)
        startActivity(i)
        finish()
    }
}
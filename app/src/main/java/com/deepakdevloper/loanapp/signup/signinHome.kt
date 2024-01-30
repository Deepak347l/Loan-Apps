package com.deepakdevloper.loanapp.signup

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.deepakdevloper.loanapp.MainActivity
import com.deepakdevloper.loanapp.R
import com.deepakdevloper.loanapp.databinding.ActivitySigninHomeBinding
import com.deepakdevloper.loanapp.loanActivity.LoanOfferActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap


class signinHome : AppCompatActivity() {

    private lateinit var binding: ActivitySigninHomeBinding
    private val RC_SIGN_IN: Int = 123
    private val TAG = "SignInActivity Tag"
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var verificationId: String
    private lateinit var edOtp1:TextInputEditText
    private lateinit var btn11:TextView
    private lateinit var btn22:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //just transfer our activity to mobile number login
        binding.linearButton2.setOnClickListener {
       //    we are useed bootom sheet dialog there
            phnDialog()
        }
         // google login we work there directly
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth
        //handele for click on signup device
        binding.linearButton.setOnClickListener {
            signin()
        }
    }

    private fun phnDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.activity_signin_phone, null)
        val phnNumber = view.findViewById<TextInputEditText>(R.id.num)
        val sumbitBtn = view.findViewById<AppCompatButton>(R.id.btnGetStarted)
        sumbitBtn.setOnClickListener {
            try
            {
                //we write here login code using mobile number
                if(phnNumber.text.toString().isEmpty()){
                    phnNumber.setError("required number")
                    phnNumber.requestFocus()
                }else{
                    phnNumberverification(view, phnNumber.text.toString())
                }
            }
            catch (e: Exception){Log.d(TAG, e.message.toString())}
        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation;
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    private fun phnNumberverification(view: View, toString: String) {
        val otpTextBox = view.findViewById<TextInputLayout>(R.id.btnHelp2)
        val otpLayout = view.findViewById<LinearLayout>(R.id.otpLayout)
        val btn1 = view.findViewById<AppCompatButton>(R.id.btnGetStarted)
        val btn2 = view.findViewById<AppCompatButton>(R.id.btnGetStarted2)
        val txtMob = view.findViewById<TextView>(R.id.txt_mob)
        otpTextBox.visibility = View.VISIBLE
        otpLayout.visibility = View.VISIBLE
        btn2.visibility = View.VISIBLE
        btn1.visibility = View.GONE
        txtMob.visibility = View.VISIBLE

        //workingWithOtp
        val otpFilled = view.findViewById<TextInputEditText>(R.id.otpfillupfiled)
        val otpResendr = view.findViewById<TextView>(R.id.btn_reenter)
        val otpTimerfilled = view.findViewById<TextView>(R.id.btn_timer)
        btn11 = otpResendr
        btn22 = otpTimerfilled

        btn11.setOnClickListener {
            otpResendr.setVisibility(View.GONE)
            otpTimerfilled.setVisibility(View.VISIBLE)
            try {
                object : CountDownTimer(60000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                        otpTimerfilled.setText("$seconds Second Wait")
                    }

                    override fun onFinish() {
                        otpResendr.setVisibility(View.VISIBLE)
                        otpTimerfilled.setVisibility(View.GONE)
                    }
                }.start()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            sendVerificationCode("+91" + toString)
        }

        btn2.setOnClickListener {
            if (validation()) {
                verifyCode(edOtp1.getText().toString());
            }
        }

        edOtp1 = otpFilled

        sendVerificationCode("+91" + toString)
        txtMob.setText("We have sent you an SMS on " + "+91" + " " + toString + "\n with 6 digit verification code")
        try {
            object : CountDownTimer(60000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                    otpTimerfilled.setText("$seconds Second Wait")
                }

                override fun onFinish() {
                    otpResendr.setVisibility(View.VISIBLE)
                    otpTimerfilled.setVisibility(View.GONE)
                }
            }.start()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun validation(): Boolean {
        if (edOtp1.getText().toString().isEmpty()) {
            edOtp1.setError("")
            return false;
        }

        return true;
    }

    private fun verifyCode(code: String) {
        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            signInWithCredential(credential)
        }catch(e:Exception){
            Toast.makeText(this@signinHome, e.message.toString(), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener({ task ->
            if (task.isSuccessful) {
                updateUI(task.result.user)
            } else {
                Toast.makeText(this@signinHome, task.exception.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun sendVerificationCode(number: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            number,
            60,
            TimeUnit.SECONDS,
            this,
            mCallBack
        )
    }

    private val mCallBack: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                verificationId = s
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val code = phoneAuthCredential.smsCode
                if (code != null) {
                    edOtp1.setText("" + code)
                    verifyCode(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException)
            {
                Toast.makeText(this@signinHome, e.message, Toast.LENGTH_LONG).show()
            }
        }

    override fun onStart() {
        super.onStart()
        val currentuser = auth.currentUser
        if (currentuser != null){
            val i = Intent(this, LoanOfferActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    private fun signin() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account =
                completedTask.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode + e.message.toString())

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        binding.linearButton.visibility = View.GONE
        binding.linearButton2.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful){
                if (task.result.additionalUserInfo?.isNewUser == true){
                    val firebaseUser = auth.currentUser
                    updateUI(firebaseUser)
                }
                else {
                    val i = Intent(this, LoanOfferActivity::class.java)
                    startActivity(i)
                    finish()
                }
            }else {
                updateUI(null)
            }
        }
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if(firebaseUser != null) {
            val curentdeviseid = android.provider.Settings.Secure.getString(
                this.contentResolver,
                android.provider.Settings.Secure.ANDROID_ID
            )
            val AlphaNumericString = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    + "0123456789"
                    + "abcdefghijklmnopqrstuvxyz")
            val sb: StringBuilder = StringBuilder(10)
            for (i in 0 until 10) {
                // generate a random number between
                // 0 to AlphaNumericString variable length
                val index = (AlphaNumericString.length
                        * Math.random()).toInt()
                // add Character one by one in end of sb
                sb.append(AlphaNumericString[index])
            }
            val sdf = SimpleDateFormat("MM/dd/yyyy")
            val curDate = sdf.format(Date())
            val hashMap = HashMap<String, Any>()
            hashMap.put("uid", firebaseUser.uid)
            hashMap.put("name", firebaseUser.displayName.toString())
            hashMap.put("email", firebaseUser.email.toString())
            hashMap.put("number", firebaseUser.phoneNumber.toString())
            hashMap.put("user_pic", firebaseUser.photoUrl.toString())
            hashMap.put("REFER_ID", sb.toString())
            hashMap.put("DIV_ID", curentdeviseid.toString())
            hashMap.put("USER_SIGNIN", curDate)
            hashMap.put("banUser", false) //user if true then cant be login user
            hashMap.put("refered", false)
            hashMap.put("total_reffer", "0")
            hashMap.put("id", sb.toString())
            FirebaseDatabase.getInstance().getReference("user").child(firebaseUser.uid.toString()).setValue(hashMap)
            val i = Intent(this, LoanOfferActivity::class.java)
            startActivity(i)
            finish()
        } else {
            binding.linearButton.visibility = View.VISIBLE
            binding.linearButton2.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }
}
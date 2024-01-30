package com.deepakdevloper.loanapp.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.deepakdevloper.loanapp.databinding.ActivityEmailVerifyBinding

class EmailVerifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmailVerifyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGetStarted.setOnClickListener {}
    }
}
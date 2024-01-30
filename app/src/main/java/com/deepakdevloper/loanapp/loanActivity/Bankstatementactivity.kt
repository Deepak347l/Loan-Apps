package com.deepakdevloper.loanapp.loanActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.deepakdevloper.loanapp.databinding.ActivityBankstatementactivityBinding

class Bankstatementactivity : AppCompatActivity() {
    private lateinit var binding:ActivityBankstatementactivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBankstatementactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
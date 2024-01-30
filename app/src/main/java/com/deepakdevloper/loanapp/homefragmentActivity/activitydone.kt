package com.deepakdevloper.loanapp.homefragmentActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.deepakdevloper.loanapp.databinding.ActivityActivitydoneBinding

class activitydone : AppCompatActivity() {
    private lateinit var binding:ActivityActivitydoneBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActivitydoneBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
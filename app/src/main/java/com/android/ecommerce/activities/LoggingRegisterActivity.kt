package com.android.ecommerce.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.ecommerce.databinding.ActivityLoggingRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoggingRegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoggingRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoggingRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}
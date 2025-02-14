package com.android.ecommerce.activities

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.ecommerce.databinding.ActivityLoggingRegisterBinding
import com.android.ecommerce.util.NetworkReceiver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoggingRegisterActivity : AppCompatActivity() {
    private lateinit var networkReceiver: NetworkReceiver

    private lateinit var binding : ActivityLoggingRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoggingRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        networkReceiver = NetworkReceiver(this)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkReceiver)
    }

}
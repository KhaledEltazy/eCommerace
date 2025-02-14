package com.android.ecommerce.activities

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.android.ecommerce.R
import com.android.ecommerce.databinding.ActivityShoppingBinding
import com.android.ecommerce.util.NetworkReceiver
import com.android.ecommerce.util.Resource
import com.android.ecommerce.viewmodel.cart_biling_adress_all_orders_viewmodels.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {
    private lateinit var binding : ActivityShoppingBinding
    lateinit var navHostFragment: NavHostFragment
    private lateinit var networkReceiver: NetworkReceiver

    val viewModel by viewModels<CartViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerShopping) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)

        lifecycleScope.launch {
            viewModel.cartProducts.collect{
                when(it){
                    is Resource.Success ->{
                        val count = it.data?.size ?:  0
                        binding.bottomNav.getOrCreateBadge(R.id.cartFragment).apply {
                            number = count
                            backgroundColor = resources.getColor(R.color.g_blue)
                        }
                    }
                    else -> Unit
                }
            }
        }

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
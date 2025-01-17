package com.android.ecommerce.util

import android.view.View
import androidx.fragment.app.Fragment
import com.android.ecommerce.R
import com.android.ecommerce.activities.ShoppingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.showingBottomNavView(){
    val bottomNavigationView = (activity as ShoppingActivity)
        .findViewById<BottomNavigationView>(R.id.bottomNav)
    bottomNavigationView.visibility = View.VISIBLE
}

fun Fragment.hidingBottomNavView(){
    val bottomNavigationView = (activity as ShoppingActivity)
        .findViewById<BottomNavigationView>(R.id.bottomNav)
    bottomNavigationView.visibility = View.GONE
}
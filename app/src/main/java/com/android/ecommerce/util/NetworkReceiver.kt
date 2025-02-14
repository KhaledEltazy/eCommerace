package com.android.ecommerce.util

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog

class NetworkReceiver(private val activity: Activity) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (!NetworkUtils.isInternetAvailable(context!!)) {
            showNoInternetDialog()
        }
    }

    private fun showNoInternetDialog() {
        AlertDialog.Builder(activity)
            .setTitle("No Internet Connection")
            .setMessage("Please check your internet connection and try again.")
            .setPositiveButton("Exit") { _, _ ->
                activity.finishAffinity() // Closes all activities and exits the app
            }
            .setCancelable(false)
            .show()
    }
}
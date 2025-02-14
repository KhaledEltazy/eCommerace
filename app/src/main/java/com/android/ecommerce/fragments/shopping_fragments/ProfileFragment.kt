package com.android.ecommerce.fragments.shopping_fragments

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.ecommerce.R
import com.android.ecommerce.activities.LoggingRegisterActivity
import com.android.ecommerce.databinding.FragmentProfileBinding
import com.android.ecommerce.util.Constants.REQUEST_NOTIFICATION_PERMISSION
import com.android.ecommerce.util.Resource
import com.android.ecommerce.util.showingBottomNavView
import com.android.ecommerce.viewmodel.profile_settings_viewmodel.ProfileSettingViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private val viewModel by viewModels<ProfileSettingViewModel>()

    @Inject
    lateinit var sharedPreferences: SharedPreferences // Inject SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeNotificationState()

        binding.apply {

            // setup navigation of Name clickListener
            nameProfileFragmentLayout.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
            }

            // setup navigation of allOrders clickListener
            allOrdersLayout.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_allOrdersFragment)
            }

            // setup navigation of TrackOrder clickListener
            billingLayout.setOnClickListener {
                val action = ProfileFragmentDirections.actionProfileFragmentToBillingFragment(0f,
                    emptyArray(),false
                )
                findNavController().navigate(action)
            }

            // setup navigation of logOut clickListener
            logOutLayout.setOnClickListener {
                viewModel.logout()
                val intent = Intent(requireActivity(),LoggingRegisterActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }

            //setup navigation to addresses list
            addressLayout.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_addressesListFragment)
            }

            // Handle notification switch toggle
            switchNotification.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Check if the notification permission is granted
                    if (isNotificationPermissionGranted()) {
                        viewModel.setNotificationPreference(true)
                    } else {
                        // Request permission if not granted
                        requestNotificationPermission()
                    }
                } else {
                    viewModel.setNotificationPreference(false)
                }
            }
        }

       lifecycleScope.launch {
            viewModel.user.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.progressbarSettings.visibility = View.VISIBLE
                    }
                    is Resource.Success ->{
                        binding.progressbarSettings.visibility = View.GONE
                       Glide.with(requireView()).load(it.data!!.imagePath).error(resources.getDrawable(R.drawable.baseline_person_24))
                          .into(binding.imageNameProfileFragment)
                        binding.nameTvProfileFragment.text = "${it.data!!.firstName} ${it.data!!.lastName}"
                    }
                    is Resource.Error ->{
                        binding.progressbarSettings.visibility = View.GONE
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun observeNotificationState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isNotificationsEnabled.collectLatest { isEnabled ->
                binding.switchNotification.isChecked = isEnabled
            }
        }
    }

    private fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // On Android 13 and above, check notification permission
            requireContext().checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            // For below Android 13, permissions are granted automatically
            true
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_NOTIFICATION_PERMISSION
            )
        }
    }



    //return bottomNavView in navigateUP from another fragment
    override fun onResume() {
        super.onResume()
        showingBottomNavView()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_NOTIFICATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, enable notifications
                    viewModel.setNotificationPreference(true)
                } else {
                    // Permission denied, show a toast or take appropriate action
                    Toast.makeText(context, "Notification permission is required to enable notifications.", Toast.LENGTH_SHORT).show()
                    binding.switchNotification.isChecked = false
                }
            }
        }
    }
}
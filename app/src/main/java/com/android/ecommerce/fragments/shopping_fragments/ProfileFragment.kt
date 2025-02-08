package com.android.ecommerce.fragments.shopping_fragments

import android.content.Intent
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
import com.android.ecommerce.util.Resource
import com.android.ecommerce.util.showingBottomNavView
import com.android.ecommerce.viewmodel.profile_settings_viewmodel.ProfileSettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private val viewModel by viewModels<ProfileSettingViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        }

       lifecycleScope.launch {
            viewModel.user.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.progressbarSettings.visibility = View.VISIBLE
                    }
                    is Resource.Success ->{
                        binding.progressbarSettings.visibility = View.GONE
                       // Glide.with(requireView()).load(it.data!!.imagePath).error(ColorDrawable(Color.BLACK))
                       //     .into(binding.imageNameProfileFragment)
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

    override fun onResume() {
        super.onResume()

        showingBottomNavView()
    }
}
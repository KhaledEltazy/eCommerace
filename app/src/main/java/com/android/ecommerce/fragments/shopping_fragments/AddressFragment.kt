package com.android.ecommerce.fragments.shopping_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.navigateUp
import com.android.ecommerce.data.Address
import com.android.ecommerce.databinding.FragmentAddressBinding
import com.android.ecommerce.util.Resource
import com.android.ecommerce.util.hidingBottomNavView
import com.android.ecommerce.viewmodel.cart_biling_adress_all_orders_viewmodels.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AddressFragment : Fragment() {
    private lateinit var binding : FragmentAddressBinding
    private val viewModel by viewModels<AddressViewModel>()
    private val arg by navArgs<AddressFragmentArgs>()
        override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            hidingBottomNavView()
            binding = FragmentAddressBinding.inflate(inflater)
            return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val address = arg.address
        if (address == null){
            binding.deleteBtnAddressFragment.visibility = View.GONE
        } else {
            binding.apply {
                addressName.setText(address.addressTitle)
                fullNameETAddressFragment.setText(address.fullName)
                stateETAddressFragment.setText(address.state)
                phoneETAddressFragment.setText(address.phone)
                cityETAddressFragment.setText(address.city)
                streetETAddressFragment.setText(address.street)
            }
        }

        binding.apply {
            saveBtnddressFragment.setOnClickListener {
                val addressTitle = addressName.text.toString()
                val fullName = fullNameETAddressFragment.text.toString()
                val phone = phoneETAddressFragment.text.toString()
                val street = streetETAddressFragment.text.toString()
                val state = stateETAddressFragment.text.toString()
                val city = cityETAddressFragment.text.toString()

                val address = Address(addressTitle,fullName,phone,street,state,city)

                viewModel.addAddress(address)
            }

            binding.closeIconAddressFragment.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        lifecycleScope.launch {
            viewModel.addAddress.collect{
                when(it){
                    is Resource.Loading ->{
                        binding.saveBtnddressFragment.startAnimation()
                    }
                    is Resource.Success ->{
                        binding.saveBtnddressFragment.revertAnimation()
                    }
                    is Resource.Error ->{
                        binding.saveBtnddressFragment.revertAnimation()
                        Toast.makeText(requireContext(),it.message.toString(),Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            viewModel.error.collect{
                Toast.makeText(requireContext(),it,Toast.LENGTH_LONG).show()
            }
        }


    }
}
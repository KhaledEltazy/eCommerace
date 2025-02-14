package com.android.ecommerce.fragments.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.ecommerce.R
import com.android.ecommerce.adapters.AddressListAdapter
import com.android.ecommerce.databinding.FragmentAddressesListBinding
import com.android.ecommerce.util.Resource
import com.android.ecommerce.util.VerticalItemDecoration
import com.android.ecommerce.util.hidingBottomNavView
import com.android.ecommerce.viewmodel.cart_biling_adress_all_orders_viewmodels.BillingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddressesListFragment : Fragment() {
    private lateinit var binding : FragmentAddressesListBinding
    private val viewModel by viewModels<BillingViewModel>()
    private val addressListAdapter by lazy {
        AddressListAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hidingBottomNavView()
        binding = FragmentAddressesListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAddressListRv()


        //handling add new address button
        binding.addNewAddressCardView.setOnClickListener {
            findNavController().navigate(R.id.action_addressesListFragment_to_addressFragment)
        }

        //handle close icon
        binding.closeIconAddressFragment.setOnClickListener {
            findNavController().navigateUp()
        }

        addressListAdapter.onAddressClicked = {
            val bundle = Bundle().apply {
                putParcelable("address",it)
            }
            findNavController().navigate(R.id.action_addressesListFragment_to_addressFragment,bundle)
        }


        lifecycleScope.launch {
            viewModel.getAddress.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.addressListProgressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.addressListProgressBar.visibility = View.GONE
                        addressListAdapter.differ.submitList(it.data)
                    }
                    is Resource.Error ->{
                        binding.addressListProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(),it.message.toString(), Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

    }

    private fun setAddressListRv() {
        binding.addressesRv.apply {
            adapter = addressListAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            addItemDecoration(VerticalItemDecoration())
        }
    }
}
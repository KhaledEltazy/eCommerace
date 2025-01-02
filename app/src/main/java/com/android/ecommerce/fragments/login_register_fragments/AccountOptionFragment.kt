package com.android.ecommerce.fragments.login_register_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.android.ecommerce.R
import com.android.ecommerce.databinding.FragmentAccountOptionBinding

class AccountOptionFragment : Fragment() {
    private lateinit var binding: FragmentAccountOptionBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountOptionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            //handling login button to navigate login fragment
            btnLogin.setOnClickListener {
                findNavController().navigate(R.id.action_accountOptionFragment_to_logInFragment)
            }

            //handling register button to navigate register fragment
            btnRegister.setOnClickListener {
                findNavController().navigate(R.id.action_accountOptionFragment_to_registerFragment)
            }
        }
    }
}
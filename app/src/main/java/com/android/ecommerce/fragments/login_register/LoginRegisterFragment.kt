package com.android.ecommerce.fragments.login_register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.ecommerce.databinding.FragmentLoginRegisterBinding


class LoginRegisterFragment : Fragment() {
    private lateinit var binding: FragmentLoginRegisterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginRegisterBinding.inflate(inflater,container,false)
        return binding.root
    }
}
package com.android.ecommerce.fragments.login_register

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.ecommerce.data.User
import com.android.ecommerce.databinding.FragmentRegisterBinding
import com.android.ecommerce.util.Resource
import com.android.ecommerce.viewmodel.RegisterViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


private val TAG = "RegisterFragment"

@AndroidEntryPoint
class RegisterFragment : Fragment() {
   private lateinit var binding: FragmentRegisterBinding
   private val viewmodel : RegisterViewmodel by viewModels<RegisterViewmodel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //handling register button and create an account
        binding.apply {
            btnRegister.setOnClickListener {
                if (etFirstName.text.toString() != "" &&
                    etLastName.text.toString() != "" &&
                    etEmail.text.toString() != "" &&
                    etPassword.text.toString() != ""
                ) {
                    val user = User(
                        etFirstName.text.toString().trim(),
                        etLastName.text.toString().trim(),
                        etEmail.text.toString().trim()
                    )

                    val password = etPassword.text.toString()
                    viewmodel.createAccountWithEmailAndPassword(user, password)
                } else {
                    Toast.makeText(requireContext(),"Please fill missing field",Toast.LENGTH_LONG).show()
                }
            }
        }

        //state of register button
        lifecycleScope.launch {
                viewmodel.register.collect {
                    when (it) {
                        is Resource.Loading -> {
                            binding.btnRegister.startAnimation()}

                        is Resource.Error -> {
                            Log.d(TAG, "Error: ${it.message}")
                            binding.btnRegister.revertAnimation()
                        }
                        is Resource.Success -> {
                            Log.d(TAG, "Success: ${it.data}")
                            binding.btnRegister.revertAnimation()
                            Toast.makeText(requireContext(),"Register Successfully",Toast.LENGTH_LONG).show()
                        }
                        else -> Unit
                    }
                }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.btnRegister.clearAnimation()
    }
}
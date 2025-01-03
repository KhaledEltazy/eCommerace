package com.android.ecommerce.fragments.login_register_fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.ecommerce.R
import com.android.ecommerce.data.User
import com.android.ecommerce.databinding.FragmentRegisterBinding
import com.android.ecommerce.util.RegisterValidation
import com.android.ecommerce.util.Resource
import com.android.ecommerce.viewmodel.RegisterViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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


        binding.apply {
            //handling register button and create an account
            btnRegister.setOnClickListener {
                if (etFirstName.text.toString() != "" &&
                    etLastName.text.toString() != ""
                ) {
                    val user = User(
                        etFirstName.text.toString().trim(),
                        etLastName.text.toString().trim(),
                        etEmail.text.toString().trim()
                    )

                    val password = etPassword.text.toString()
                    viewmodel.createAccountWithEmailAndPassword(user, password)
                } else {
                    Toast.makeText(requireContext(),"Please fill missing fields",Toast.LENGTH_LONG).show()
                }
            }

            //handling login text
            tvLogin.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_logInFragment)
            }
        }

        //state of register button
        //collect register value
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

        //collect validation email and password
        lifecycleScope.launch {
            viewmodel.validation.collect{ valition ->
                if (valition.email is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.etEmail.apply {
                            requestFocus()
                            error = valition.email.message
                        }
                    }
                }
                if ( valition.password is RegisterValidation.Failed){
                        withContext(Dispatchers.Main){
                            binding.etPassword.apply {
                                requestFocus()
                                error = valition.password.message
                            }
                        }
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.btnRegister.clearAnimation()
    }
}
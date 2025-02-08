package com.android.ecommerce.fragments.login_register_fragments

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
import com.android.ecommerce.activities.ShoppingActivity
import com.android.ecommerce.databinding.FragmentLogInBinding
import com.android.ecommerce.dialog.setupBottomSheetDialog
import com.android.ecommerce.util.Resource
import com.android.ecommerce.viewmodel.login_registration_viewmodels.LoginViewmodel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LogInFragment : Fragment() {
   private lateinit var binding : FragmentLogInBinding
   private val viewmodel : LoginViewmodel by viewModels<LoginViewmodel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLogInBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.apply {
            //handling login button and login function
            btnLogin.setOnClickListener {
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString()
                if(email.isNotEmpty() && password.isNotEmpty()){
                    viewmodel.loggingAccount(email,password)
                } else {
                    Toast.makeText(requireContext(),"Fill the missing fields",Toast.LENGTH_LONG).show()
                }
            }

            //handling register text
            tvRegister.setOnClickListener {
                findNavController().navigate(R.id.action_logInFragment_to_registerFragment)
            }

            //handling click on ForgotPassword
            tvForgotPassword.setOnClickListener {
                setupBottomSheetDialog {email->
                    viewmodel.resetPassword(email)
                }
            }
        }

        //checking the value of ResetPassword
        lifecycleScope.launch {
            viewmodel.resetPassword.collect{
                when (it){
                    is Resource.Loading ->{

                    }
                    is Resource.Success ->{
                       Snackbar.make(requireView(),"Reset link was sent to yor email", Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Error ->{
                        Snackbar.make(requireView(),"Error: ${it.message}", Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        //checking the value of login
        lifecycleScope.launch {
            viewmodel.login.collect{
                when (it){
                    is Resource.Loading ->{
                        binding.btnLogin.startAnimation()
                    }
                    is Resource.Success ->{
                        binding.btnLogin.revertAnimation()
                        Intent(requireActivity(),ShoppingActivity::class.java).also {
                            intent -> intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    is Resource.Error ->{
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                        binding.btnLogin.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        binding.btnLogin.clearAnimation()
    }
}
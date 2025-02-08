package com.android.ecommerce.fragments.login_register_fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.ecommerce.R
import com.android.ecommerce.activities.ShoppingActivity
import com.android.ecommerce.data.User
import com.android.ecommerce.databinding.FragmentRegisterBinding
import com.android.ecommerce.util.RegisterValidation
import com.android.ecommerce.util.Resource
import com.android.ecommerce.viewmodel.login_registration_viewmodels.RegisterViewmodel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private val TAG = "RegisterFragment"

@AndroidEntryPoint
class RegisterFragment : Fragment() {
   private lateinit var binding: FragmentRegisterBinding
   private val viewmodel : RegisterViewmodel by viewModels<RegisterViewmodel>()
    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    account?.idToken?.let { idToken ->
                        viewmodel.registerByGoogleAccount(idToken)
                    }
                } catch (e: ApiException) {
                    Log.e(TAG, "Google Sign-In failed: ${e.message}")
                    Toast.makeText(requireContext(), "Google Sign-In Failed", Toast.LENGTH_LONG).show()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(),gso)
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

            //handling google button
            btnGoogle.setOnClickListener {
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            }
        }

        //state of register button
        //collect register value
        lifecycleScope.launch {
            viewmodel.register.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnRegister.startAnimation()
                    }
                    is Resource.Error -> {
                        Log.d(TAG, "Error: ${it.message}")
                        binding.btnRegister.revertAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    is Resource.Success -> {
                        Log.d(TAG, "Success: ${it.data}")
                        binding.btnRegister.revertAnimation()
                        Toast.makeText(requireContext(), "Register Successfully", Toast.LENGTH_LONG).show()
                        Intent(requireActivity(), ShoppingActivity::class.java).also {
                                intent -> intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
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
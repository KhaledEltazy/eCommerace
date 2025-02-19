package com.android.ecommerce.fragments.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.ecommerce.R
import com.android.ecommerce.data.User
import com.android.ecommerce.databinding.FragmentUserAccountBinding
import com.android.ecommerce.dialog.setupBottomSheetDialog
import com.android.ecommerce.util.Resource
import com.android.ecommerce.util.hidingBottomNavView
import com.android.ecommerce.viewmodel.profile_settings_viewmodel.UserAccountViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserAccountFragment :Fragment() {
    private lateinit var binding : FragmentUserAccountBinding
    private val viewModel by viewModels<UserAccountViewModel>()
    private var imageUri : Uri? = null
    private lateinit var imageActivityResultLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                imageUri = it.data?.data
                Glide.with(this).load(imageUri).into(binding.imageUser)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hidingBottomNavView()
        binding = FragmentUserAccountBinding.inflate(inflater)
        return binding.root
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //handle save button
        binding.buttonSave.setOnClickListener {
            binding.apply {
                val firstName = edFirstName.text.trim().toString()
                val lastName = edLastName.text.trim().toString()
                val email = edEmail.text.trim().toString()
                val user = User(firstName, lastName, email)
                viewModel.updateUser(user, imageUri)
            }
        }

        binding.imageEdit.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            imageActivityResultLauncher.launch(intent)
        }

        binding.tvUpdatePassword.setOnClickListener {
            setupBottomSheetDialog {

            }
        }

        //hande close icon
        binding.imageCloseUserAccount.setOnClickListener {
            findNavController().navigateUp()
        }

        lifecycleScope.launch {
            viewModel.user.collect{
                when(it){
                    is Resource.Loading ->{
                        showUserLoading()
                    }
                    is Resource.Success->{
                        hideUserLoading()
                        showUserInformation(it.data!!)
                    }
                    is Resource.Error->{
                        hideUserLoading()
                        Toast.makeText(requireContext(),it.message, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launch {
            viewModel.updateInfo.collectLatest{
                when(it){
                    is Resource.Loading->{
                        binding.buttonSave.startAnimation()
                    }
                    is Resource.Success->{
                        binding.buttonSave.revertAnimation()
                        findNavController().navigateUp()
                    }
                    is Resource.Error->{
                        binding.buttonSave.revertAnimation()
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()

                    }
                    else -> Unit
                }
            }
        }


    }

    private fun showUserInformation(user: User) {
        binding.apply {
            edFirstName.setText(user.firstName)
            edLastName.setText(user.lastName)
            edEmail.setText(user.email)
            Glide.with(this@UserAccountFragment).load(user.imagePath).error(resources.getDrawable(R.drawable.baseline_person_24))
                .into(imageUser)
        }
    }

    private fun showUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.VISIBLE
            imageUser.visibility = View.INVISIBLE
            imageEdit.visibility = View.INVISIBLE
            edFirstName.visibility = View.INVISIBLE
            edLastName.visibility = View.INVISIBLE
            edEmail.visibility = View.INVISIBLE
            tvUpdatePassword.visibility = View.INVISIBLE
            buttonSave.visibility = View.INVISIBLE
        }
    }

    private fun hideUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.GONE
            imageUser.visibility = View.VISIBLE
            imageEdit.visibility = View.VISIBLE
            edFirstName.visibility = View.VISIBLE
            edLastName.visibility = View.VISIBLE
            edEmail.visibility = View.VISIBLE
            tvUpdatePassword.visibility = View.VISIBLE
            buttonSave.visibility = View.VISIBLE
        }
    }
}
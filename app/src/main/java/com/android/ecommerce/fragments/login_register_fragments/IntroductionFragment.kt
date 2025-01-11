package com.android.ecommerce.fragments.login_register_fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.ecommerce.R
import com.android.ecommerce.activities.ShoppingActivity
import com.android.ecommerce.databinding.FragmentIntroductionBinding
import com.android.ecommerce.util.Constants.ACCOUNT_OPTION_FRAGMENT
import com.android.ecommerce.util.Constants.SHOPPING_ACTIVITY
import com.android.ecommerce.viewmodel.IntroductionViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IntroductionFragment : Fragment() {

    private lateinit var binding: FragmentIntroductionBinding
    private  val viewmodel by viewModels<IntroductionViewmodel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntroductionBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //handling start button
        binding.apply {
            btnStart.setOnClickListener {
                viewmodel.startButtonClicked()
                findNavController().navigate(R.id.action_introductionFragment_to_accountOptionFragment)
            }
        }

        lifecycleScope.launch {
            viewmodel.navigate.collect{
                when(it){
                    SHOPPING_ACTIVITY -> {
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    ACCOUNT_OPTION_FRAGMENT ->{
                        findNavController().navigate(R.id.action_introductionFragment_to_accountOptionFragment)
                    }

                    else -> Unit
                }
            }
        }
    }
}
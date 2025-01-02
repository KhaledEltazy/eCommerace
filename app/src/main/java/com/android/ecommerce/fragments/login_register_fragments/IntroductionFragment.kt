package com.android.ecommerce.fragments.login_register_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.android.ecommerce.R
import com.android.ecommerce.databinding.FragmentIntroductionBinding

class IntroductionFragment : Fragment() {

    private lateinit var binding: FragmentIntroductionBinding

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
                findNavController().navigate(R.id.action_introductionFragment_to_accountOptionFragment)
            }
        }
    }
}
package com.android.ecommerce.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.ecommerce.R
import com.android.ecommerce.databinding.FragmentBaseCategoryBinding

open class BaseCategoryFragment : Fragment(R.layout.fragment_base_category) {
    private lateinit var binding : FragmentBaseCategoryBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
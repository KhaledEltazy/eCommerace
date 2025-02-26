package com.android.ecommerce.fragments.shopping_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.ecommerce.R
import com.android.ecommerce.adapters.HomeViewPagerAdapter
import com.android.ecommerce.databinding.FragmentHomeShoppingBinding
import com.android.ecommerce.fragments.categories.AccessoryFragment
import com.android.ecommerce.fragments.categories.ChairFragment
import com.android.ecommerce.fragments.categories.CupboardFragment
import com.android.ecommerce.fragments.categories.FurnitureFragment
import com.android.ecommerce.fragments.categories.MainCategoryFragment
import com.android.ecommerce.fragments.categories.TableFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeShoppingFragment : Fragment() {
   private lateinit var binding : FragmentHomeShoppingBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeShoppingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragments = arrayListOf<Fragment>(
            MainCategoryFragment(),
            ChairFragment(),
            CupboardFragment(),
            TableFragment(),
            AccessoryFragment(),
            FurnitureFragment()
        )

        binding.viewPager.isUserInputEnabled = false

        val viewPager2Adapter = HomeViewPagerAdapter(categoriesFragments,childFragmentManager,lifecycle)
        binding.viewPager.adapter = viewPager2Adapter
        TabLayoutMediator(binding.tabLayout,binding.viewPager){tab, position ->
            when (position) {
                0 -> tab.text = context?.getString(R.string.tab_home)
                1 -> tab.text = context?.getString(R.string.tab_chair)
                2 -> tab.text = context?.getString(R.string.tab_cupboard)
                3 -> tab.text = context?.getString(R.string.tab_table)
                4 -> tab.text = context?.getString(R.string.tab_accessory)
                5 -> tab.text = context?.getString(R.string.tab_furniture)
            }

        }.attach()
    }
}
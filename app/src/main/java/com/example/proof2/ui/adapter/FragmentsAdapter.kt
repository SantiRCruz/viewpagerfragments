package com.example.proof2.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.proof2.ui.HomeFragment
import com.example.proof2.ui.MapFragment
import com.example.proof2.ui.QrFragment

class FragmentsAdapter(fragmentManager: FragmentManager,lifecycle: Lifecycle):FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> HomeFragment()
            1-> QrFragment()
            2-> MapFragment()
            else-> HomeFragment()
        }
    }
}
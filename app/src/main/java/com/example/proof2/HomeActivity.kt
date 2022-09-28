package com.example.proof2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.proof2.databinding.ActivityHomeBinding
import com.example.proof2.ui.adapter.FragmentsAdapter
import com.google.android.material.tabs.TabLayoutMediator

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()

        binding.vpFragments.adapter = FragmentsAdapter(supportFragmentManager,lifecycle)
        TabLayoutMediator(binding.tabL,binding.vpFragments){t,p->
            when(p){
                0->t.text = "Home"
                1->t.text = "Qr"
                2->t.text = "Map"
            }
        }.attach()
    }
}
package com.rahman.pemiluapp.view.ui.entryData

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.rahman.pemiluapp.R
import com.rahman.pemiluapp.databinding.ActivityFormEntryBinding

class FormEntryActivity : AppCompatActivity() {
    private var _binding: ActivityFormEntryBinding? = null
    private val binding get() = _binding!!

    private val onBackCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            backNavigation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityFormEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        onBackPressedDispatcher.addCallback(this, onBackCallback)

        binding.appBar.setNavigationOnClickListener { backNavigation() }
    }

    private fun backNavigation() {
        val navController = findNavController(R.id.nav_enter_fragment)

        if (navController.currentDestination?.id == R.id.nikFragment) finish()
        else navController.popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
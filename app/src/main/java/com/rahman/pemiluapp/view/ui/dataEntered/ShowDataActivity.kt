package com.rahman.pemiluapp.view.ui.dataEntered

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import com.rahman.pemiluapp.R
import com.rahman.pemiluapp.databinding.ActivityShowDataBinding
import com.rahman.pemiluapp.utils.DisplayMessage.showToast
import com.rahman.pemiluapp.utils.Response
import com.rahman.pemiluapp.view.viewmodel.ViewModelFactory
import com.rahman.pemiluapp.view.viewmodel.ShowDataViewModel

class ShowDataActivity : AppCompatActivity() {
    private var _binding: ActivityShowDataBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShowDataViewModel by viewModels { ViewModelFactory.getInstance(this) }
    private val onBackCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            backNavigation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityShowDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        onBackPressedDispatcher.addCallback(this, onBackCallback)

//        todo: print data to table.pdf ???
        handleVoterResponse()

        binding.appBar.setNavigationOnClickListener { backNavigation() }
    }

    private fun handleVoterResponse() {
        viewModel.getAllVoters { result ->
            when (result) {
                is Response.Success -> loadingState(false, true)
                is Response.Failure -> loadingState(false)
                is Response.Error -> {
                    loadingState(false)
                    if (!result.message.isNullOrEmpty()) showToast(this, result.message)
                }

                is Response.Loading -> loadingState(true)
            }
        }
    }

    private fun loadingState(isVisible: Boolean, fullyGone: Boolean = false) {
        binding.loadingIndicator.isVisible = if (fullyGone) false else isVisible
        binding.rvKosong.isVisible = if (fullyGone) false else !isVisible
    }

    private fun backNavigation() {
        val navController = findNavController(R.id.nav_show_fragment)

        if (navController.currentDestination?.id == R.id.votersDataListFragment) finish()
        else navController.popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
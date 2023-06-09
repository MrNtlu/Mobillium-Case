package com.example.mobilliumcase.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation

abstract class BaseFragment<T>: Fragment() {
    protected var _binding: T? = null
    protected val binding get() = _binding!!

    protected lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    // To prevent memory leak
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
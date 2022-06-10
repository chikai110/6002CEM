package com.example.a6002cem

import androidx.fragment.app.Fragment

interface FragmentNavigation {
    // Use to navigate fragment
    fun navigateFrag(fragment: Fragment, addToStack: Boolean)
}
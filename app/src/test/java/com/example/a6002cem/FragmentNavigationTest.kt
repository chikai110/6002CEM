package com.example.a6002cem

import org.junit.Assert
import org.junit.Test

class FragmentNavigationTest {
    @Test
    fun navigateFrag() {
        var result = HomeFragment()
        Assert.assertEquals(result, false)
    }
}
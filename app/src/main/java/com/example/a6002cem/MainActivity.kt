package com.example.a6002cem

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.ConfigurationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*

class MainActivity : AppCompatActivity(), FragmentNavigation{

    companion object {
        const val LANG_KEY = "LANG_KEY"
        const val CURRENT_LOCATION = "CURRENT_LOCATION"
        const val USER_ID = "USER_ID"
    }
    private lateinit var fAuth: FirebaseAuth
    private var sharedPreferences: SharedPreferences? = null
    private var currentLang: String? = null
    lateinit var locale: Locale
    lateinit var bottomNav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.bottom_nav)
        // sets background color for the whole bar
        navView.setBackgroundColor(ContextCompat.getColor(this, R.color.black))

        // Using sharedPreferences
        sharedPreferences = getSharedPreferences("SharedPreMain", MODE_PRIVATE)
        fAuth = Firebase.auth

        // Load Home Fragment By default
        navigateFrag(HomeFragment(), true)

        // Setup Bottom Nav Bar
        bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener{
            when (it.itemId) {
                R.id.homeFragment->{
                    navigateFrag(HomeFragment(), true)
                    return@setOnItemSelectedListener true
                }
                R.id.mapFragment -> {
                    navigateFrag(LocationFragment(), true)
                    return@setOnItemSelectedListener true
                }
                R.id.qrcodeFragment -> {
                    navigateFrag(QRCodeTicketFragment(), true)
                    return@setOnItemSelectedListener true
                }
                R.id.profileFragment -> {
                    val currentUser = fAuth.currentUser
                    if(currentUser != null){
                        navigateFrag(ProfileFragment(), true)
                    } else {
                        navigateFrag(LoginFragment(), false)
                    }
                    return@setOnItemSelectedListener true
                }
                // Call change Language Function
                R.id.languageFragment -> {
                    var localeName = sharedPreferences!!.getString(LANG_KEY, "en")
                    if (localeName == "en") {
                        setLocale("ja")
                        saveLocale("ja")
                    }
                    else {
                        setLocale("en")
                        saveLocale("en")
                    }
                }
            }
            false
        }
    }

    // Change Language
    private fun setLocale(localeName: String) {
        if (localeName !== currentLang) {
            locale = Locale(localeName)
            val dm = resources.displayMetrics
            val configuration = resources.configuration
            configuration.setLocale(locale)
            resources.updateConfiguration(configuration, dm)
            val refresh = Intent(
                this,
                MainActivity::class.java
            )
            refresh.putExtra(currentLang, localeName)
            // Restart activity
            startActivity(refresh)
        }
    }

    // Save the locale to sharedPreference
    private fun saveLocale(localeName: String) {
        val editor = sharedPreferences!!.edit()
        editor.putString(LANG_KEY, localeName)
        editor.commit()
    }

    // Navigate Fragment Function
    override fun navigateFrag(fragment: Fragment, addToStack: Boolean) {
        val transaction = supportFragmentManager
            .beginTransaction()
            .replace(R.id.container,fragment)

        if(addToStack){
            transaction.addToBackStack(null)
        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
    }
}
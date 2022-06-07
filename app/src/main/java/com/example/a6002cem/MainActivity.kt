package com.example.a6002cem

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.ConfigurationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*

class MainActivity : AppCompatActivity(), FragmentNavigation{
    private lateinit var fAuth: FirebaseAuth
    lateinit var bottomNav : BottomNavigationView

    companion object {
        const val LANG_KEY = "LANG_KEY"
    }
    private var editTextName: EditText? = null
    private var sharedPreferences: SharedPreferences? = null
    lateinit var locale: Locale
    private var currentLang: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextName = findViewById<View>(R.id.nameText) as EditText

        sharedPreferences = getSharedPreferences("SharedPreMain", MODE_PRIVATE)
        if (sharedPreferences!!.contains(LANG_KEY)) {
            editTextName!!.setText(sharedPreferences!!.getString(LANG_KEY, "en"))
        }

        fAuth = Firebase.auth

        val currentUser = fAuth.currentUser
        if(currentUser != null){
            supportFragmentManager.beginTransaction()
                .add(R.id.container, HomeFragment()).addToBackStack(null)
                .commit()
        }else {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, LoginFragment())
                .commit()
        }
        bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener{
            when (it.itemId) {
                R.id.homeFragment->{
                    loadFragment(HomeFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.cartFragment -> {
                    loadFragment(ItemDetailsFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.accountFragment -> {
                    loadFragment(ProfileFragment())
//                    save()
//                    sharedPreferences!!.getString(LANG_KEY, "en")?.let { it1 -> setLocale(it1) }
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }


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
            startActivity(refresh)
        }
    }

    private fun save() {
        val editor = sharedPreferences!!.edit()
        editor.putString(LANG_KEY, editTextName!!.text.toString())
        editor.commit()
    }

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

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
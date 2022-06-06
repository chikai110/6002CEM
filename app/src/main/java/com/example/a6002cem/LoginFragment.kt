package com.example.a6002cem

import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.Executor

class LoginFragment : Fragment() {
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var fAuth: FirebaseAuth

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreateView (
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_login, container, false)

        username = view.findViewById(R.id.log_username)
        password = view.findViewById(R.id.log_password)
        fAuth = Firebase.auth

        view.findViewById<Button>(R.id.btn_register).setOnClickListener {
            var navRegister = activity as FragmentNavigation
            navRegister.navigateFrag(RegisterFragment(),false)
        }

        view.findViewById<Button>(R.id.btn_login).setOnClickListener {
            validateForm()
        }

        executor = ContextCompat.getMainExecutor(requireContext())

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(requireContext(),
                        "Authentication error: $errString", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    var navHome = activity as FragmentNavigation
                    navHome.navigateFrag(HomeFragment(),addToStack = true)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(requireContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show()
                }

            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()

        val biometricLoginButton =
            view.findViewById<Button>(R.id.biometric_login)
        biometricLoginButton.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }

        val biometricStatusTextView =
            view.findViewById<TextView>(R.id.biometric_status)
        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                biometricStatusTextView.text = "App can authenticate using biometrics."
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                biometricStatusTextView.text = "No biometric features available on this device."
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                biometricStatusTextView.text = "Biometric features are currently unavailable."
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                // Prompts the user to create credentials that your app accepts.
                biometricStatusTextView.text = "Biometric features are not enrolled."
        }

        return view
    }

    private fun firebaseSignIn() {
        val btnLogin = view?.findViewById(R.id.btn_login) as Button
        btnLogin.isEnabled = false
        btnLogin.alpha = 0.5f
        fAuth.signInWithEmailAndPassword(username.text.toString(),
            password.text.toString()).addOnCompleteListener{
                task ->
            if(task.isSuccessful){
                var navHome = activity as FragmentNavigation
                navHome.navigateFrag(HomeFragment(),addToStack = true)
            }else{
                btnLogin.isEnabled = true
                btnLogin.alpha = 1.0f
                Toast.makeText(context,task.exception?.message,Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateForm(){
        val icon = AppCompatResources.getDrawable(requireContext(),
            R.drawable.ic_warning)

        icon?.setBounds(0,0,icon.intrinsicWidth,icon.intrinsicHeight)
        when {
            TextUtils.isEmpty(username.text.toString().trim()) -> {
                username.setError("Please Enter Username", icon)
            }
            TextUtils.isEmpty(password.text.toString().trim()) -> {
                password.setError("Please Enter Password", icon)
            }

            username.text.toString().isNotEmpty() &&
                    password.text.toString().isNotEmpty() -> {

                if (username.text.toString().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {

                    firebaseSignIn()
                } else {
                    username.setError("Please Enter Valid Email Id", icon)
                }
            }
        }
    }
}
package com.example.firebasegroupapp7

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.firebasegroupapp7.databinding.SignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: SignInBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var createAccountButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        email = findViewById(R.id.emailText)
        password = findViewById(R.id.passwordText)
        loginButton = findViewById(R.id.login)
        createAccountButton = findViewById(R.id.createAccount)

        loginButton.setOnClickListener {
            val emailInput = email.text.toString().trim()
            val passwordInput = password.text.toString().trim()

            if (isLoginFormValid(emailInput, passwordInput)) {

                auth.signInWithEmailAndPassword(emailInput, passwordInput)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(
                                baseContext,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
            }
        }

        createAccountButton.setOnClickListener {
            intent = Intent(this, CreateUserPersonalInfoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isLoginFormValid(emailInput: String, passwordInput: String): Boolean {
        if (emailInput.isEmpty()) {
            email.error = "Email is required"
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            email.error = "Enter a valid email"
            return false
        }

        if (passwordInput.isEmpty()) {
            password.error = "Password is required"
            return false
        }

        if (passwordInput.length < 6) {
            password.error = "Password must be at least 6 characters"
            return false
        }

        return true
    }

}
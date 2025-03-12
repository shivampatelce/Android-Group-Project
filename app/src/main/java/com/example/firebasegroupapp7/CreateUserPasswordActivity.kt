package com.example.firebasegroupapp7

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebasegroupapp7.databinding.CreateUserAddressBinding
import com.example.firebasegroupapp7.databinding.CreateUserPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CreateUserPasswordActivity : AppCompatActivity() {

    private lateinit var binding: CreateUserPasswordBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var signupButton: Button
    private lateinit var backToLoginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CreateUserPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val createAccount: CreateAccount =
            intent.getSerializableExtra("createAccount") as CreateAccount

        password = findViewById(R.id.passwordText)
        confirmPassword = findViewById(R.id.confirmPasswordText)
        signupButton = findViewById(R.id.signUpBtn)
        backToLoginButton = findViewById(R.id.backToLogin)

        signupButton.setOnClickListener {
            val passwordInput = password.text.toString().trim()
            val confirmPasswordInput = confirmPassword.text.toString().trim()

            if (isPasswordValid(passwordInput, confirmPasswordInput)) {
                var userEmail = "";

                if(createAccount.email != null) {
                    userEmail = createAccount.email!!
                }

                auth.createUserWithEmailAndPassword(
                    userEmail,
                    password.text.toString().trim()
                )
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val pathString = "userInfo/" + user?.uid
                            FirebaseDatabase.getInstance().reference.child(pathString).push()
                                .setValue(createAccount).addOnCompleteListener {
                                    startActivity(Intent(this, SignInActivity::class.java))
                                }
                        } else {
                            Toast.makeText(
                                baseContext,
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
            }
        }

        backToLoginButton.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    private fun isPasswordValid(password: String, confirmPassword: String): Boolean {
        if (password.isEmpty()) {
            this.password.error = "Password is required"
            return false
        }

        if (password.length < 6) {
            this.password.error = "Password must be at least 6 characters"
            return false
        }

        if (!password.matches(".*[A-Z].*".toRegex())) {
            this.password.error = "Password must contain at least one uppercase letter"
            return false
        }

        if (!password.matches(".*[a-z].*".toRegex())) {
            this.password.error = "Password must contain at least one lowercase letter"
            return false
        }

        if (!password.matches(".*\\d.*".toRegex())) {
            this.password.error = "Password must contain at least one digit"
            return false
        }

        if (!password.matches(".*[@#\$%^&+=!].*".toRegex())) {
            this.password.error = "Password must contain at least one special character"
            return false
        }

        if (confirmPassword.isEmpty()) {
            this.confirmPassword.error = "Please confirm your password"
            return false
        }

        if (password != confirmPassword) {
            this.confirmPassword.error = "Passwords do not match"
            return false
        }

        return true
    }
}
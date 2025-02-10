package com.example.firebasegroupapp7

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasegroupapp7.databinding.CreateUserPersonalInfoBinding

class CreateUserPersonalInfoActivity : AppCompatActivity() {

    private lateinit var binding: CreateUserPersonalInfoBinding

    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var email: EditText
    private lateinit var nextButton: Button
    private lateinit var backToLoginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CreateUserPersonalInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firstName = findViewById(R.id.firstNameText)
        lastName = findViewById(R.id.lastNameText)
        email = findViewById(R.id.emailText)
        nextButton = findViewById(R.id.nextBtn)
        backToLoginButton = findViewById(R.id.backToLogin)

        nextButton.setOnClickListener {
            val firstNameInput = firstName.text.toString().trim()
            val lastNameInput = lastName.text.toString().trim()
            val emailInput = email.text.toString().trim()
            val createAccount = CreateAccount(firstNameInput, lastNameInput, emailInput)

            if (isFormValid(firstNameInput, lastNameInput, emailInput)) {
                intent = Intent(this, CreateUserAddressActivity::class.java)
                intent.putExtra("createAccount", createAccount)
                startActivity(intent)
            }
        }

        backToLoginButton.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

    }

    private fun isFormValid(
        firstNameInput: String,
        lastNameInput: String,
        emailInput: String
    ): Boolean {
        if (firstNameInput.isEmpty() || firstNameInput.length < 2) {
            firstName.error = "First name must be at least 2 characters"
            return false
        }

        if (lastNameInput.isEmpty() || lastNameInput.length < 2) {
            lastName.error = "Last name must be at least 2 characters"
            return false
        }

        if (emailInput.isEmpty()) {
            email.error = "Email is required"
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            email.error = "Enter a valid email"
            return false
        }

        return true
    }
}
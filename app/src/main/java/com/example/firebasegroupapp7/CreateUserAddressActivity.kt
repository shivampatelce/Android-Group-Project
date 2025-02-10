package com.example.firebasegroupapp7

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebasegroupapp7.databinding.CreateUserAddressBinding
import com.example.firebasegroupapp7.databinding.CreateUserPersonalInfoBinding
import com.firebase.ui.auth.data.model.User

class CreateUserAddressActivity : AppCompatActivity() {

    private lateinit var binding: CreateUserAddressBinding

    private lateinit var streetAddress: EditText
    private lateinit var city: EditText
    private lateinit var province: EditText
    private lateinit var postalCode: EditText
    private lateinit var nextButton: Button
    private lateinit var skipButton: Button
    private lateinit var backToLoginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CreateUserAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val createAccount: CreateAccount =
            intent.getSerializableExtra("createAccount") as CreateAccount

        streetAddress = findViewById(R.id.streetAddressText)
        city = findViewById(R.id.cityText)
        province = findViewById(R.id.provinceText)
        postalCode = findViewById(R.id.postalCode)
        nextButton = findViewById(R.id.nextBtn)
        skipButton = findViewById(R.id.skipBtn)
        backToLoginButton = findViewById(R.id.backToLogin)

        nextButton.setOnClickListener {
            val streetAddressInput = streetAddress.text.toString().trim()
            val cityInput = city.text.toString().trim()
            val provinceInput = province.text.toString().trim()
            val postalCodeInput = postalCode.text.toString().trim()

            if (isFormValid(streetAddressInput, cityInput, provinceInput, postalCodeInput)) {
                val userAddress = UserAddress(streetAddressInput, cityInput, provinceInput, postalCodeInput)

                createAccount.address = listOf(userAddress)

                intent = Intent(this, CreateUserPasswordActivity::class.java)
                intent.putExtra("createAccount", createAccount)
                startActivity(intent)
            }
        }

        skipButton.setOnClickListener {
            intent = Intent(this, CreateUserPasswordActivity::class.java)
            intent.putExtra("createAccount", createAccount)
            startActivity(intent)
        }

        backToLoginButton.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

    }

    private fun isFormValid(
        street: String,
        city: String,
        province: String,
        postal: String
    ): Boolean {
        if (street.isEmpty()) {
            streetAddress.error = "Street address is required"
            return false
        }

        if (city.isEmpty()) {
            this.city.error = "City is required"
            return false
        }

        if (province.isEmpty()) {
            this.province.error = "Province is required"
            return false
        }

        if (postal.isEmpty()) {
            postalCode.error = "Postal code is required"
            return false
        }

        val postalCodePattern = Regex("^[A-Za-z]\\d[A-Za-z][ -]?\\d[A-Za-z]\\d\$")
        if (!postalCodePattern.matches(postal)) {
            postalCode.error = "Enter a valid postal code"
            return false
        }

        return true
    }
}
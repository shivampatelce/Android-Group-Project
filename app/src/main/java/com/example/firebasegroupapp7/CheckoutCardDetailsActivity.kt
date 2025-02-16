package com.example.firebasegroupapp7

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebasegroupapp7.databinding.CheckOutSelectAddressBinding
import com.example.firebasegroupapp7.databinding.CheckoutCardDetailsBinding
import java.util.Calendar

class CheckoutCardDetailsActivity : AppCompatActivity() {

    private lateinit var binding: CheckoutCardDetailsBinding

    private lateinit var cardHolderName: EditText
    private lateinit var cardNumber: EditText
    private lateinit var expiryMonth: EditText
    private lateinit var expiryYear: EditText
    private lateinit var cvv: EditText
    private lateinit var paymentButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CheckoutCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cardHolderName = findViewById(R.id.cardHolderName)
        cardNumber = findViewById(R.id.cardNumber)
        expiryMonth = findViewById(R.id.expiryMonth)
        expiryYear = findViewById(R.id.expiryYear)
        cvv = findViewById(R.id.cvv)
        paymentButton = findViewById(R.id.paymentButton)

        paymentButton.setOnClickListener {
            if (validateInputs()) {
                startActivity(Intent(this, ThankYouActivity::class.java))
            }
        }

    }

    private fun validateInputs(): Boolean {
        val name = cardHolderName.text.toString().trim()
        val number = cardNumber.text.toString().trim()
        val month: Int = expiryMonth.text.toString().trim().toIntOrNull() ?: 0
        val year = expiryYear.text.toString().trim().toIntOrNull() ?: 0
        val cvvCode = cvv.text.toString().trim()

        if (name.isEmpty()) {
            cardHolderName.error = "Cardholder name is required"
            return false
        }

        if (number.isEmpty() || number.length != 16) {
            cardNumber.error = "Enter a valid 16-digit card number"
            return false
        }

        if (this.expiryMonth.text.toString().isEmpty() || month < 1 || month > 12) {
            expiryMonth.error = "Enter a valid month (1-12)"
            return false
        }

        if (this.expiryYear.text.toString().isEmpty() || year < Calendar.getInstance()
                .get(Calendar.YEAR)
        ) {
            expiryYear.error = "Enter a valid expiry year"
            return false
        }

        if (cvvCode.isEmpty() || !cvvCode.matches(Regex("^[0-9]{3,4}\$"))) {
            cvv.error = "Enter a valid 3 or 4-digit CVV"
            return false
        }

        return true
    }
}
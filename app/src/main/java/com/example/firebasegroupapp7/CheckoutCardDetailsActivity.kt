package com.example.firebasegroupapp7

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebasegroupapp7.databinding.CheckOutSelectAddressBinding
import com.example.firebasegroupapp7.databinding.CheckoutCardDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class CheckoutCardDetailsActivity : AppCompatActivity() {

    private lateinit var binding: CheckoutCardDetailsBinding

    private lateinit var cardHolderName: EditText
    private lateinit var cardNumber: EditText
    private lateinit var expiryMonth: EditText
    private lateinit var expiryYear: EditText
    private lateinit var cvv: EditText
    private lateinit var paymentButton: Button
    private lateinit var amountText: TextView
    private var totalBillingAmount: Long = 0
    private lateinit var cartList: ArrayList<Cart>
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var address: UserAddress

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CheckoutCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        totalBillingAmount = intent.getLongExtra("totalBillingAmount", -1L)
        cartList = intent.getSerializableExtra("cartItems") as ArrayList<Cart>
        address = intent.getSerializableExtra("address") as UserAddress

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser

        cardHolderName = findViewById(R.id.cardHolderName)
        cardNumber = findViewById(R.id.cardNumber)
        expiryMonth = findViewById(R.id.expiryMonth)
        expiryYear = findViewById(R.id.expiryYear)
        cvv = findViewById(R.id.cvv)
        paymentButton = findViewById(R.id.paymentButton)
        amountText = findViewById(R.id.amount)

        amountText.setText("$ " + totalBillingAmount.toString())

        paymentButton.setOnClickListener {
            if (validateInputs()) {
                completeOrder(Order(user?.uid!!, cartList, address))
            }
        }
    }

    private fun completeOrder(order: Order) {
        database.reference.child("orders/" + user?.uid)
            .get()
            .addOnSuccessListener { snapshot ->
                val orderList: MutableList<Order> = mutableListOf()
                if(snapshot.exists()) {
                    for (orderSnapshot in snapshot.children) {
                        val orderItem: Order? = orderSnapshot.getValue(Order::class.java)
                        if(orderItem != null) {
                            orderList.add(orderItem)
                        }
                    }
                }
                orderList.add(order)

                snapshot.ref.setValue(orderList)
                    .addOnSuccessListener {
                        emptyCart()
                    }
            }
    }

    private fun emptyCart() {
        val pathString = "cart/" + user?.uid

        database.reference.child(pathString).removeValue()
            .addOnSuccessListener {
                startActivity(Intent(this, ThankYouActivity::class.java))
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
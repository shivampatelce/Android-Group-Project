package com.example.firebasegroupapp7

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebasegroupapp7.databinding.ProductDetailsBinding
import com.example.firebasegroupapp7.databinding.SignInBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ProductDetailsBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null

    private lateinit var productTitle: TextView
    private lateinit var productDescription: TextView
    private lateinit var productPrice: TextView
    private lateinit var cartQuantity: EditText
    private lateinit var incrementButton: Button
    private lateinit var decrementButton: Button
    private lateinit var addToCartButton: ImageButton
    private lateinit var removeFromCartButton: ImageButton
    private var productId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        user = auth.currentUser

        database = FirebaseDatabase.getInstance()

        productTitle = findViewById(R.id.productTitle)
        productDescription = findViewById(R.id.productDescription)
        productPrice = findViewById(R.id.productPrice)
        cartQuantity = findViewById(R.id.quantity)
        incrementButton = findViewById(R.id.increaseQnt)
        decrementButton = findViewById(R.id.decreaseQnt)
        addToCartButton = findViewById(R.id.addToCart)
        removeFromCartButton = findViewById(R.id.removeFormCart)

        productId = intent.getLongExtra("productId", -1L)

        database.reference.child("products").child(productId.toString()).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val product = snapshot.getValue(Product::class.java)
                    if (product != null) {
                        productTitle.text = product.title
                        productDescription.text = product.description
                        productPrice.text = "$${product.price.toString()}"
                    }
                }
            }

        setInitialCartQuantity()

        incrementButton.setOnClickListener {
            var quantity: Int = cartQuantity.text.toString().toIntOrNull() ?: 0
            quantity += 1;
            if (quantity <= 5) {
                cartQuantity.setText(quantity.toString())
                updateCartItem()
            }
        }

        decrementButton.setOnClickListener {
            var quantity: Int = cartQuantity.text.toString().toIntOrNull() ?: 0
            quantity -= 1
            if (quantity >= 1) {
                cartQuantity.setText(quantity.toString())
                updateCartItem()
            }
        }

        addToCartButton.setOnClickListener {
            updateCartItem()
        }

        removeFromCartButton.setOnClickListener {
            removeItemFromCart()
        }
    }

    private fun removeItemFromCart() {
        val pathString = "cart/" + user?.uid

        database.reference.child(pathString).get()
            .addOnSuccessListener { snapshot ->
                val cartList: MutableList<Cart> = mutableListOf()
                for (cartListSnapshot in snapshot.children) {
                    for (cartItemSnapshot in cartListSnapshot.children) {
                        val cartItem: Cart? = cartItemSnapshot.getValue(Cart::class.java)
                        if (cartItem != null) {
                            cartList.add(cartItem)
                        }
                    }

                    val updatedCartList = cartList.filter {
                        cartItem -> cartItem.productId != productId
                    }

                    cartListSnapshot.ref.setValue(updatedCartList).addOnSuccessListener {
                        addToCartButton.visibility = View.VISIBLE
                        removeFromCartButton.visibility = View.GONE
                        cartQuantity.setText("1")
                    }
                }
            }
    }

    private fun updateCartItem() {
        val pathString = "cart/" + user?.uid

        database.reference.child(pathString).get()
            .addOnSuccessListener { snapshot ->
                val cartList: MutableList<Cart> = mutableListOf()
                val updatedQuantity: Int = cartQuantity.text.toString().toIntOrNull() ?: 0
                if (snapshot.exists()) {
                    for (cartListSnapshot in snapshot.children) {
                        for (cartItemSnapshot in cartListSnapshot.children) {
                            val cartItem: Cart? = cartItemSnapshot.getValue(Cart::class.java)
                            if (cartItem != null) {
                                cartList.add(cartItem)
                            }
                        }

                        val exitingCartItem: Cart? =
                            cartList.find { cartItem -> cartItem.productId == productId }

                        if(exitingCartItem != null) {
                            val updatedCartList: List<Cart> = cartList.map { cartItem ->
                                if (cartItem.productId == productId) {
                                    cartItem.quantity = updatedQuantity
                                    cartItem
                                } else {
                                    cartItem
                                }
                            }
                            cartListSnapshot.ref.setValue(updatedCartList)
                        } else {
                            cartList.add(Cart(productId!!, updatedQuantity))
                            cartListSnapshot.ref.setValue(cartList)
                        }

                        addToCartButton.visibility = View.GONE
                        removeFromCartButton.visibility = View.VISIBLE

                    }
                } else {
                    cartList.add(Cart(productId!!, cartQuantity.text.toString().toIntOrNull() ?: 0))
                    FirebaseDatabase.getInstance().reference.child(pathString).push()
                        .setValue(cartList).addOnCompleteListener {
                            addToCartButton.visibility = View.GONE
                            removeFromCartButton.visibility = View.VISIBLE
                        }
                }
            }
    }

    private fun setInitialCartQuantity() {
        val pathString = "cart/" + user?.uid

        database.reference.child(pathString).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    for (cartListSnapshot in snapshot.children) {
                        val cartList: MutableList<Cart> = mutableListOf()
                        for (cartItemSnapshot in cartListSnapshot.children) {
                            val cartItem = cartItemSnapshot.getValue(Cart::class.java)
                            if (cartItem != null) {
                                cartList.add(cartItem)
                            }
                        }

                        val existingCartItem: Cart? = cartList.find { cartItem ->
                            cartItem.productId == productId
                        }

                        if (existingCartItem != null) {
                            cartQuantity.setText(existingCartItem.quantity.toString())
                            addToCartButton.visibility = View.GONE
                            removeFromCartButton.visibility = View.VISIBLE
                        } else {
                            cartQuantity.setText("1")
                            addToCartButton.visibility = View.VISIBLE
                            removeFromCartButton.visibility = View.GONE
                        }
                    }
                } else {
                    cartQuantity.setText("1")
                    addToCartButton.visibility = View.VISIBLE
                    removeFromCartButton.visibility = View.GONE
                }
            }
    }
}
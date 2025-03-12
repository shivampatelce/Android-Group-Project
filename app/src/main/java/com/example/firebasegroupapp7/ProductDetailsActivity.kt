package com.example.firebasegroupapp7

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.firebasegroupapp7.databinding.ProductDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ProductDetailsBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null

    private lateinit var productImg: ImageView
    private lateinit var productTitle: TextView
    private lateinit var productDescription: TextView
    private lateinit var itemDetails: TextView
    private lateinit var productPrice: TextView
    private lateinit var cartQuantity: TextView
    private lateinit var incrementButton: Button
    private lateinit var decrementButton: Button
    private lateinit var addToCartButton: Button
    private lateinit var removeFromCart: Button
    private lateinit var quantityContainer: LinearLayout
    private lateinit var backButton: ImageButton
    private lateinit var goToCartButton: ImageButton
    private var productId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        user = auth.currentUser

        database = FirebaseDatabase.getInstance()

        productImg = findViewById(R.id.productImg)
        productTitle = findViewById(R.id.productTitle)
        productDescription = findViewById(R.id.productDescription)
        itemDetails = findViewById(R.id.itemDetails)
        productPrice = findViewById(R.id.productPrice)
        cartQuantity = findViewById(R.id.quantity)
        incrementButton = findViewById(R.id.increaseQnt)
        decrementButton = findViewById(R.id.decreaseQnt)
        addToCartButton = findViewById(R.id.addToCart)
        removeFromCart = findViewById(R.id.removeFromCart)
        quantityContainer = findViewById(R.id.quantityContainer)
        backButton = findViewById(R.id.backButton)
        goToCartButton = findViewById(R.id.goToCartButton)

        productId = intent.getLongExtra("productId", -1L)

        database.reference.child("products").child(productId.toString()).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val product = snapshot.getValue(Product::class.java)
                    if (product != null) {

                        val theImage: String = product.image ?: ""
                        if (theImage.startsWith("gs://")) {
                            val storageReference =
                                FirebaseStorage.getInstance().getReferenceFromUrl(theImage)

                            Glide.with(this)
                                .load(storageReference)
                                .into(productImg)
                        }
                        productTitle.text = product.title
                        productDescription.text = product.description
                        itemDetails.text = product.itemsDetails
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

        removeFromCart.setOnClickListener {
            removeItemFromCart()
        }

        backButton.setOnClickListener {
            startActivity(Intent(this, ProductsListActivity::class.java))
        }

        goToCartButton.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
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

                    val updatedCartList = cartList.filter { cartItem ->
                        cartItem.productId != productId
                    }

                    cartListSnapshot.ref.setValue(updatedCartList).addOnSuccessListener {
                        addToCartButton.visibility = View.VISIBLE
                        removeFromCart.visibility = View.GONE
                        quantityContainer.visibility = View.GONE
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

                        if (exitingCartItem != null) {
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
                        removeFromCart.visibility = View.VISIBLE
                        quantityContainer.visibility = View.VISIBLE

                    }
                } else {
                    cartList.add(Cart(productId!!, cartQuantity.text.toString().toIntOrNull() ?: 0))
                    FirebaseDatabase.getInstance().reference.child(pathString).push()
                        .setValue(cartList).addOnCompleteListener {
                            addToCartButton.visibility = View.GONE
                            removeFromCart.visibility = View.VISIBLE
                            quantityContainer.visibility = View.VISIBLE
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
                            removeFromCart.visibility = View.VISIBLE
                            quantityContainer.visibility = View.VISIBLE
                        } else {
                            cartQuantity.setText("1")
                            addToCartButton.visibility = View.VISIBLE
                            removeFromCart.visibility = View.GONE
                            quantityContainer.visibility = View.GONE
                        }
                    }
                } else {
                    cartQuantity.setText("1")
                    addToCartButton.visibility = View.VISIBLE
                    removeFromCart.visibility = View.GONE
                    quantityContainer.visibility = View.GONE
                }
            }
    }
}
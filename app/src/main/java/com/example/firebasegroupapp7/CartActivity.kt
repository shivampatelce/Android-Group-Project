package com.example.firebasegroupapp7

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasegroupapp7.databinding.CartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class CartActivity : AppCompatActivity() {

    private lateinit var binding: CartBinding

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private var adapter: CartAdapter? = null

    private lateinit var checkoutBtn: Button
    private lateinit var subtotalText: TextView
    private lateinit var taxText: TextView
    private lateinit var totalAmountText: TextView
    private lateinit var emptyCartView: ConstraintLayout
    private lateinit var cartListView: ScrollView
    private var totalBillingAmount: Long = 0
    private var cartList = arrayListOf<Cart>()
    private lateinit var currentUser: FirebaseUser
    private lateinit var productList: MutableList<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        currentUser = auth.currentUser!!

        checkoutBtn = findViewById(R.id.checkoutBtn)
        subtotalText = findViewById(R.id.subtotalAmount)
        taxText = findViewById(R.id.taxAmount)
        totalAmountText = findViewById(R.id.totalAmount)
        emptyCartView = findViewById(R.id.emptyCart)
        cartListView = findViewById(R.id.cartListView)

        checkoutBtn.setOnClickListener {
            if (totalBillingAmount != 0L) {
                val intent = Intent(this, CheckOutSelectAddressActivity::class.java)
                intent.putExtra("cartItems", cartList)
                intent.putExtra("totalBillingAmount", totalBillingAmount)
                startActivity(intent)
            }
        }

        productList = getProductList()

        adapter = CartAdapter(cartList, productList, database, currentUser, this)

        val recyclerView: RecyclerView = findViewById(R.id.cartRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        updateCart()
    }

    fun updateCart() {
        val pathString = "cart/" + currentUser?.uid
        database.reference.child(pathString).get()
            .addOnSuccessListener { snapshot ->
                cartList.clear()
                if (snapshot.exists()) {
                    for (cartListSnapshot in snapshot.children) {
                        for (cartItemSnapshot in cartListSnapshot.children) {
                            val cartItem = cartItemSnapshot.getValue(Cart::class.java)
                            if (cartItem != null) {
                                cartList.add(cartItem)
                            }
                        }
                    }
                }

                adapter?.notifyDataSetChanged()
                updateCartView()
                updateTotalBilling()
            }
    }

    private fun updateCartView() {
        if (cartList.isEmpty()) {
            emptyCartView.visibility = View.VISIBLE
            cartListView.visibility = View.GONE
        } else {
            emptyCartView.visibility = View.GONE
            cartListView.visibility = View.VISIBLE
        }
    }

    private fun getProductList(): MutableList<Product> {
        val productList: MutableList<Product> = mutableListOf()
        database.reference.child("products").get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    for (productSnapshot in snapshot.children) {
                        val product = productSnapshot.getValue(Product::class.java)
                        if (product != null) {
                            productList.add(product)
                        }
                    }
                }
            }

        return productList;
    }

    private fun updateTotalBilling() {
        var subtotal: Long = 0
        var tax: Long = 0
        cartList.forEach { cartItem ->
            val product: Product? =
                productList.find { productItem -> productItem.productId == cartItem.productId }
            if (product != null) {
                val itemTotal = cartItem.quantity.times(product.price!!)
                if (itemTotal != null) {
                    subtotal += itemTotal
                }
            }
        }

        tax = (subtotal * 0.05).toLong()
        totalBillingAmount = subtotal + tax

        subtotalText.setText("$ " + subtotal.toString())
        taxText.setText("$ " + tax.toString())
        totalAmountText.setText("$ " + totalBillingAmount.toString())
    }

}
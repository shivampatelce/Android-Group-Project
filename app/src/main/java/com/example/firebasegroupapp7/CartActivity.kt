package com.example.firebasegroupapp7

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasegroupapp7.databinding.CartBinding
import com.google.firebase.auth.FirebaseAuth
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
    private var totalBillingAmount: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser

        checkoutBtn = findViewById(R.id.checkoutBtn)
        subtotalText = findViewById(R.id.subtotalAmount)
        taxText = findViewById(R.id.taxAmount)
        totalAmountText = findViewById(R.id.totalAmount)

        checkoutBtn.setOnClickListener {
            val intent = Intent(this, CheckOutSelectAddressActivity::class.java)
            intent.putExtra("totalBillingAmount", totalBillingAmount)
            startActivity(intent)
        }

        val cartList = mutableListOf<Cart>()
        val productList = getProductList()

        adapter = CartAdapter(cartList, productList, database, currentUser)

        val recyclerView: RecyclerView = findViewById(R.id.cartRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val pathString = "cart/" + currentUser?.uid
        database.reference.child(pathString).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    for (cartListSnapshot in snapshot.children) {
                        for (cartItemSnapshot in cartListSnapshot.children) {
                            val cartItem = cartItemSnapshot.getValue(Cart::class.java)
                            if (cartItem != null) {
                                cartList.add(cartItem)
                            }
                        }
                    }
                    updateTotalBilling(cartList, productList)
                    adapter?.notifyDataSetChanged()
                }
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

    private fun updateTotalBilling(
        cartList: MutableList<Cart>,
        productsList: MutableList<Product>
    ) {
        var subtotal: Long = 0
        var tax: Long = 0
        cartList.forEach { cartItem ->
            val product : Product? = productsList.find { productItem -> productItem.productId == cartItem.productId }
            if(product != null) {
                val itemTotal = product.price?.times(cartItem.quantity)
                if (itemTotal != null) {
                    subtotal += itemTotal
                }
            }
        }

        tax = (subtotal * 0.05).toLong()
        totalBillingAmount = subtotal + tax

        subtotalText.setText("$ " + subtotal.toString())
        taxText.setText("$ "+ tax.toString())
        totalAmountText.setText("$ "+ totalBillingAmount.toString())
    }

}
package com.example.firebasegroupapp7

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebasegroupapp7.databinding.CartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class CartActivity : AppCompatActivity() {

    private lateinit var binding: CartBinding

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private var adapter: CartAdapter? = null

    private lateinit var checkoutBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser

        checkoutBtn = findViewById(R.id.checkoutBtn)

        checkoutBtn.setOnClickListener {
            startActivity(Intent(this, CheckOutSelectAddressActivity::class.java))
        }

        val cartList = mutableListOf<Cart>()

        adapter = CartAdapter(cartList, getProductList(), database, currentUser)

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
                        if(product != null) {
                            productList.add(product)
                        }
                    }
                }
            }

        return productList;
    }

}
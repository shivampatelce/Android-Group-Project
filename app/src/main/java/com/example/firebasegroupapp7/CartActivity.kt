package com.example.firebasegroupapp7

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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
        database.reference.child("cart/${currentUser?.uid}").get()
            .addOnSuccessListener { cartSnapshot ->
                val cartProduct = cartSnapshot.getValue(Cart::class.java)
                val productList = mutableListOf<Long>()

            }

//        adapter = CartAdapter(options)

        val recyclerView: RecyclerView = findViewById(R.id.cartRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

}
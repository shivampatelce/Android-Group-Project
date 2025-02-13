package com.example.firebasegroupapp7

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasegroupapp7.databinding.ProductsListBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProductsListActivity : AppCompatActivity() {
    private lateinit var binding: ProductsListBinding

    private lateinit var auth: FirebaseAuth
    private var adapter: ProductAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProductsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser

        if (currentUser == null) {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
//            loadUI()
        }

    }

    private fun loadUI() {
        val query = FirebaseDatabase.getInstance().reference.child("products")
        val options =
            FirebaseRecyclerOptions.Builder<Product>().setQuery(query, Product::class.java).build()

        adapter = ProductAdapter(options)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }
}
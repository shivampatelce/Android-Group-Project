package com.example.firebasegroupapp7

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasegroupapp7.databinding.ProductsListBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProductsListActivity : AppCompatActivity() {
    private lateinit var binding: ProductsListBinding

    private lateinit var auth: FirebaseAuth
    private var adapter: ProductAdapter? = null

    private lateinit var goToCartButton: ImageButton
    private lateinit var logoutButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProductsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser

        goToCartButton = findViewById(R.id.goToCartButton)
        logoutButton = findViewById(R.id.logoutButton)

        if (currentUser == null) {
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            loadUI()
        }

        goToCartButton.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }

    private fun loadUI() {
        val query = FirebaseDatabase.getInstance().reference.child("products")
        val options =
            FirebaseRecyclerOptions.Builder<Product>().setQuery(query, Product::class.java).build()

        adapter = ProductAdapter(options, FirebaseDatabase.getInstance(), auth.currentUser)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        // Dynamically set the number of columns based on orientation
        val spanCount = if (resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) 2 else 1

        recyclerView.layoutManager = GridLayoutManager(this, spanCount) // Use GridLayoutManager
        recyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }
}
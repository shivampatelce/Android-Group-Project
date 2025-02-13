package com.example.firebasegroupapp7

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebasegroupapp7.databinding.CartBinding
import com.example.firebasegroupapp7.databinding.SignInBinding

class CartActivity : AppCompatActivity() {

    private lateinit var binding: CartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CartBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
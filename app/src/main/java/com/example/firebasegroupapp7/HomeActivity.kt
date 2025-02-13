package com.example.firebasegroupapp7

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebasegroupapp7.databinding.ActivityMainBinding
import com.example.firebasegroupapp7.databinding.HomeBinding
import com.example.firebasegroupapp7.databinding.ProductsListBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: HomeBinding

    private lateinit var startBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = HomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startBtn = findViewById(R.id.startBtn)

        startBtn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }
}
package com.example.firebasegroupapp7

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasegroupapp7.databinding.CheckOutSelectAddressBinding
import com.example.firebasegroupapp7.databinding.ThankYouBinding

class ThankYouActivity : AppCompatActivity() {

    private lateinit var binding: ThankYouBinding
    private lateinit var shopMoreButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ThankYouBinding.inflate(layoutInflater)
        setContentView(binding.root)

        shopMoreButton = findViewById(R.id.shopMoreButton)

        shopMoreButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
package com.example.firebasegroupapp7

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasegroupapp7.databinding.CheckOutSelectAddressBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class CheckOutSelectAddressActivity : AppCompatActivity() {

    private lateinit var binding: CheckOutSelectAddressBinding
    private var adapter: AddressAdapter? = null
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var database: FirebaseDatabase
    private val addressList = mutableListOf<UserAddress>();

    private lateinit var addressView: LinearLayout
    private lateinit var streetAddress: EditText
    private lateinit var city: EditText
    private lateinit var province: EditText
    private lateinit var postalCode: EditText
    private lateinit var addNewAddressBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CheckOutSelectAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        user = auth.currentUser

        database = FirebaseDatabase.getInstance()

        addressView = findViewById(R.id.addressView)
        streetAddress = findViewById(R.id.streetAddressText)
        city = findViewById(R.id.cityText)
        province = findViewById(R.id.provinceText)
        postalCode = findViewById(R.id.postalCode)
        addNewAddressBtn = findViewById(R.id.addAddress)

        adapter = AddressAdapter(addressList)

        val recyclerView: RecyclerView = findViewById(R.id.addressListRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        updateAddressList()

        addNewAddressBtn.setOnClickListener {
            val streetAddressInput = streetAddress.text.toString().trim()
            val cityInput = city.text.toString().trim()
            val provinceInput = province.text.toString().trim()
            val postalCodeInput = postalCode.text.toString().trim()

            if (isFormValid(streetAddressInput, cityInput, provinceInput, postalCodeInput)) {
                addAddress(streetAddressInput, cityInput, provinceInput, postalCodeInput)
            }
        }
    }

    private fun addAddress(
        streetAddress: String,
        city: String,
        province: String,
        postalCode: String
    ) {
        val userAddress =
            UserAddress(streetAddress, city, province, postalCode)

        database.reference.child("userInfo").child(user?.uid.toString()).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    snapshot.children.forEach { userSnapshot ->
                        val addressSnapshot = userSnapshot.child("address")
                        val newAddressList = mutableListOf<UserAddress>();
                        if (addressSnapshot.exists()) {
                            for (addressItem in addressSnapshot.children) {
                                val userAddress = addressItem.getValue(UserAddress::class.java)
                                if (userAddress != null) {
                                    newAddressList.add(userAddress)
                                }
                            }
                        }
                        newAddressList.add(userAddress)
                        userSnapshot.ref.child("address").setValue(newAddressList)
                            .addOnCompleteListener {

                                this.streetAddress.text.clear()
                                this.city.text.clear()
                                this.province.text.clear()
                                this.postalCode.text.clear()

                                updateAddressList()
                            }
                    }
                }
            }
    }

    private fun updateAddressList() {
        database.reference.child("userInfo").child(user?.uid.toString()).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    addressList.clear()

                    snapshot.children.forEach { userSnapshot ->
                        val addressSnapshot = userSnapshot.child("address")
                        if (addressSnapshot.exists()) {
                            for (addressItem in addressSnapshot.children) {
                                val userAddress = addressItem.getValue(UserAddress::class.java)
                                if (userAddress != null) {
                                    addressList.add(userAddress)
                                }
                            }
                        }
                    }
                }

                if (addressList.isEmpty()) {
                    addressView.visibility = View.GONE
                } else {
                    addressView.visibility = View.VISIBLE
                }
                adapter?.notifyDataSetChanged()
            }
    }

    private fun isFormValid(
        street: String,
        city: String,
        province: String,
        postal: String
    ): Boolean {
        if (street.isEmpty()) {
            streetAddress.error = "Street address is required"
            return false
        }

        if (city.isEmpty()) {
            this.city.error = "City is required"
            return false
        }

        if (province.isEmpty()) {
            this.province.error = "Province is required"
            return false
        }

        if (postal.isEmpty()) {
            postalCode.error = "Postal code is required"
            return false
        }

        val postalCodePattern = Regex("^[A-Za-z]\\d[A-Za-z][ -]?\\d[A-Za-z]\\d\$")
        if (!postalCodePattern.matches(postal)) {
            postalCode.error = "Enter a valid postal code"
            return false
        }

        return true
    }

}
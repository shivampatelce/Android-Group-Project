package com.example.firebasegroupapp7

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddressAdapter(private val addressList: MutableList<UserAddress>, private val totalBillingAmount: Long) :
    RecyclerView.Adapter<AddressAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val address: UserAddress = addressList[position]
        holder.addressLine.text = address.streetAddress
        holder.city.text = address.city
        holder.province.text = address.province
        holder.postalCode.text = address.postalCode

        holder.selectAddressButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, CheckoutCardDetailsActivity::class.java)
            intent.putExtra("address", address)
            intent.putExtra("totalBillingAmount", totalBillingAmount)
            holder.itemView.context.startActivity(intent)
        }

        holder.removeAddressButton.setOnClickListener {
            removeAddress(position)
        }
    }

    class MyViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.address_card, parent, false)) {
        val addressLine: TextView = itemView.findViewById(R.id.addressLine)
        val city: TextView = itemView.findViewById(R.id.city)
        val province: TextView = itemView.findViewById(R.id.province)
        val postalCode: TextView = itemView.findViewById(R.id.postalCode)
        val selectAddressButton: Button = itemView.findViewById(R.id.selectButton)
        val removeAddressButton: Button = itemView.findViewById(R.id.removeButton)
    }

    private fun removeAddress(addressIndex: Int) {
        FirebaseDatabase.getInstance().reference.child("userInfo")
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString()).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    snapshot.children.forEach { userSnapshot ->
                        addressList.removeAt(addressIndex)
                        userSnapshot.ref.child("address").setValue(addressList).addOnCompleteListener {
                            notifyItemRemoved(addressIndex)
                            notifyItemRangeChanged(addressIndex, addressList.size)
                        }
                    }
                }

            }
    }

}
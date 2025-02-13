package com.example.firebasegroupapp7

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class CartAdapter(private val options: FirebaseRecyclerOptions<Cart>) :
    FirebaseRecyclerAdapter<Cart, CartAdapter.MyViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: Cart) {
        holder.quantity.setText(model.quantity.toString())

    }

    class MyViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.cart_card, parent, false)) {
        val cartProductTitle: TextView = itemView.findViewById(R.id.cartProductTitle)
        val quantity: EditText = itemView.findViewById(R.id.quantity)
    }
}
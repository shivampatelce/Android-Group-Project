package com.example.firebasegroupapp7

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebasegroupapp7.ProductAdapter.MyViewHolder
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class CartAdapter(
    private val cartList: MutableList<Cart>,
    private val productList: MutableList<Product>,
    private val database: FirebaseDatabase,
    private val user: FirebaseUser?
) :
    RecyclerView.Adapter<CartAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val cartItem: Cart = cartList[position]
        val product: Product? = getProductDetailById(cartItem.productId)

        if (product != null) {
            holder.cartProductTitle.setText(product.title)

            val productImg: String = product.image ?: ""
            if (productImg.startsWith("gs://")) {
                val storageReference =
                    FirebaseStorage.getInstance().getReferenceFromUrl(productImg)

                Glide.with(holder.itemView.context)
                    .load(storageReference)
                    .into(holder.productImg)
            }
        }

        holder.incrementButton.setOnClickListener {
            var quantity: Int = holder.cartQuantity.text.toString().toIntOrNull() ?: 0
            quantity += 1;
            if (quantity <= 5) {
                holder.cartQuantity.setText(quantity.toString())
                updateCartItem(holder, cartItem.productId ?: 0)
            }
        }

        holder.decrementButton.setOnClickListener {
            var quantity: Int = holder.cartQuantity.text.toString().toIntOrNull() ?: 0
            quantity -= 1
            if (quantity >= 1) {
                holder.cartQuantity.setText(quantity.toString())
                updateCartItem(holder, cartItem.productId ?: 0)
            }
        }

        holder.removeFromCart.setOnClickListener {
            removeItemFromCart(cartItem.productId ?: 0, position)
        }

        holder.quantity.setText(cartItem.quantity.toString())
    }

    class MyViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.cart_card, parent, false)) {
        val cartProductTitle: TextView = itemView.findViewById(R.id.cartProductTitle)
        val quantity: TextView = itemView.findViewById(R.id.quantity)
        val productImg: ImageView = itemView.findViewById(R.id.cartProductImage)
        var cartQuantity: TextView = itemView.findViewById(R.id.quantity)
        var incrementButton: Button = itemView.findViewById(R.id.increaseQnt)
        var decrementButton: Button = itemView.findViewById(R.id.decreaseQnt)
        var removeFromCart: ImageButton = itemView.findViewById(R.id.removeButton)
    }

    private fun getProductDetailById(productId: Long): Product? {
        return productList.find { product -> product.productId == productId }
    }

    private fun removeItemFromCart(productId: Long, productIndex: Int) {
        val pathString = "cart/" + user?.uid

        database.reference.child(pathString).get()
            .addOnSuccessListener { snapshot ->
                val cartList: MutableList<Cart> = mutableListOf()
                for (cartListSnapshot in snapshot.children) {
                    for (cartItemSnapshot in cartListSnapshot.children) {
                        val cartItem: Cart? = cartItemSnapshot.getValue(Cart::class.java)
                        if (cartItem != null) {
                            cartList.add(cartItem)
                        }
                    }

                    val updatedCartList = cartList.filter { cartItem ->
                        cartItem.productId != productId
                    }

                    cartListSnapshot.ref.setValue(updatedCartList).addOnSuccessListener {
                        this.cartList.removeAt(productIndex)
                        notifyItemRemoved(productIndex)
                        notifyItemRangeChanged(productIndex, this.cartList.size)
                    }
                }
            }
    }

    private fun updateCartItem(holder: MyViewHolder, productId: Long) {
        val pathString = "cart/" + user?.uid

        database.reference.child(pathString).get()
            .addOnSuccessListener { snapshot ->
                val cartList: MutableList<Cart> = mutableListOf()
                val updatedQuantity: Int = holder.cartQuantity.text.toString().toIntOrNull() ?: 0
                if (snapshot.exists()) {
                    for (cartListSnapshot in snapshot.children) {
                        for (cartItemSnapshot in cartListSnapshot.children) {
                            val cartItem: Cart? = cartItemSnapshot.getValue(Cart::class.java)
                            if (cartItem != null) {
                                cartList.add(cartItem)
                            }
                        }

                        val exitingCartItem: Cart? =
                            cartList.find { cartItem -> cartItem.productId == productId }

                        if (exitingCartItem != null) {
                            val updatedCartList: List<Cart> = cartList.map { cartItem ->
                                if (cartItem.productId == productId) {
                                    cartItem.quantity = updatedQuantity
                                    cartItem
                                } else {
                                    cartItem
                                }
                            }
                            cartListSnapshot.ref.setValue(updatedCartList)
                        } else {
                            cartList.add(Cart(productId!!, updatedQuantity))
                            cartListSnapshot.ref.setValue(cartList)
                        }

                    }
                }
            }
    }
}
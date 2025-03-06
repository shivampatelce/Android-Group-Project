package com.example.firebasegroupapp7

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class ProductAdapter(
    private val options: FirebaseRecyclerOptions<Product>, private val database: FirebaseDatabase,
    private val user: FirebaseUser?
) :
    FirebaseRecyclerAdapter<Product, ProductAdapter.MyViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: Product) {

        holder.productTitle.text = model.title
        holder.productPrice.text = "$${model.price.toString()}"

        setInitialCartQuantity(holder, model.productId ?: 0)

        val image: String = model.image ?: ""
        if (image.indexOf("gs://") > -1) {
            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(image)
            Glide.with(holder.productImg.context)
                .load(storageReference)
                .into(holder.productImg)
        } else {
            Glide.with(holder.productImg.context)
                .load(image)
                .into(holder.productImg)
        }

        holder.productCard.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ProductDetailsActivity::class.java)

            intent.putExtra("productId", model.productId)
            context.startActivity(intent)
        }

        holder.incrementButton.setOnClickListener {
            var quantity: Int = holder.cartQuantity.text.toString().toIntOrNull() ?: 0
            quantity += 1;
            if (quantity <= 5) {
                holder.cartQuantity.setText(quantity.toString())
                updateCartItem(holder, model.productId ?: 0)
            }
        }

        holder.decrementButton.setOnClickListener {
            var quantity: Int = holder.cartQuantity.text.toString().toIntOrNull() ?: 0
            quantity -= 1
            if (quantity >= 1) {
                holder.cartQuantity.setText(quantity.toString())
                updateCartItem(holder, model.productId ?: 0)
            }
        }

        holder.addToCartButton.setOnClickListener {
            updateCartItem(holder, model.productId ?: 0)
        }

        holder.removeFromCart.setOnClickListener {
            removeItemFromCart(holder, model.productId ?: 0)
        }
    }

    class MyViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.product_card, parent, false)) {
        val productImg: ImageView = itemView.findViewById(R.id.productImg);
        val productTitle: TextView = itemView.findViewById(R.id.productTitle)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productCard: CardView = itemView.findViewById(R.id.productCard)
        var cartQuantity: TextView = itemView.findViewById(R.id.quantity)
        var incrementButton: Button = itemView.findViewById(R.id.increaseQnt)
        var decrementButton: Button = itemView.findViewById(R.id.decreaseQnt)
        var addToCartButton: Button = itemView.findViewById(R.id.addToCart)
        var removeFromCart: Button = itemView.findViewById(R.id.removeFromCart)
        var quantityContainer: LinearLayout = itemView.findViewById(R.id.quantityContainer)
    }

    private fun setInitialCartQuantity(holder: MyViewHolder, productId: Long) {
        val pathString = "cart/" + user?.uid

        database.reference.child(pathString).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    for (cartListSnapshot in snapshot.children) {
                        val cartList: MutableList<Cart> = mutableListOf()
                        for (cartItemSnapshot in cartListSnapshot.children) {
                            val cartItem = cartItemSnapshot.getValue(Cart::class.java)
                            if (cartItem != null) {
                                cartList.add(cartItem)
                            }
                        }

                        val existingCartItem: Cart? = cartList.find { cartItem ->
                            cartItem.productId == productId
                        }

                        if (existingCartItem != null) {
                            holder.cartQuantity.setText(existingCartItem.quantity.toString())
                            holder.addToCartButton.visibility = View.GONE
                            holder.removeFromCart.visibility = View.VISIBLE
                            holder.quantityContainer.visibility = View.VISIBLE
                        } else {
                            holder.cartQuantity.setText("1")
                            holder.addToCartButton.visibility = View.VISIBLE
                            holder.removeFromCart.visibility = View.GONE
                            holder.quantityContainer.visibility = View.GONE
                        }
                    }
                } else {
                    holder.cartQuantity.setText("1")
                    holder.addToCartButton.visibility = View.VISIBLE
                    holder.removeFromCart.visibility = View.GONE
                    holder.quantityContainer.visibility = View.GONE
                }
            }
    }

    private fun removeItemFromCart(holder: MyViewHolder, productId: Long) {
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
                        holder.addToCartButton.visibility = View.VISIBLE
                        holder.removeFromCart.visibility = View.GONE
                        holder.quantityContainer.visibility = View.GONE
                        holder.cartQuantity.setText("1")
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

                        holder.addToCartButton.visibility = View.GONE
                        holder.removeFromCart.visibility = View.VISIBLE
                        holder.quantityContainer.visibility = View.VISIBLE

                    }
                } else {
                    cartList.add(
                        Cart(
                            productId!!,
                            holder.cartQuantity.text.toString().toIntOrNull() ?: 0
                        )
                    )
                    FirebaseDatabase.getInstance().reference.child(pathString).push()
                        .setValue(cartList).addOnCompleteListener {
                            holder.addToCartButton.visibility = View.GONE
                            holder.removeFromCart.visibility = View.VISIBLE
                            holder.quantityContainer.visibility = View.VISIBLE
                        }
                }
            }
    }
}
package com.example.firebasegroupapp7

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.storage.FirebaseStorage

class ProductAdapter(private val options: FirebaseRecyclerOptions<Product>) :
    FirebaseRecyclerAdapter<Product, ProductAdapter.MyViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: Product) {

        holder.productTitle.text = model.title
//        holder.productDescription.text = model.description
        holder.productPrice.text = "$${model.price.toString()}"

        val image : String = model.image ?: ""
        if (image.indexOf("gs://") > -1){
            val storageReference =FirebaseStorage.getInstance().getReferenceFromUrl(image)
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
    }

    class MyViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.product_card, parent, false)) {
        val productImg: ImageView = itemView.findViewById(R.id.productImg);
        val productTitle: TextView = itemView.findViewById(R.id.productTitle)
//        val productDescription: TextView = itemView.findViewById(R.id.productDescription)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productCard: CardView = itemView.findViewById(R.id.productCard)
    }

}
package com.example.a6002cem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ItemAdapter(private var itemList:MutableList<Item>): RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):ItemViewHolder {

        val layoutView: View = LayoutInflater.from(parent.context).
        inflate(R.layout.item_card_view,parent,false)
        return ItemViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
//        holder.productImage.setImageResource(productList[position].img)
        Picasso.get().load(itemList[position].img)
            .into(holder.itemImage)
        holder.itemTitle.text = itemList[position].title
        holder.itemPrice.text = itemList[position].price
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        var itemImage: ImageView = view.findViewById(R.id.item_image)
        var itemTitle: TextView = view.findViewById(R.id.item_title)
        var itemPrice: TextView = view.findViewById(R.id.item_price)
    }


}
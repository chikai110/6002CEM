package com.example.a6002cem

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ItemAdapter(private var itemList:MutableList<Item>): RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int ):ItemViewHolder {
        val layoutView: View = LayoutInflater.from(parent.context).
        inflate(R.layout.item_card_view,parent,false)
        return ItemViewHolder(layoutView, mListener)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        Picasso.get().load(itemList[position].img)
            .into(holder.itemImage)
        holder.itemTitle.text = itemList[position].title
        holder.itemRelease.text = itemList[position].release
        holder.itemDuration.text = itemList[position].duration
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ItemViewHolder(view: View, listener: onItemClickListener): RecyclerView.ViewHolder(view){
        var itemImage: ImageView = view.findViewById(R.id.item_image)
        var itemTitle: TextView = view.findViewById(R.id.item_title)
        var itemRelease: TextView = view.findViewById(R.id.item_release)
        var itemDuration: TextView = view.findViewById(R.id.item_duration)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }


}
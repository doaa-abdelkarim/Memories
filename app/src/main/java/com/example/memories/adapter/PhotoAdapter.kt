package com.example.memories.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.memories.R
import com.example.memories.model.Photo
import kotlinx.android.synthetic.main.photos_list_item.view.*

class PhotoAdapter(var photos: ArrayList<Photo>? = null) :
    RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.photos_list_item, parent, false)
        return PhotoViewHolder(view, onItemClickListener)
    }

    override fun getItemCount(): Int = photos?.size?:0

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.imageViewPhoto.setImageURI(Uri.parse(photos?.get(position)?.uri))
    }

    fun refillPhotosList (photos: ArrayList<Photo>) {
        this.photos?.apply {
            clear()
            addAll(photos)
        }
        notifyDataSetChanged()
    }

    class PhotoViewHolder(itemView: View, onItemClickListener: OnItemClickListener?) :
        RecyclerView.ViewHolder(itemView) {
        val imageViewPhoto = itemView.image_view_memory_photo
        val imageViewDelete = itemView.image_view_delete
        init {
            imageViewDelete.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION)
                    onItemClickListener?.onDelete(adapterPosition)
            }
        }
    }

    interface OnItemClickListener {
        fun onDelete (position: Int)
    }
}
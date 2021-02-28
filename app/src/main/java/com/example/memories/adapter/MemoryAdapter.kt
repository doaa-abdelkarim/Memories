package com.example.memories.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.memories.R
import com.example.memories.model.MemoryWithPhotos
import com.example.memories.model.Photo
import com.example.memories.util.Utility
import kotlinx.android.synthetic.main.memories_list_item.view.*
import kotlinx.android.synthetic.main.memories_list_item.view.text_view_memory_desc
import java.util.*
import kotlin.collections.ArrayList


class MemoryAdapter(var memories: ArrayList<MemoryWithPhotos>?) :
    RecyclerView.Adapter<MemoryAdapter.MemoryViewHolder>() {

    var onItemClickListener: OnItemClickListener? = null
    var memoryFullList: ArrayList<MemoryWithPhotos> = ArrayList()

    init {
        memories?.let { memoryFullList.addAll(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoryViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.memories_list_item, parent, false)
        return MemoryViewHolder(view, onItemClickListener)
    }

    override fun getItemCount() = memories?.size ?: 0

    override fun onBindViewHolder(holder: MemoryViewHolder, position: Int) {
        holder.textViewMemoryDesc.setText(memories?.get(position)?.memory?.description ?: "")
        holder.textViewTime.text = Utility.formatTime(Date(memories?.get(position)?.memory?.time!!))
            ?: ""
        val photos = memories?.get(position)?.photos
        val cardView = holder.itemView.card_view
        if (position % 3 == 0)
            cardView.setBackgroundResource(R.drawable.bg_memeory_item_card_view)
        else if (position % 3 == 1)
            cardView.setBackgroundResource(R.drawable.bg_memeory_item_card_view2)
        else
            cardView.setBackgroundResource(R.drawable.bg_memeory_item_card_view3)

        if (photos.isNullOrEmpty()) {
            holder.imageViewMemoryPhoto.setImageResource(R.drawable.ic_memory_photo)
            holder.imageViewNext.visibility = View.GONE
            holder.imageViewPrev.visibility = View.GONE
            return
        } else if (photos.size > 1)
            holder.imageViewNext.visibility = View.VISIBLE
        else {
            holder.imageViewNext.visibility = View.GONE
            holder.imageViewPrev.visibility = View.GONE
        }

        holder.imageViewMemoryPhoto.setImageURI(Uri.parse(photos[0].uri))


    }

    fun refillMemoriesList(memories: ArrayList<MemoryWithPhotos>?) {
        memories?.let {
            this.memories?.clear()
            this.memories?.addAll(it)
            this.memoryFullList.clear()
            this.memoryFullList.addAll(it)
        }
    }

    fun filterList(text: String) {
        val filteredList = ArrayList<MemoryWithPhotos>()
        val filterPattern = text.toLowerCase().trim()
        for (item in memoryFullList) {
            if (item.memory.description.toLowerCase().contains(filterPattern)) {
                filteredList.add(item)
            }
        }
        memories = filteredList
        notifyDataSetChanged()
    }

    inner class MemoryViewHolder(itemView: View, onItemClickListener: OnItemClickListener?) :
        RecyclerView.ViewHolder(itemView) {

        var photoIndex = 0
        lateinit var photos:List<Photo>

        val textViewMemoryDesc = itemView.text_view_memory_desc
        val textViewTime = itemView.text_view_time
        val imageViewMemoryPhoto = itemView.image_view_memory_photo
        val imageViewPrev = itemView.image_view_prev
        val imageViewNext = itemView.image_view_next

        init {
            //textViewMemoryDesc.movementMethod = ScrollingMovementMethod()

            itemView.setOnLongClickListener {
                onItemClickListener?.onItemLongClick(it)
                true
            }

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION)
                    onItemClickListener?.onItemClick(adapterPosition)
            }

            imageViewNext.setOnClickListener {
                photos = memories!!.get(adapterPosition).photos
                ++photoIndex;
                imageViewPrev.visibility = View.VISIBLE
                if (photoIndex == photos.lastIndex)
                    imageViewNext.visibility = View.GONE
                imageViewMemoryPhoto.setImageURI(Uri.parse(photos[photoIndex].uri))

            }

            imageViewPrev.setOnClickListener {
                photos = memories!!.get(adapterPosition).photos
                --photoIndex;
                imageViewNext.visibility = View.VISIBLE
                if (photoIndex == 0)
                    imageViewPrev.visibility = View.GONE
                imageViewMemoryPhoto.setImageURI(Uri.parse(photos[photoIndex].uri))

            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onItemLongClick(view: View)
    }

}
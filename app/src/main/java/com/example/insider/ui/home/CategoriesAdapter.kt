package com.example.insider.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.insider.R
import com.example.insider.models.SubData
import kotlinx.android.synthetic.main.layout_category_item.view.*

class CategoriesAdapter(private val glide: RequestManager) :
    ListAdapter<SubData, CategoriesAdapter.ViewHolder>(SubDataDC()) {

    var listener: Listener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_category_item, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    fun swapData(data: List<SubData>) = submitList(data.toMutableList())

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(category: SubData) = with(itemView) {

            glide.load(category.icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(img_icon)

            txt_name.text = category.name

            txt_count.text = category.getEventCount()

            setOnClickListener { listener?.onClick(category.name) }

        }
    }


    private class SubDataDC : DiffUtil.ItemCallback<SubData>() {
        override fun areItemsTheSame(
            oldItem: SubData,
            newItem: SubData
        ): Boolean = oldItem._id == newItem._id

        override fun areContentsTheSame(
            oldItem: SubData,
            newItem: SubData
        ): Boolean = oldItem == newItem
    }

    interface Listener {
        fun onClick(category: String?)
    }

}

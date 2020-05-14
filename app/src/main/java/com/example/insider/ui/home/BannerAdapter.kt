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
import com.example.insider.models.Banner
import kotlinx.android.synthetic.main.layout_banner_item.view.*

class BannerAdapter(private val glide: RequestManager) :
    ListAdapter<Banner, BannerAdapter.ViewHolder>(BannerDC()) {

    var listener: Listener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.layout_banner_item, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    fun swapData(data: List<Banner>) = submitList(data.toMutableList())

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(banner: Banner) = with(itemView) {

            glide.load(banner.verticalCover)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(img_banner)

            setOnClickListener { listener?.onClick(banner.mapLink) }
        }
    }

    private class BannerDC : DiffUtil.ItemCallback<Banner>() {
        override fun areItemsTheSame(
            oldItem: Banner,
            newItem: Banner
        ): Boolean = oldItem._id == newItem._id

        override fun areContentsTheSame(
            oldItem: Banner,
            newItem: Banner
        ): Boolean = oldItem == newItem
    }

    interface Listener {
        fun onClick(link: String?)
    }
}
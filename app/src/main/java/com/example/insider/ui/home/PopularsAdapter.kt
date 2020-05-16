package com.example.insider.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.insider.R
import com.example.insider.models.Event
import com.example.insider.util.extensions.gone
import com.example.insider.util.extensions.visible
import kotlinx.android.synthetic.main.layout_popular_item.view.*

class PopularsAdapter(private val glide: RequestManager) :
    ListAdapter<Event, PopularsAdapter.ViewHolder>(EventDC()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_popular_item, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    fun swapData(data: List<Event>) = submitList(data.toMutableList())

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(event: Event) = with(itemView) {

            glide.load(event.horizontalCover)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(img_event_cover)

            txt_event_name.text = event.name

            txt_event_venue_date.text = event.venueDate

            txt_event_venue_name.text = event.venueName

            txt_price.text = event.getDisplayPrice()

            txt_category_name.apply {
                val categoryName = event.category?.name?.trim()
                if (!categoryName.isNullOrEmpty()) {
                    visible()
                    text = categoryName
                } else gone()
            }

            setOnClickListener { Log.d("TAG", "TestLog: c:${event.category}") }

        }
    }


    private class EventDC : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(
            oldItem: Event,
            newItem: Event
        ): Boolean = oldItem._id == newItem._id

        override fun areContentsTheSame(
            oldItem: Event,
            newItem: Event
        ): Boolean = oldItem == newItem
    }
}

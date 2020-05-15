package com.example.insider.ui.filter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.insider.R
import com.example.insider.models.Show
import kotlinx.android.synthetic.main.layout_show_item.view.*

class ShowAdapter : RecyclerView.Adapter<ShowAdapter.ViewHolder>() {

    private val shows = mutableListOf<Show>()
    private var selectedIndex = -1
    var listener: Listener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_show_item, parent, false)
    )

    override fun getItemCount(): Int = shows.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder.itemView) {
        val show = shows[position]

        isSelected = selectedIndex == position

        val colorRes = if (isSelected) R.color.colorAccent else R.color.color_primary_text

        txt_day.setTextColor(resources.getColor(colorRes))

        txt_day.text = show.display

        txt_value.text = show.customDate?.value

        setOnClickListener {
            selectedIndex = position
            notifyDataSetChanged()
            listener?.onClick(show)
        }

    }

    fun swapData(data: List<Show>) {
        shows.clear()
        shows.addAll(data)
        notifyDataSetChanged()
    }

    fun resetSelection() {
        selectedIndex = -1
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface Listener {
        fun onClick(show: Show)
    }
}

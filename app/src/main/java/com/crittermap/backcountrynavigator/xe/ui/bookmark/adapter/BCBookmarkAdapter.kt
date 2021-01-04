package com.crittermap.backcountrynavigator.xe.ui.bookmark.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.crittermap.backcountrynavigator.xe.R
import com.crittermap.backcountrynavigator.xe.service.bookmark.BCBookmarkData
import kotlinx.android.synthetic.main.bookmark_item.view.*

class BCBookmarkAdapter(var arrayData: List<BCBookmarkData>, var context: Context, var listener: OnBookmarkAdapterListener) : RecyclerView.Adapter<BCBookmarkAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.bookmark_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return arrayData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = arrayData[position]
        holder.tvLocation.text = data.bookmarkName
        val lat = String.format("%1$,.4f", data.lat.toDouble())
        val lon = String.format("%1$,.4f", data.lon.toDouble())
        holder.tvCoordination.text = String.format("%s , %s", lat, lon)
        holder.actionButton.setOnClickListener({
            //TODO @Trang
            Toast.makeText(context, "Action", Toast.LENGTH_SHORT).show()
        })
        holder.clickableView.setOnClickListener({
            listener.onBookmarkItemClicked(data)
        })
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val tvLocation = view.tv_location_name!!
        val tvCoordination = view.tv_coordination!!
        val actionButton = view.imageButton!!
        var clickableView = view.clickableView
    }

    interface OnBookmarkAdapterListener {
        fun onBookmarkItemClicked(bookmark: BCBookmarkData)
    }
}
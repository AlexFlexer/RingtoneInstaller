package com.axelpetprojects.ringtoner.adapters

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.axelpetprojects.ringtoner.R
import com.axelpetprojects.ringtoner.Track
import com.axelpetprojects.ringtoner.databinding.ItemTrackBinding
import com.axelpetprojects.ringtoner.layoutInflater
import java.util.*

class TracksAdapter(private val mTracks: List<Track>, private val trackClicked: (Track) -> Unit) :
    RecyclerView.Adapter<TracksAdapter.TrackHolder>() {

    class TrackHolder(val binding: ItemTrackBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        return TrackHolder(ItemTrackBinding.inflate(parent.layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        val item = mTracks[position]
        holder.binding.apply {
            imgTrack.setImageBitmap(item.cover)
            txtName.text = item.name
            txtPath.text = item.path
            txtDate.text = formatDate(holder.binding.root.context, item)
            root.setOnClickListener { trackClicked(item) }
        }
    }

    override fun getItemCount(): Int = mTracks.size

    @Suppress("deprecation")
    private fun formatDate(context: Context, item: Track): String {
        val now = Date()
        val prefix = with(item.date) {
            if (now.month != month && now.year != year) {
                "${day}.${month}.${year}"
            } else {
                when (now.day - item.date.day) {
                    -2 -> context.getString(R.string.date_tomorrow_after)
                    -1 -> context.getString(R.string.date_tomorrow)
                    0 -> context.getString(R.string.date_today)
                    1 -> context.getString(R.string.date_yesterday)
                    2 -> context.getString(R.string.date_yesterday_before)
                    else -> "${day}.${month}.${year}"
                }
            }
        }
        return "$prefix ${item.date.hours}:${item.date.minutes}"
    }
}
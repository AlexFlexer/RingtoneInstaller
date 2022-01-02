package com.axelpetprojects.ringtoner.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.axelpetprojects.ringtoner.Track
import com.axelpetprojects.ringtoner.databinding.ItemTrackBinding
import com.axelpetprojects.ringtoner.layoutInflater

class TracksAdapter(private val mTracks: List<Track>) :
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
            txtDate.text = formatDate(item)
        }
    }

    override fun getItemCount(): Int = mTracks.size

    private fun formatDate(item: Track): String {
        return ""
    }
}
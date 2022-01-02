package com.axelpetprojects.ringtoner.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.axelpetprojects.ringtoner.databinding.ItemTextBinding
import com.axelpetprojects.ringtoner.layoutInflater

class TextAdapter(val text: String?) : RecyclerView.Adapter<TextAdapter.TextHolder>() {
    class TextHolder(val binding: ItemTextBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextHolder {
        return TextHolder(
            ItemTextBinding.inflate(parent.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TextHolder, position: Int) {
        holder.binding.root.text = text
    }

    override fun getItemCount(): Int = 1
}
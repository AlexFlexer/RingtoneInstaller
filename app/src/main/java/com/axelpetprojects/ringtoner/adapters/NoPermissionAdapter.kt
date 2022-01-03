package com.axelpetprojects.ringtoner.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.axelpetprojects.ringtoner.databinding.ItemNoPermissionBinding
import com.axelpetprojects.ringtoner.layoutInflater

class NoPermissionAdapter(
    private val onBtnClick: () -> Unit
) : RecyclerView.Adapter<NoPermissionAdapter.NoPermHolder>() {
    class NoPermHolder(val binding: ItemNoPermissionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoPermHolder {
        return NoPermHolder(ItemNoPermissionBinding.inflate(parent.layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: NoPermHolder, position: Int) {
        holder.binding.btnProvide.setOnClickListener { onBtnClick() }
    }

    override fun getItemCount(): Int = 1
}
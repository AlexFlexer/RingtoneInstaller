package com.axelpetprojects.ringtoner.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.axelpetprojects.ringtoner.databinding.ItemLoadingBinding
import com.axelpetprojects.ringtoner.layoutInflater

class LoadingAdapter : RecyclerView.Adapter<LoadingAdapter.LoadingHolder>() {
    class LoadingHolder(itemLoadingBinding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(itemLoadingBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadingHolder {
        return LoadingHolder(
            ItemLoadingBinding.inflate(parent.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: LoadingHolder, position: Int) {}

    override fun getItemCount(): Int = 1
}
package com.axelpetprojects.ringtoner

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.axelpetprojects.ringtoner.adapters.LoadingAdapter
import com.axelpetprojects.ringtoner.adapters.NoPermissionAdapter
import com.axelpetprojects.ringtoner.adapters.TextAdapter
import com.axelpetprojects.ringtoner.adapters.TracksAdapter
import com.axelpetprojects.ringtoner.databinding.FragmentTracksBinding

class TracksFragment : Fragment(R.layout.fragment_tracks), SwipeRefreshLayout.OnRefreshListener {
    private val mBinding: FragmentTracksBinding by viewBinding()
    private val mViewModel: TracksViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBinding.refresherTracks.setOnRefreshListener(this)
        mViewModel.tracksData.observe(viewLifecycleOwner) { handleTracks(it) }
        mViewModel.fetchAudios(false)
    }

    override fun onRefresh() = mViewModel.fetchAudios(true)

    private fun handleTracks(event: TrackEvent) {
        mBinding.refresherTracks.isRefreshing = false
        val adapter = when (event) {
            is TrackEvent.Error -> TextAdapter(event.msg)
            is TrackEvent.Loading -> LoadingAdapter()
            is TrackEvent.NoPermission -> NoPermissionAdapter { mViewModel.fetchAudios(false) }
            is TrackEvent.Result -> TracksAdapter(event.tracks) {
                mViewModel.selectTrack(it)
                findNavController().navigate(R.id.tracks_dialog)
            }
        }
        mBinding.recyclerTracks.adapter = adapter
    }
}
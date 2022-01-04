package com.axelpetprojects.ringtoner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.axelpetprojects.ringtoner.adapters.LoadingAdapter
import com.axelpetprojects.ringtoner.adapters.NoPermissionAdapter
import com.axelpetprojects.ringtoner.adapters.TextAdapter
import com.axelpetprojects.ringtoner.adapters.TracksAdapter
import com.axelpetprojects.ringtoner.databinding.FragmentTracksBinding

@Suppress("deprecation")
class TracksFragment : Fragment(R.layout.fragment_tracks), SwipeRefreshLayout.OnRefreshListener {

    companion object {
        private const val PERMISSIONS_CODE = 1
    }

    private val mBinding: FragmentTracksBinding by viewBinding()
    private val mViewModel: TracksViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBinding.refresherTracks.setOnRefreshListener(this)
        mViewModel.tracksData.observe(viewLifecycleOwner) { handleTracks(it) }
        mViewModel.fetchAudios(true)
    }

    override fun onRefresh() = mViewModel.fetchAudios(true)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                mViewModel.fetchAudios(false)
            } else {
                Toast.makeText(requireContext(), R.string.go_to_settings, Toast.LENGTH_LONG).show()
                requireContext().openAppSettings()
            }
        }
    }

    private fun handleTracks(event: TrackEvent) {
        mBinding.refresherTracks.isRefreshing = false
        val adapter = when (event) {
            is TrackEvent.Error -> TextAdapter(event.msg)
            is TrackEvent.Loading -> LoadingAdapter()
            is TrackEvent.NoPermission -> NoPermissionAdapter {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_CODE
                )
            }
            is TrackEvent.Result -> TracksAdapter(event.tracks) {
                mViewModel.selectTrack(it)
                findNavController().navigate(R.id.tracks_dialog)
            }
        }
        mBinding.recyclerTracks.adapter = adapter
    }
}
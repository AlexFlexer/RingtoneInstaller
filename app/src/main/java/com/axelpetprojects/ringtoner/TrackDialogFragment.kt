package com.axelpetprojects.ringtoner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.axelpetprojects.ringtoner.databinding.DialogTrackBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TrackDialogFragment : BottomSheetDialogFragment(), Player.Listener {

    private val mBinding: DialogTrackBinding by viewBinding(CreateMethod.INFLATE)
    private val mViewModel: TracksViewModel by viewModels()
    private var mPlayer: ExoPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mViewModel.track.observe(viewLifecycleOwner) {
            mBinding.apply {
                imgTrack.setImageBitmap(it.cover)
                txtName.text = it.name
                txtSinger.text = it.singer
                txtAlbum.text = it.album
            }
        }
        mBinding.btnPlay.setOnClickListener {
            if (mPlayer?.isPlaying != true) {
                // apparently, we are playing track now, it's time to stop playback
                releasePlayer()
                mBinding.btnPlay.setText(R.string.track_play)
            } else {
                startPlayer()
                mBinding.btnPlay.setText(R.string.track_pause)
            }
        }
        mBinding.btnSet.setOnClickListener {
            try {
                mViewModel.setTrackAsRingtone()
                Toast.makeText(requireContext(), R.string.toast_ringtone_success, Toast.LENGTH_LONG)
                    .show()
                dismissAllowingStateLoss()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    private fun startPlayer() {
        if (mPlayer == null) {
            mPlayer = ExoPlayer.Builder(requireContext())
                .build()
        }
        mPlayer?.clearMediaItems()
        mPlayer?.addMediaItem(MediaItem.fromUri(mViewModel.getCurrentTrack()?.path.orEmpty()))
        mPlayer?.addListener(this)
        mPlayer?.play()
    }

    private fun releasePlayer() {
        mPlayer?.stop()
        mPlayer?.release()
        mPlayer = null
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        if (playbackState == Player.STATE_IDLE) {
            mBinding.btnPlay.setText(R.string.track_play)
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        val context = context
        if (context != null) {
            Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
        }
        releasePlayer()
    }
}
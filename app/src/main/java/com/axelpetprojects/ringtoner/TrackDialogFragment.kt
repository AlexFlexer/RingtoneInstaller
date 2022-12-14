package com.axelpetprojects.ringtoner

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.axelpetprojects.ringtoner.databinding.DialogTrackBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File

class TrackDialogFragment : BottomSheetDialogFragment(), Player.Listener {

    private val mBinding: DialogTrackBinding by viewBinding(CreateMethod.INFLATE)
    private val mViewModel: TracksViewModel by activityViewModels()
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
                if (it.cover == null) imgTrack.setImageResource(R.drawable.ic_music_placeholder)
                else imgTrack.setImageBitmap(it.cover)
                txtName.text = it.name
                txtSinger.text = it.singer
                txtAlbum.text = it.album
            }
        }
        mBinding.btnPlay.setOnClickListener {
            if (mPlayer?.isPlaying == true) {
                // apparently, we are playing track now, it's time to stop playback
                releasePlayer()
            } else {
                startPlayer()
                mBinding.btnPlay.setText(R.string.track_pause)
            }
        }
        mBinding.btnSet.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.System.canWrite(requireContext())) {
                    createAlertDialogWithButtonsAndMessage(
                        requireContext(),
                        R.string.attention,
                        R.string.error_cant_write_settings,
                        R.string.ok,
                        R.string.cancel,
                        onPositiveButtonClicked = {
                            context?.openChangeSettingsActivity()
                            it.dismiss()
                        },
                        onNegativeButtonClicked = { it.dismiss() }
                    ).show()
                    return@setOnClickListener
                }
            }
            try {
                mViewModel.setTrackAsRingtone()
                Toast.makeText(requireContext(), R.string.toast_ringtone_success, Toast.LENGTH_LONG)
                    .show()
                dismissAllowingStateLoss()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.stackTraceToString(), Toast.LENGTH_LONG).show()
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
        mPlayer?.addMediaItem(MediaItem.fromUri(Uri.fromFile(File(mViewModel.getCurrentTrack()?.path.orEmpty()))))
        mPlayer?.addListener(this)
        mPlayer?.playWhenReady = true
        mPlayer?.prepare()
    }

    private fun releasePlayer() {
        mPlayer?.stop()
        mPlayer?.release()
        mPlayer = null
    }

    @SuppressLint("SwitchIntDef")
    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_IDLE, Player.STATE_ENDED -> {
                mBinding.btnPlay.setText(R.string.track_play)
                releasePlayer()
            }
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        val context = context
        if (context != null) {
            Toast.makeText(context, error.stackTraceToString(), Toast.LENGTH_LONG).show()
        }
        mBinding.btnPlay.setText(R.string.track_play)
        releasePlayer()
    }
}
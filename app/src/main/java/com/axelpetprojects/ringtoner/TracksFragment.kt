package com.axelpetprojects.ringtoner

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.axelpetprojects.ringtoner.databinding.FragmentTracksBinding

class TracksFragment : Fragment(R.layout.fragment_tracks) {
    private val mBinding: FragmentTracksBinding by viewBinding()
    private val mViewModel: TracksViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mViewModel.tracksData.observe(viewLifecycleOwner) {

        }
    }
}
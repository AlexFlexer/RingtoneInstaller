package com.axelpetprojects.ringtoner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.axelpetprojects.ringtoner.databinding.DialogTrackBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TrackDialogFragment : BottomSheetDialogFragment() {
    private val mBinding: DialogTrackBinding by viewBinding(CreateMethod.INFLATE)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return mBinding.root
    }
}
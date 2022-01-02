package com.axelpetprojects.ringtoner

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class Track(
    val name: String,
    val path: String,
    val date: String,
    val singer: String,
    val album: String,
    val fileUri: Uri
)

class TracksViewModel(app: Application) : AndroidViewModel(app) {

    val tracksData = MutableLiveData<TrackEvent>()

    fun fetchAudios(isRefreshing: Boolean) {

    }
}

sealed class TrackEvent(val tracks: List<Track> = emptyList()) {
    class Loading : TrackEvent()
    class Result(tracks: List<Track>) : TrackEvent(tracks)
    class Error(val msg: String) : TrackEvent()
}
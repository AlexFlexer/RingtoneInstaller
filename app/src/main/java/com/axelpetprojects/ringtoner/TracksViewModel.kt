package com.axelpetprojects.ringtoner

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import java.time.LocalDateTime

class Track(
    val name: String,
    val path: String,
    val date: LocalDateTime,
    val singer: String,
    val album: String,
    val cover: Bitmap,
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
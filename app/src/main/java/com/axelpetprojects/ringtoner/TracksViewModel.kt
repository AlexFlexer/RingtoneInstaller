package com.axelpetprojects.ringtoner

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class Track(
    val name: String,
    val path: String,
    val date: Date,
    val singer: String,
    val album: String,
    val cover: Bitmap?
)

class TracksViewModel(app: Application) : AndroidViewModel(app) {

    val tracksData = MutableLiveData<TrackEvent>()
    val track = MutableLiveData<Track>()

    fun fetchAudios(isRefreshing: Boolean) {
        if (isRefreshing || (tracksData.value is TrackEvent.Result && tracksData.value?.tracks.isNullOrEmpty())) {
            tracksData.value = TrackEvent.Loading()
        }
        viewModelScope.launch(Dispatchers.IO) {
            tracksData.postValue(
                try {
                    TrackEvent.Result(retrieveTracks())
                } catch (e: Exception) {
                    TrackEvent.Error(e.message.orEmpty())
                }
            )
        }
    }

    fun selectTrack(track: Track) {
        this.track.value = track
    }

    fun setTrackAsRingtone() {
        val track = this.track.value ?: return
    }

    @Suppress("deprecation")
    private suspend fun retrieveTracks(): List<Track> {
        val resolver = getApplication<Application>().contentResolver
        val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val result = mutableListOf<Track>()
        val metaDataRetriever = MediaMetadataRetriever()
        resolver.query(musicUri, null, null, null, null).use { musicCursor ->
            if (musicCursor != null && musicCursor.moveToNext()) {
                val titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                val artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                val albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
                val dateModified = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED)
                val path = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                do {
                    val extractedPath = musicCursor.getString(path)
                    result.add(
                        Track(
                            musicCursor.getString(titleColumn),
                            extractedPath,
                            Date(musicCursor.getLong(dateModified)),
                            musicCursor.getString(artistColumn),
                            musicCursor.getString(albumColumn),
                            with(metaDataRetriever) {
                                setDataSource(extractedPath)
                                val bytes = embeddedPicture
                                if (bytes == null) null
                                else BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            }
                        )
                    )
                } while (musicCursor.moveToNext())
            }
            return result
        }
    }
}

sealed class TrackEvent(val tracks: List<Track> = emptyList()) {
    class Loading : TrackEvent()
    class Result(tracks: List<Track>) : TrackEvent(tracks)
    class Error(val msg: String) : TrackEvent()
}
package com.axelpetprojects.ringtoner

import android.Manifest
import android.app.Application
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.RingtoneManager
import android.os.Build
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
) {
    @Suppress("deprecation")
    fun toContentValues(): ContentValues {
        return ContentValues().apply {
            put(MediaStore.MediaColumns.DATA, path)
            put(MediaStore.MediaColumns.TITLE, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3")
            put(MediaStore.Audio.Media.ARTIST, singer)
            put(MediaStore.Audio.Media.IS_RINGTONE, true)
            put(MediaStore.Audio.Media.IS_NOTIFICATION, false)
            put(MediaStore.Audio.Media.IS_ALARM, false)
            put(MediaStore.Audio.Media.IS_MUSIC, false)
        }
    }
}

class TracksViewModel(app: Application) : AndroidViewModel(app) {

    val tracksData = MutableLiveData<TrackEvent>()
    val track = MutableLiveData<Track>()

    fun fetchAudios(isRefreshing: Boolean) {
        val context = getApplication<Application>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                tracksData.value = TrackEvent.NoPermission()
                return
            }
        }
        if (isRefreshing || (tracksData.value is TrackEvent.Result && tracksData.value?.tracks.isNullOrEmpty())) {
            tracksData.value = TrackEvent.Loading()
        }
        viewModelScope.launch(Dispatchers.IO) {
            tracksData.postValue(
                try {
                    TrackEvent.Result(retrieveTracks())
                } catch (e: Exception) {
                    TrackEvent.Error(e.stackTraceToString())
                }
            )
        }
    }

    fun selectTrack(track: Track) {
        this.track.value = track
    }

    fun getCurrentTrack(): Track? = track.value

    fun setTrackAsRingtone() {
        val track = this.track.value ?: return
        val uri = MediaStore.Audio.Media.getContentUriForPath(track.path)
        RingtoneManager.setActualDefaultRingtoneUri(
            getApplication(),
            RingtoneManager.TYPE_RINGTONE,
            uri
        )
    }

    @Suppress("deprecation", "RedundantSuspendModifier")
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
                    val extractedPath = musicCursor.getString(path).orEmpty()
                    result.add(
                        Track(
                            musicCursor.getString(titleColumn),
                            extractedPath,
                            Date(musicCursor.getLong(dateModified)),
                            musicCursor.getString(artistColumn),
                            musicCursor.getString(albumColumn),
                            with(metaDataRetriever) {
                                try {
                                    setDataSource(extractedPath)
                                    val bytes = embeddedPicture
                                    if (bytes == null) null
                                    else BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                } catch (e: Exception) {
                                    null
                                }
                            }
                        )
                    )
                } while (musicCursor.moveToNext())
            }
            return result.also { it.sortByDescending { track -> track.date } }
        }
    }
}

sealed class TrackEvent(val tracks: List<Track> = emptyList()) {
    class Loading : TrackEvent()
    class Result(tracks: List<Track>) : TrackEvent(tracks)
    class Error(val msg: String) : TrackEvent()
    class NoPermission() : TrackEvent()
}
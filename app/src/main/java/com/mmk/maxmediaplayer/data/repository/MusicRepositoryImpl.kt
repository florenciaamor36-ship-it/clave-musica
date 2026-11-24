package com.mmk.maxmediaplayer.data.repository

import android.content.Context
import android.net.Uri
import com.mmk.maxmediaplayer.data.local.dao.TrackDao
import com.mmk.maxmediaplayer.data.mapper.TrackMapper
import com.mmk.maxmediaplayer.data.remote.api.InternetArchiveApi
import com.mmk.maxmediaplayer.data.remote.dto.TrackDto
import com.mmk.maxmediaplayer.domain.model.Playlist
import com.mmk.maxmediaplayer.domain.model.Track
import com.mmk.maxmediaplayer.domain.repository.MusicRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: InternetArchiveApi,
    private val trackDao: TrackDao
) : MusicRepository {

    private val baseQuery = "mediatype:audio AND collection:opensource_audio AND licenseurl:*creativecommons* AND format:MP3 AND subject:music"
    private val fields = listOf("identifier", "title", "creator", "licenseurl")

    override suspend fun getAllTracksOnce(): List<Track> {
        val cached = trackDao.getAllTracksOnce().map(TrackMapper::toDomain)
        return cached.ifEmpty { fetchTracksFromNetwork() }
    }

    override suspend fun getTracksPaginated(page: Int, size: Int): List<Track> = searchTracks("", page, size)

    override suspend fun searchTracks(query: String, page: Int, size: Int): List<Track> {
        val safe = query.trim().filter { it.isLetterOrDigit() || it.isWhitespace() || it == '-' || it == '_' }.trim()
        val q = if (safe.isBlank()) baseQuery else "$baseQuery AND (title:($safe) OR creator:($safe) OR subject:($safe))"
        val response = api.searchTracks(q, fields, size.coerceIn(1, 100), page + 1)
        if (!response.isSuccessful) throw IllegalStateException("No se pudo cargar el catálogo (${response.code()})")
        val tracks = response.body()?.response?.docs.orEmpty()
            .filter { it.id.isNotBlank() }
            .map(TrackMapper::toDomain)
        if (page == 0 && safe.isBlank() && tracks.isNotEmpty()) {
            trackDao.insertAll(tracks.map(TrackMapper::toEntity))
        }
        return tracks
    }

    override suspend fun getNextTrack(currentTrackId: String): Track? {
        val all = getAllTracksOnce(); val idx = all.indexOfFirst { it.id == currentTrackId }
        return all.getOrNull(idx + 1)
    }

    override suspend fun getPreviousTrack(currentTrackId: String): Track? {
        val all = getAllTracksOnce(); val idx = all.indexOfFirst { it.id == currentTrackId }
        return if (idx > 0) all.getOrNull(idx - 1) else null
    }

    override suspend fun addToRecentPlays(track: Track) {
        trackDao.upsertTrack(TrackMapper.toEntity(track.copy(lastPlayed = System.currentTimeMillis())))
    }

    override suspend fun getRecentPlays(): List<Track> = trackDao.getRecentPlays().map(TrackMapper::toDomain)

    override suspend fun getFeaturedPlaylists(): List<Playlist> = emptyList()

    override suspend fun getTrackUrl(trackId: String): String {
        val metadata = api.getMetadata("https://archive.org/metadata/${Uri.encode(trackId)}")
        if (!metadata.isSuccessful) throw IllegalStateException("No se encontró el audio en Internet Archive")
        val files = metadata.body()?.files.orEmpty()
        val mp3 = files.firstOrNull { it.format.contains("mp3", true) && it.source == "original" }
            ?: files.firstOrNull { it.format.contains("mp3", true) }
            ?: throw IllegalStateException("Este elemento no tiene un MP3 reproducible")
        val name = URLEncoder.encode(mp3.name, StandardCharsets.UTF_8.name()).replace("+", "%20")
        return "https://archive.org/download/${Uri.encode(trackId)}/$name"
    }

    override suspend fun fetchTracksFromNetwork(): List<Track> = searchTracks("", 0, 50)

    override suspend fun getCachedTracks(): Flow<List<Track>> = trackDao.getAllTracks().map { rows -> rows.map(TrackMapper::toDomain) }

    override suspend fun getTracks(): List<Track> = fetchTracksFromNetwork()

    override suspend fun getTrackById(trackId: String): Track? = trackDao.getTrackById(trackId)?.let(TrackMapper::toDomain)

    override suspend fun toggleFavorite(trackId: String) = trackDao.toggleFavorite(trackId)

    override fun getFavoriteTracks(): Flow<List<Track>> = trackDao.getFavorites().map { rows -> rows.map(TrackMapper::toDomain) }
}

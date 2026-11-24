package com.mmk.maxmediaplayer.data.mapper

import com.mmk.maxmediaplayer.data.local.entity.TrackEntity
import com.mmk.maxmediaplayer.data.remote.dto.PlaylistDto
import com.mmk.maxmediaplayer.data.remote.dto.TrackDto
import com.mmk.maxmediaplayer.domain.model.Playlist
import com.mmk.maxmediaplayer.domain.model.Track

object TrackMapper {
    fun toDomain(dto: TrackDto): Track = Track(
        id = dto.id,
        title = dto.title?.takeIf { it.isNotBlank() } ?: "Sin título",
        artist = dto.artistName(),
        duration = 0L,
        audioUrl = "",
        imageUrl = "https://archive.org/services/img/${dto.id}"
    )

    fun toDto(domain: Track): TrackDto = TrackDto(
        id = domain.id,
        title = domain.title,
        creator = null,
        licenseUrl = null
    )

    fun toEntity(domain: Track) = TrackEntity(
        id = domain.id,
        title = domain.title,
        artist = domain.artist,
        duration = domain.duration,
        audioUrl = domain.audioUrl,
        imageUrl = domain.imageUrl,
        lastPlayed = domain.lastPlayed
    )

    fun toDomain(entity: TrackEntity) = Track(
        id = entity.id,
        title = entity.title,
        artist = entity.artist,
        duration = entity.duration,
        audioUrl = entity.audioUrl,
        imageUrl = entity.imageUrl,
        lastPlayed = entity.lastPlayed,
        isFavorite = entity.isFavorite
    )

    fun toDomain(dto: PlaylistDto): Playlist = Playlist(
        id = dto.id,
        title = dto.name,
        coverArtUrl = dto.imageUrl,
        trackCount = dto.trackCount
    )
}

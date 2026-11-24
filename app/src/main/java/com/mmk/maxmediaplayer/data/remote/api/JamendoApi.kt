package com.mmk.maxmediaplayer.data.remote.api

import com.mmk.maxmediaplayer.data.remote.dto.ArchiveMetadataDto
import com.mmk.maxmediaplayer.data.remote.dto.ArchiveSearchResponse
import com.mmk.maxmediaplayer.data.remote.dto.TrackDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/** Internet Archive's public search and metadata endpoints. */
interface InternetArchiveApi {
    @GET("advancedsearch.php")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("fl[]") fields: List<String>,
        @Query("rows") rows: Int,
        @Query("page") page: Int,
        @Query("output") output: String = "json"
    ): Response<ArchiveSearchResponse>

    @GET
    suspend fun getMetadata(@Url url: String): Response<ArchiveMetadataDto>
}

package com.mmk.maxmediaplayer.data.remote.api

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
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

data class ArchiveSearchResponse(@SerializedName("response") val response: ArchiveSearchResults? = null)
data class ArchiveSearchResults(@SerializedName("docs") val docs: List<TrackDto> = emptyList())

data class TrackDto(
    @SerializedName("identifier") val id: String = "",
    @SerializedName("title") val title: String? = null,
    @SerializedName("creator") val creator: JsonElement? = null,
    @SerializedName("licenseurl") val licenseUrl: String? = null
) {
    fun artistName(): String = when {
        creator == null || creator.isJsonNull -> "Artista independiente"
        creator.isJsonArray -> creator.asJsonArray.joinToString(", ") { it.asString }
        creator.isJsonPrimitive -> creator.asString
        else -> "Artista independiente"
    }
}

data class ArchiveMetadataDto(@SerializedName("files") val files: List<ArchiveFileDto> = emptyList())
data class ArchiveFileDto(
    @SerializedName("name") val name: String = "",
    @SerializedName("format") val format: String = "",
    @SerializedName("length") val length: String? = null,
    @SerializedName("source") val source: String? = null
)

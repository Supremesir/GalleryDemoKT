package com.supremesir.gallerydemokt

import com.google.gson.annotations.SerializedName

/**
 * @author HaoFan Fang
 * @date 2020/4/23 16:15
 */

data class Pixabay(
    val total: Int,
    val totalHits: Int,
    val hits: Array<PhotoItem>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Pixabay

        if (total != other.total) return false
        if (totalHits != other.totalHits) return false
        if (!hits.contentEquals(other.hits)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = total
        result = 31 * result + totalHits
        result = 31 * result + hits.contentHashCode()
        return result
    }
}

data class PhotoItem(
    // 使用 SerializedName 规范化命名
    @SerializedName("id") val photoId: Int,
    @SerializedName("largeImageURL") val fullUrl: String,
    @SerializedName("webformatURL") val previewUrl: String
)
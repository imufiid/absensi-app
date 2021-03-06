package com.skripsi.absensi_app.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class WrappedListResponse<T>(
    @SerializedName("meta") val meta: MetaResponse? = null,
    @SerializedName("data") val data: List<T>? = null
)


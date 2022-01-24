package com.themarkettheory.user.model


import com.google.gson.annotations.SerializedName

data class PopularLocation(
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("name")
    val name: String
)
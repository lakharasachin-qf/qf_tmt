package com.themarkettheory.user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetFavoriteServicesResponse {

    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: ArrayList<FavoriteServicesDatum>? = null
}

class FavoriteServicesDatum {
    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("area")
    @Expose
    var area: String? = null

    @SerializedName("category_name")
    @Expose
    var category_name: String? = null

    @SerializedName("latitude")
    @Expose
    var latitude: String? = null

    @SerializedName("base_url")
    @Expose
    var baseUrl: String? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("category_id")
    @Expose
    var category_id: Int? = null

    @SerializedName("longitude")
    @Expose
    var longitude: String? = null

    @SerializedName("rating")
    @Expose
    var rating: String? = null

    @SerializedName("total_rating")
    @Expose
    var totalRating: String? = null
}

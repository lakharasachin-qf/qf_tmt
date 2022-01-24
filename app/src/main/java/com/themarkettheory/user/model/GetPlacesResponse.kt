package com.themarkettheory.user.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class GetPlacesResponse {

    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: GetPlacesData? = null
}

class GetPlacesData {
    @SerializedName("list")
    @Expose
    var list: List<RecentList>? = null

    @SerializedName("popular_places")
    @Expose
    var popularPlaces: List<PopularPlace>? = null
}

class RecentList {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null

    @SerializedName("search_string")
    @Expose
    var searchString: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null
}

class PopularPlace {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("category_id")
    @Expose
    var category_id: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("address")
    @Expose
    var address: String? = null
}

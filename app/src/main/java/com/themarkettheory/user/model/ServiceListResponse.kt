package com.themarkettheory.user.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class ServiceListResponse {

    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: ServiceData? = null
}

class ServiceData {
    @SerializedName("list")
    @Expose
    var list: List<ServiceList>? = null

    @SerializedName("page")
    @Expose
    var page: Int? = null
}

class ServiceList {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("latitude")
    @Expose
    var latitude: String? = null

    @SerializedName("longitude")
    @Expose
    var longitude: String? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("is_recommanded")
    @Expose
    var is_recommanded: Int? = null

    @SerializedName("is_deleted")
    @Expose
    var isDeleted: Int? = null

    @SerializedName("category_id")
    @Expose
    var categoryId: Int? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("rating")
    @Expose
    var rating: String? = null

    @SerializedName("total_rating")
    @Expose
    var totalRating: String? = null

    @SerializedName("distance")
    @Expose
    var distance: String? = null

    @SerializedName("favourites")
    @Expose
    var favourites: Int? = null

    @SerializedName("sub_category_id")
    @Expose
    var sub_category_id: Int? = null

    var distanceNumeric: Float? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("category_name")
    @Expose
    var category_name: String? = null

    @SerializedName("base_url")
    @Expose
    var baseUrl: String? = null

    @SerializedName("is_open")
    @Expose
    var isOpen: Int? = null

    @SerializedName("cuisine_types")
    @Expose
    var cuisineTypes: List<CuisineTypes>? = null
}

class CuisineTypes {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("is_deleted")
    @Expose
    var is_deleted: Int? = null

    @SerializedName("created_at")
    @Expose
    var created_at: String? = null

    @SerializedName("updated_at")
    @Expose
    var updated_at: String? = null

    @SerializedName("is_selected")
    @Expose
    var is_selected: Int? = null

    override fun toString(): String {
        return name.toString()
    }
}

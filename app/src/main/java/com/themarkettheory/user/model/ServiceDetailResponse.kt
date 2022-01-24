package com.themarkettheory.user.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import com.themarkettheory.user.newmodels.home.Offer
import java.io.Serializable


class ServiceDetailResponse : Serializable {
    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: ServiceDetail? = null
}

class ServiceDetail : Serializable {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    /* @SerializedName("tags")
     @Expose
     var tags: String? = null*/

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("cross_promotions")
    @Expose
    var cross_promotions: List<CrossPromotion>? = null

    @SerializedName("mobile")
    @Expose
    var phone: String? = null

    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("opening_time")
    @Expose
    var openingTime: List<OpeningTime>? = null

    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("latitude")
    @Expose
    var latitude: String? = null

    @SerializedName("is_live_deals")
    @Expose
    var is_live_deals: Int? = null

    @SerializedName("longitude")
    @Expose
    var longitude: String? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

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

    @SerializedName("facebook_url")
    val facebookURL: String? = null

    @SerializedName("twitter_url")
    val twitterURL: String? = null

    @SerializedName("google_url")
    val googleURL: String? = null

    @SerializedName("instagram_url")
    val instagramURL: String? = null

    @SerializedName("total_rating")
    @Expose
    var totalRating: String? = null

    @SerializedName("distance")
    @Expose
    var distance: String? = null

    @SerializedName("is_liked")
    @Expose
    var is_liked: Int? = null

    @SerializedName("images")
    @Expose
    var images: ArrayList<Image>? = null

    @SerializedName("tags")
    @Expose
    var tags: List<VendorTag>? = null

    @SerializedName("amenities")
    @Expose
    var amenities: List<Amenities>? = null

    @SerializedName("base_url")
    @Expose
    var baseUrl: String? = null

    @SerializedName("privacy_document")
    @Expose
    var privacy_document: String? = null

    @SerializedName("base_url_offer")
    @Expose
    var baseUrlOffer: String? = null

    @SerializedName("is_open")
    @Expose
    var isOpen: Int? = null

    @SerializedName("points")
    @Expose
    var points: Int? = null

    @SerializedName("offers")
    @Expose
    var offers: List<Offer>? = null

    @SerializedName("favourites")
    @Expose
    var favourites: Int? = null

    @SerializedName("currency")
    @Expose
    var currency: String? = null
}

class Image : Serializable {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("service_id")
    @Expose
    var serviceId: Int? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("position")
    @Expose
    var position: Int? = null
}

class VendorTag : Serializable {
    @SerializedName("id")
    @Expose
    val id: Int? = null

    @SerializedName("name")
    @Expose
    val name: String? = null

    @SerializedName("created_at")
    @Expose
    val createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    val updatedAt: String? = null

}

class Amenities : Serializable {
    @SerializedName("id")
    @Expose
    val id: Int? = null

    @SerializedName("service_id")
    @Expose
    val serviceId: Int? = null

    @SerializedName("name")
    @Expose
    val name: String? = null

    @SerializedName("image")
    @Expose
    val image: String? = null

    @SerializedName("created_at")
    @Expose
    val createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    val updatedAt: String? = null

}


data class OpeningTime(
    val day: String? = null,
    val hours: List<Hour>? = null
) : Serializable


data class Hour(
    val time: String? = null
) : Serializable

class CrossPromotion : Serializable {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("category_id")
    @Expose
    var categoryId: Int? = null

    @SerializedName("service_id")
    @Expose
    var serviceId: Int? = null

    @SerializedName("promotion_service_id")
    @Expose
    var promotionServiceId: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null
}

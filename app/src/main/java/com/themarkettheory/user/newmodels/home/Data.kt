package com.themarkettheory.user.newmodels.home


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("slider")
    var slider: List<Slider>? = listOf(),
    @SerializedName("categories")
    var categories: List<Category>? = listOf(),
    @SerializedName("base_url_slider")
    var baseUrlSlider: String? = "",
    @SerializedName("restaurants")
    var restaurants: List<Restaurant>? = listOf(),
    @SerializedName("restaurants_base_url")
    var restaurantsBaseUrl: String? = "",
    @SerializedName("notification_count")
    var notificationCount: Int? = 0,
    @SerializedName("nearby")
    var nearby: List<Nearby>? = listOf(),
    @SerializedName("nearby_base_url")
    var nearbyBaseUrl: String? = "",
    @SerializedName("offers")
    var offers: List<Offer>? = listOf(),
    @SerializedName("offer_base_url")
    var offerBaseUrl: String? = "",
    @SerializedName("popular_locations")
    var popularLocations: List<PopularLocation>? = listOf(),
    @SerializedName("popular_locations_url")
    var popularLocationsUrl: String? = "",
    @SerializedName("cart_count")
    var cartCount: Int? = 0,
    @SerializedName("report_service_questions")
    var report_service_questions: List<String>?= listOf()
)
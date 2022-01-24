package com.themarkettheory.user.newmodels.searchrestaurant


import com.google.gson.annotations.SerializedName

data class SearchRestaurantData(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("title")
    var title: String? = "",
    @SerializedName("description")
    var description: String? = "",
    @SerializedName("address")
    var address: String? = "",
    @SerializedName("latitude")
    var latitude: String? = "",
    @SerializedName("longitude")
    var longitude: String? = "",
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("is_deleted")
    var isDeleted: Int? = 0,
    @SerializedName("category_id")
    var categoryId: Int? = 0,
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("updated_at")
    var updatedAt: String? = "",
    @SerializedName("rating")
    var rating: String? = "",
    @SerializedName("total_rating")
    var totalRating: String? = "",
    @SerializedName("area")
    var area: String? = "",
    @SerializedName("currency")
    var currency: String? = "",
    @SerializedName("currency_str")
    var currencyStr: String? = "",
    @SerializedName("facebook_url")
    var facebookUrl: String? = "",
    @SerializedName("twitter_url")
    var twitterUrl: String? = "",
    @SerializedName("google_url")
    var googleUrl: String? = "",
    @SerializedName("instagram_url")
    var instagramUrl: String? = "",
    @SerializedName("sub_category_id")
    var subCategoryId: Int? = 0,
    @SerializedName("email")
    var email: String? = "",
    @SerializedName("mobile")
    var mobile: String? = "",
    @SerializedName("is_recommanded")
    var isRecommanded: Int? = 0,
    @SerializedName("youtube_url")
    var youtubeUrl: String? = "",
    @SerializedName("cuisine_types")
    var cuisineTypes: List<CuisineType>? = listOf(),
    @SerializedName("street")
    var street: String? = "",
    @SerializedName("town")
    var town: String? = "",
    @SerializedName("state")
    var state: String? = "",
    @SerializedName("gst_no")
    var gstNo: String? = "",
    @SerializedName("device_system")
    var deviceSystem: String? = "",
    @SerializedName("advertise")
    var advertise: String? = "",
    @SerializedName("is_owner")
    var isOwner: Int? = 0,
    @SerializedName("owner_name")
    var ownerName: String? = "",
    @SerializedName("is_service")
    var isService: Int? = 0,
    @SerializedName("is_veg")
    var isVeg: Int? = 0,
    @SerializedName("is_non_veg")
    var isNonVeg: Int? = 0,
    @SerializedName("is_egg")
    var isEgg: Int? = 0,
    @SerializedName("service_cost")
    var serviceCost: String? = "",
    @SerializedName("service_name")
    var serviceName: String? = "",
    @SerializedName("other_option")
    var otherOption: String? = "",
    @SerializedName("is_instering")
    var isInstering: Int? = 0,
    @SerializedName("notes")
    var notes: String? = "",
    @SerializedName("submit_by")
    var submitBy: String? = "",
    @SerializedName("is_market_vendor")
    var isMarketVendor: Int? = 0,
    @SerializedName("submit_date")
    var submitDate: String? = "",
    @SerializedName("service_type")
    var serviceType: String? = "",
    @SerializedName("is_table_booking")
    var isTableBooking: Int? = 0,
    @SerializedName("distance")
    var distance: String? = "",
    @SerializedName("is_live_deal")
    var isLiveDeal: Boolean? = false,
    @SerializedName("favourite")
    var search_favourite: Int? = 0,
    @SerializedName("is_favourite")
    var isFavourite: Int? = 0,
    @SerializedName("image")
    var image: String? = "",
    @SerializedName("base_url")
    var baseUrl: String? = "",
    @SerializedName("is_open")
    var isOpen: Int? = 0,
    @SerializedName("category_name")
    var categoryName: String? = ""
)
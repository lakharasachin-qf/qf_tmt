package com.themarkettheory.user.newmodels.overview


import com.google.gson.annotations.SerializedName
import com.themarkettheory.user.newmodels.home.Offer

data class Data(
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
    var gstNo: Any? = Any(),
    @SerializedName("device_system")
    var deviceSystem: String? = "",
    @SerializedName("advertise")
    var advertise: Any? = Any(),
    @SerializedName("is_owner")
    var isOwner: Int? = 0,
    @SerializedName("owner_name")
    var ownerName: Any? = Any(),
    @SerializedName("is_service")
    var isService: Int? = 0,
    @SerializedName("is_veg")
    var isVeg: Int? = 0,
    @SerializedName("is_non_veg")
    var isNonVeg: Int? = 0,
    @SerializedName("is_egg")
    var isEgg: Int? = 0,
    @SerializedName("service_cost")
    var serviceCost: Any? = Any(),
    @SerializedName("service_name")
    var serviceName: Any? = Any(),
    @SerializedName("other_option")
    var otherOption: Any? = Any(),
    @SerializedName("is_instering")
    var isInstering: Int? = 0,
    @SerializedName("notes")
    var notes: Any? = Any(),
    @SerializedName("submit_by")
    var submitBy: Any? = Any(),
    @SerializedName("is_market_vendor")
    var isMarketVendor: Int? = 0,
    @SerializedName("submit_date")
    var submitDate: Any? = Any(),
    @SerializedName("is_table_booking")
    var isTableBooking: Int? = 0,
    @SerializedName("distance")
    var distance: String? = "",
    @SerializedName("privacy_policy_url")
    var privacyPolicyUrl: String? = "",
    @SerializedName("image")
    var image: List<Image>? = listOf(),
    @SerializedName("is_live_deal")
    var isLiveDeal: Boolean? = false,
    @SerializedName("stamp_available")
    var stampAvailable: Int? = 0,
    @SerializedName("base_url")
    var baseUrl: String? = "",
    @SerializedName("base_url_offer")
    var baseUrlOffer: String? = "",
    @SerializedName("open_at")
    var openAt: String? = "",
    @SerializedName("is_open")
    var isOpen: Int? = 0,
    @SerializedName("points")
    var points: String? = "",
    @SerializedName("category_name")
    var categoryName: String? = "",
    @SerializedName("cross_promotions")
    var crossPromotions: List<CrossPromotion>? = listOf(),
    @SerializedName("offers")
    var offers: List<Offer>? = listOf(),
    @SerializedName("favourite")
    var overviewfavourite: Int? = 0,
    @SerializedName("is_favourite")
    var isFavourite: Int? = 0,
    @SerializedName("is_liked")
    var isLiked: Int? = 0,
    @SerializedName("tags")
    var tags: List<Tag>? = listOf(),
    @SerializedName("occasion")
    var occasion: List<Occasion>? = listOf(),
    @SerializedName("amenities")
    var amenities: List<Amenity>? = listOf(),
    @SerializedName("opening_time")
    var openingTime: List<OpeningTime>? = listOf(),
    @SerializedName("tax")
    var tax: List<Tax>? = listOf()
)
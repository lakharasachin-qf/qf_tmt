package com.themarkettheory.user.newmodels.bucketcart


import com.google.gson.annotations.SerializedName

data class ServiceDetails(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("title")
    var title: String?,
    @SerializedName("description")
    var description: String?,
    @SerializedName("address")
    var address: String?,
    @SerializedName("latitude")
    var latitude: String?,
    @SerializedName("longitude")
    var longitude: String?,
    @SerializedName("status")
    var status: Int?,
    @SerializedName("is_deleted")
    var isDeleted: Int?,
    @SerializedName("category_id")
    var categoryId: Int?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("updated_at")
    var updatedAt: String?,
    @SerializedName("rating")
    var rating: String?,
    @SerializedName("total_rating")
    var totalRating: String?,
    @SerializedName("area")
    var area: String?,
    @SerializedName("currency")
    var currency: String?,
    @SerializedName("currency_str")
    var currencyStr: String?,
    @SerializedName("facebook_url")
    var facebookUrl: String?,
    @SerializedName("twitter_url")
    var twitterUrl: String?,
    @SerializedName("google_url")
    var googleUrl: String?,
    @SerializedName("instagram_url")
    var instagramUrl: String?,
    @SerializedName("sub_category_id")
    var subCategoryId: Int?,
    @SerializedName("email")
    var email: String?,
    @SerializedName("mobile")
    var mobile: String?,
    @SerializedName("is_recommanded")
    var isRecommanded: Int?,
    @SerializedName("youtube_url")
    var youtubeUrl: String?,
    @SerializedName("cuisine_types")
    var cuisineTypes: List<CuisineType>?,
    @SerializedName("street")
    var street: String?,
    @SerializedName("town")
    var town: String?,
    @SerializedName("state")
    var state: String?,
    @SerializedName("gst_no")
    var gstNo: Any?,
    @SerializedName("device_system")
    var deviceSystem: String?,
    @SerializedName("advertise")
    var advertise: Any?,
    @SerializedName("is_owner")
    var isOwner: Int?,
    @SerializedName("owner_name")
    var ownerName: Any?,
    @SerializedName("is_service")
    var isService: Int?,
    @SerializedName("is_veg")
    var isVeg: Int?,
    @SerializedName("is_non_veg")
    var isNonVeg: Int?,
    @SerializedName("is_egg")
    var isEgg: Int?,
    @SerializedName("service_cost")
    var serviceCost: Any?,
    @SerializedName("service_name")
    var serviceName: Any?,
    @SerializedName("other_option")
    var otherOption: Any?,
    @SerializedName("is_instering")
    var isInstering: Int?,
    @SerializedName("notes")
    var notes: Any?,
    @SerializedName("submit_by")
    var submitBy: Any?,
    @SerializedName("is_market_vendor")
    var isMarketVendor: Int?,
    @SerializedName("submit_date")
    var submitDate: Any?,
    @SerializedName("distance")
    var distance: String?,
    @SerializedName("privacy_policy_url")
    var privacyPolicyUrl: String?,
    @SerializedName("image")
    var image: List<Image>?,
    @SerializedName("is_live_deal")
    var isLiveDeal: Boolean?,
    @SerializedName("stamp_available")
    var stampAvailable: Int?,
    @SerializedName("base_url")
    var baseUrl: String?,
    @SerializedName("base_url_offer")
    var baseUrlOffer: String?,
    @SerializedName("open_at")
    var openAt: String?,
    @SerializedName("is_open")
    var isOpen: Int?,
    @SerializedName("points")
    var points: Int?,
    @SerializedName("category_name")
    var categoryName: String?,
    @SerializedName("cross_promotions")
    var crossPromotions: List<CrossPromotion>?,
    @SerializedName("offers")
    var offers: List<Offer>?,
    @SerializedName("favourite")
    var favourite: Int?,
    @SerializedName("is_favourite")
    var isCartFavourite: Int?,
    @SerializedName("is_liked")
    var isLiked: Int?,
    @SerializedName("tags")
    var tags: List<Tag>?,
    @SerializedName("occasion")
    var occasion: List<Occasion>?,
    @SerializedName("amenities")
    var amenities: List<Amenity>?,
    @SerializedName("opening_time")
    var openingTime: List<OpeningTime>?
)
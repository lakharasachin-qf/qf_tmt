package com.themarkettheory.user.newmodels.mytablebookings

data class ServiceDetails(
    var id: Int?,
    var title: String?,
    var category_id: Int?,
    var image: String?,
    var address: String?,
    var latitude: String?,
    var longitude: String?,
    var rating: String?,
    var total_rating: String?,
    var is_veg: Int?,
    var is_non_veg: Int?,
    var is_egg: Int?
)
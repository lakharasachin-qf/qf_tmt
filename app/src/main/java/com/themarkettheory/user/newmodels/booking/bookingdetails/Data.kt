package com.themarkettheory.user.newmodels.booking.bookingdetails

data class Data(
    var id: Int?,
    var booking_date: String?,
    var booking_time: String?,
    var total_person: Int?,
    var adult: Int?,
    var child: Int?,
    var special_request: String?,
    var occasion_id: String?,
    var occasion_name: String?,
    var status: Int?,
    var table_id: Int?,
    var user_id: Int?,
    var service_id: Int?,
    var created_at: String?,
    var updated_at: String?,
    var specialist_id: Int?,
    var order_id: Int?,
    var otp: String?,
    var category_name: String?,
    var specialist: Specialist?,
    var title: String?,
    var address: String?,
    var latitude: String?,
    var base_url: String?,
    var image: String?,
    var longitude: String?,
    var rating: String?,
    var total_rating: String?,
    var is_reviewed: Int?,
    var services: List<Any>?
)
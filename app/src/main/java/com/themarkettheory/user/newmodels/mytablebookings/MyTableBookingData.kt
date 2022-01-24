package com.themarkettheory.user.newmodels.mytablebookings

data class MyTableBookingData(
    var id: Int?,
    var booking_date: String?,
    var booking_time: String?,
    var total_person: Int?,
    var adult: Int?,
    var child: Int?,
    var special_request: String?,
    var occasion_id: Int?,
    var status: Int?,
    var table_id: Int?,
    var user_id: Int?,
    var service_id: Int?,
    var created_at: String?,
    var updated_at: String?,
    var specialist_id: Int?,
    var order_details: OrderDetails?,
    var occasion_name: String?,
    var type: String?,
    var cart: List<Cart>?,
    var service_details: ServiceDetails?
)
package com.themarkettheory.user.model

import java.io.Serializable

class SalonBookingModel : Serializable {
    var serviceId: String = ""
    var subtotal: Float = 0.0f
    var tax_amount: Float = 0.0f
    var tax: Float = 0.0f
    var discount_type: Float = 0.0f
    var discount: Float = 0.0f
    var total: Float = 0.0f
    var totalPoints: Int = 0
    var payment_id: String = ""
    var offer_id: String = ""

    //    var services : JSONArray = JSONArray()
    var specialist_id: String = ""
    var specialist_name: String = ""
    var specialist_image: String = ""
    var serviceName: String? = ""
    var location: String? = ""
    var date: String? = ""
    var time: String? = ""
    var total_person: Int = 0
    var adult: String = ""
    var child: String = ""
    var occasion_id: String = ""
    var special_instruction: String = ""
//    var serviceList : ArrayList<SaloonSpaList> = ArrayList()
}
package com.themarkettheory.user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SaloonSpaServicesResponse : Serializable {
    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: SaloonSpaServiceData? = null

}

class SaloonSpaServiceData : Serializable {
    @SerializedName("list")
    @Expose
    var list: List<SaloonSpaList>? = null

    @SerializedName("combo")
    @Expose
    var combo: List<SaloonSpaList>? = null

    @SerializedName("services_base_url")
    @Expose
    var servicesBaseUrl: String? = null
}

class SaloonSpaCombo : Serializable {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("service_id")
    @Expose
    var serviceId: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("mrp_price")
    @Expose
    var mrpPrice: String? = null

    @SerializedName("discounted_price")
    @Expose
    var discountedPrice: String? = null

    @SerializedName("gender")
    @Expose
    var gender: Int? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("is_combo")
    @Expose
    var isCombo: Int? = null
}

class SaloonSpaList : Serializable {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("service_id")
    @Expose
    var serviceId: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("currency")
    @Expose
    var currency: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("mrp_price")
    @Expose
    var mrpPrice: String? = null

    var minAmount: Float? = null

    @SerializedName("discounted_price")
    @Expose
    var discountedPrice: String? = null

    @SerializedName("point")
    @Expose
    var points: String? = null

    @SerializedName("gender")
    @Expose
    var gender: Int? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("is_combo")
    @Expose
    var isCombo: Int? = null
    var isSelected: Boolean = false
}

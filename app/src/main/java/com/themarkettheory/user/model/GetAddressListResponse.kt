package com.themarkettheory.user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GetAddressListResponse : Serializable {
    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: GetAddressData? = null
}

class GetAddressData : Serializable {
    @SerializedName("list")
    @Expose
    var list: List<AddressList>? = null
}

class AddressList : Serializable {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null

    @SerializedName("google_address")
    @Expose
    var googleAddress: String? = null

    @SerializedName("house_number")
    @Expose
    var houseNumber: String? = null

    @SerializedName("floor")
    @Expose
    var floor: String? = null

    @SerializedName("tower")
    @Expose
    var tower: String? = null

    @SerializedName("address_optional")
    @Expose
    var addressOptional: String? = null

    @SerializedName("type")
    @Expose
    var type: Int? = null

    @SerializedName("latitude")
    @Expose
    var latitude: String? = null

    @SerializedName("longitude")
    @Expose
    var longitude: String? = null

    @SerializedName("is_default")
    @Expose
    var isDefault: Int? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: Any? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: Any? = null
}
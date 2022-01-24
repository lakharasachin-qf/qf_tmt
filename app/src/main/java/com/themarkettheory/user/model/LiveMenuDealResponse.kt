package com.themarkettheory.user.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class LiveMenuDealResponse {
    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: LiveMenuData? = null
}

class LiveMenuData {
    @SerializedName("list")
    @Expose
    var list: ArrayList<LiveMenuList>? = null
}

class LiveMenuList {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("service_id")
    @Expose
    var serviceId: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("menu")
    @Expose
    var menu: ArrayList<Menu>? = null

    @SerializedName("menu_base_url")
    @Expose
    var menuBaseUrl: String? = null
}

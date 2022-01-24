package com.themarkettheory.user.newmodels.menulist


import com.google.gson.annotations.SerializedName

data class Menu(
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("id")
    var id: Any?,
    @SerializedName("menu")
    var menu: List<MenuX>?,
    @SerializedName("menu_base_url")
    var menuBaseUrl: String?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("service_id")
    var serviceId: Any?,
    @SerializedName("status")
    var status: Int?,
    @SerializedName("updated_at")
    var updatedAt: String?
)
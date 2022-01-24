package com.themarkettheory.user.database.dbtables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tblMenuCart")
class TableMenuCart(
    var restaurantID: String?,
    var menuID: String?,
    var menuImage: String?,
    var menuFoodType: String?,
    var menuIsSpicy: String?,
    var menuPoint: String?,
    var menuPreparingTime: String?,
    var menuTitle: String?,
    var menuDishQty: String?,
    var menuUnit: String?,
    var menuCategoryName: String?,
    var menuCurrency: String?,
    var menuFinalPrice: String?,
    var menuActualPrice: String?,
    var menuIsHeader: String?,
    var menuIsAdded: String?,
    var menuIsBooked: String?
) {
    @PrimaryKey(autoGenerate = true)
    var _id = 0
}
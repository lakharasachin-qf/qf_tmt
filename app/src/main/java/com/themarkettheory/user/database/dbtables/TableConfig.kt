package com.themarkettheory.user.database.dbtables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tblConfig")
class TableConfig(var configField: String?, var configValue: String?) {
    @PrimaryKey(autoGenerate = true)
    var _id = 0

    companion object {
        var fieldConfigField = "ConfigField"
        var fieldConfigValue = "ConfigValue"
    }
}
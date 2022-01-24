package com.themarkettheory.user.database.dbdao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.themarkettheory.user.database.dbtables.TableConfig

@Dao
interface DaoConfig {
    @Insert
    fun insertConfigTable(tableConfig: TableConfig?)

    @Query("delete from tblConfig where ConfigField = :field")
    fun deleteConfigTableByField(field: String?)

    @Query("select ConfigValue from tblConfig where ConfigField = :field")
    fun selectConfigTableByField(field: String?): String?
}
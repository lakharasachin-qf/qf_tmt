package com.themarkettheory.user.database.dbdao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.themarkettheory.user.database.dbtables.TableMenuCart

@Dao
interface DaoMenuCart {
    @Insert
    fun insertMenuCart(tableMenuCart: TableMenuCart)

    @Query("delete from tblMenuCart")
    fun deleteMenuCart()

    @Query("select * from tblMenuCart")
    fun selectMenuCart(): TableMenuCart
}
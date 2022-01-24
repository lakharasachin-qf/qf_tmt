package com.themarkettheory.user.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.themarkettheory.user.database.dbdao.DaoConfig
import com.themarkettheory.user.database.dbdao.DaoMenuCart
import com.themarkettheory.user.database.dbtables.TableConfig
import com.themarkettheory.user.database.dbtables.TableMenuCart

@Database(
    entities = [
        TableConfig::class,
        TableMenuCart::class,
    ],
    version = DbConst.dbVer,
    exportSchema = false
)
abstract class MyRoomDatabase : RoomDatabase() {
    companion object {
        private var myDBInstance: MyRoomDatabase? = null

        fun getDB(context: Context): MyRoomDatabase? {
            if (myDBInstance == null) {
                myDBInstance =
                    Room.databaseBuilder(context, MyRoomDatabase::class.java, DbConst.dbName)
                        .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                        .allowMainThreadQueries()
                        .build()
            }
            return myDBInstance
        }
    }

    abstract fun daoConfig(): DaoConfig

    abstract fun daoMenuCart(): DaoMenuCart
}
package com.themarkettheory.user.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.themarkettheory.user.database.MyRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Singleton
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder().serializeNulls().create()
    }

    @Singleton
    @Provides
    fun provideMyRoomDB(@ApplicationContext context: Context): MyRoomDatabase {
        return MyRoomDatabase.getDB(context)!!
    }
}
package com.themarkettheory.user

import android.app.Application
import android.content.Context
import com.danikula.videocache.HttpProxyCacheServer
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    private var proxy: HttpProxyCacheServer? = null
    private fun newProxy(): HttpProxyCacheServer {
        return HttpProxyCacheServer(this)
    }

    override fun onCreate() {
        super.onCreate()

        //val options = FirebaseOptions.Builder()
        //.setApplicationId(getString(R.string.googleApplicationID)) // Required for Analytics.
        //.setProjectId(getStrinFirebase.messaging.isAutoInitEnabledg(R.string.googleProjectID)) // Required for Firebase Installations.
        //.setApiKey(getString(R.string.google_key)) // Required for Auth.
        //.build()

        //FirebaseApp.initializeApp(applicationContext)

        val leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(1024 * 1024 * 1024)
        val databaseProvider: DatabaseProvider = ExoDatabaseProvider(this)

        if (simpleCache == null) {
            simpleCache = SimpleCache(cacheDir, leastRecentlyUsedCacheEvictor, databaseProvider)
//            cache = Cache()
        }
    }

    companion object {
        var simpleCache: SimpleCache? = null
        var cache: SimpleCache? = null

        fun getProxy(context: Context): HttpProxyCacheServer {
            val app = context.applicationContext as App
            return (if (app.proxy == null) app.newProxy1().also { app.proxy = it } else app.proxy!!)
        }
    }

    private fun newProxy1(): HttpProxyCacheServer {
        return HttpProxyCacheServer.Builder(this)
            .maxCacheSize(1024 * 1024 * 1024.toLong()) // 1 Gb for cache
            .build()
    }
}
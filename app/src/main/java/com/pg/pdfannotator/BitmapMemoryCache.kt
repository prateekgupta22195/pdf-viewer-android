package com.pg.pdfannotator

import android.graphics.Bitmap
import android.util.Log
import android.util.LruCache


class BitmapMemoryCache {
    private val memoryCache: LruCache<String, Bitmap>

    init {
        // Get the maximum memory available to the app
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()

        // Set the cache size to a fraction of the available memory
        val cacheSize = maxMemory / 2

        // Initialize the LruCache with the calculated cache size
        memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, value: Bitmap): Int {
                return value.byteCount / 1024
            }
        }
    }

    fun addBitmapToMemoryCache(key: String?, bitmap: Bitmap?) {
        if (getBitmapFromMemoryCache(key) == null) {
            memoryCache.put(key, bitmap)
        }
    }

    fun getBitmapFromMemoryCache(key: String?): Bitmap? {
        return memoryCache.get(key)
    }

    fun clearMemoryCache() {
        memoryCache.evictAll()
    }
}
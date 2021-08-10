package com.kush.androidjetpackkotlin.application

import android.app.Application
import com.kush.androidjetpackkotlin.model.database.FavDishRepository
import com.kush.androidjetpackkotlin.model.database.FavDishRoomDatabase

class FavDishApplication : Application() {

    private val database by lazy {
        FavDishRoomDatabase.getDatabase((this@FavDishApplication))
    }

    val repository by lazy {
        FavDishRepository(database.favDishDao())
    }
}
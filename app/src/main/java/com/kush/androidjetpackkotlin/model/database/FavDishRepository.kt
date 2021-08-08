package com.kush.androidjetpackkotlin.model.database

import androidx.annotation.WorkerThread
import com.kush.androidjetpackkotlin.model.entities.FavDish

class FavDishRepository(private val favDishDao: FavDishDao) {

    @WorkerThread
    suspend fun insertFavDishData(favDish: FavDish) {
        favDishDao.insertFavDishDetails(favDish)
    }
}
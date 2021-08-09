package com.kush.androidjetpackkotlin.model.database

import androidx.room.Dao
import androidx.room.Insert
import com.kush.androidjetpackkotlin.model.entities.FavDish

@Dao
interface FavDishDao {

    @Insert
    suspend fun insertFavDishDetails(favDish: FavDish)
}
package com.kush.androidjetpackkotlin.utils

object Constants {

    const val DISH_TYPE: String = "DishType"
    const val DISH_CATEGORY: String = "DishCategory"
    const val DISH_COOKING_TIME: String = "DishCookingTime"

    fun dishTypes(): ArrayList<String> {
        val distTypeList = ArrayList<String>()
        distTypeList.add("Breakfast")
        distTypeList.add("Lunch")
        distTypeList.add("Snacks")
        distTypeList.add("Dinner")
        distTypeList.add("Salad")
        distTypeList.add("Side Dish")
        distTypeList.add("Dessert")
        distTypeList.add("Others")
        return distTypeList
    }
}
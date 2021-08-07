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

    fun dishCategories(): ArrayList<String> {
        val dishCategoriesList = ArrayList<String>()
        dishCategoriesList.add("Pizza")
        dishCategoriesList.add("BBQ")
        dishCategoriesList.add("Bakery")
        dishCategoriesList.add("Burger")
        dishCategoriesList.add("Cafe")
        dishCategoriesList.add("Chicken")
        dishCategoriesList.add("Dessert")
        dishCategoriesList.add("Drinks")
        dishCategoriesList.add("Hot Dogs")
        dishCategoriesList.add("Juices")
        dishCategoriesList.add("Sandwich")
        dishCategoriesList.add("Tea & Coffee")
        dishCategoriesList.add("Wraps")
        dishCategoriesList.add("Others")
        return dishCategoriesList
    }
}
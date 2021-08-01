package com.kush.androidjetpackkotlin.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kush.androidjetpackkotlin.R
import com.kush.androidjetpackkotlin.databinding.ActivityAddUpdateDishBinding

class AddUpdateDishActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityAddUpdateDishBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

    }
}
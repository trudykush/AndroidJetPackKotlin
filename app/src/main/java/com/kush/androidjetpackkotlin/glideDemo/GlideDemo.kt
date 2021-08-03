package com.kush.androidjetpackkotlin.glideDemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kush.androidjetpackkotlin.R

class GlideDemo : AppCompatActivity() {

    private val image = getString(R.string.superman_image)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glide_demo)
    }
}
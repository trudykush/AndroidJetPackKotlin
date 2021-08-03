package com.kush.androidjetpackkotlin.glideDemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.kush.androidjetpackkotlin.R
import com.kush.androidjetpackkotlin.databinding.ActivityGlideDemoBinding

class GlideDemo : AppCompatActivity() {

    private val supermanImage = getString(R.string.superman_image)

    private lateinit var glideDemoBinding: ActivityGlideDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glide_demo)

        val imageOne = glideDemoBinding.imageOne
        val imageTwo = glideDemoBinding.imageTwo
        val imageThree = glideDemoBinding.imageThree

        Glide.with(this)
            .load(supermanImage)
            .into(imageOne)

        Glide.with(this)
            .load(supermanImage)
            .fitCenter()
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.ic_add_a_photo)
            .into(imageTwo)
    }
}
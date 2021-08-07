package com.kush.androidjetpackkotlin.view.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.kush.androidjetpackkotlin.R
import com.kush.androidjetpackkotlin.databinding.ActivityAddUpdateDishBinding
import com.kush.androidjetpackkotlin.databinding.DialogCustomImageSelectionBinding

class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityAddUpdateDishBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setUpActionBar()
        mBinding.ivAddDishImage.setOnClickListener(this)

    }

    private fun setUpActionBar() {
        setSupportActionBar(mBinding.toolbarAddDishActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mBinding.toolbarAddDishActivity.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when(v.id) {
                R.id.iv_add_dish_image -> {
                    customImageSelectionDialog()
                    return
                }
            }
        }
    }

    private fun customImageSelectionDialog() {
        val dialog = Dialog(this)
        val binding: DialogCustomImageSelectionBinding =
            DialogCustomImageSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.tvCamera.setOnClickListener {
            Dexter.withContext(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            addUpdateDishResultLauncher.launch(intent)
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermission()
                }

            }).onSameThread().check()

            dialog.dismiss()
        }

        binding.tvGallery.setOnClickListener {
            Dexter.withContext(this).withPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object: PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    val galleryIntent = Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    addUpdateDishResultLauncher.launch(galleryIntent)
                    //startActivityForResult(galleryIntent, GALLERY)
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(this@AddUpdateDishActivity,
                        "Gallery permission denied", Toast.LENGTH_LONG)
                        .show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    showRationalDialogForPermission()
                }


            }).onSameThread().check()

            dialog.dismiss()
        }

        dialog.show()
    }

    var addUpdateDishResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result?.let {
                val selectedImage: Uri? = result.data?.data
                if (selectedImage != null) {
//                    mBinding.ivDishImage.setImageURI(selectedImage)
                    Glide.with(this)
                        .load(selectedImage)
                        .centerCrop()
                        .into(mBinding.ivDishImage)
                } else {
                    result.data?.extras?.let {
                        val bitmap: Bitmap = result.data?.extras?.get("data") as Bitmap

//                        mBinding.ivDishImage.setImageBitmap(bitmap)
                        Glide.with(this)
                            .load(bitmap)
                            .centerCrop()
                            .into(mBinding.ivDishImage)

                        mBinding.ivAddDishImage.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.ic_vector_edit
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA) {
                val bitmap: Bitmap = data?.extras?.get("data") as Bitmap

                mBinding.ivDishImage.setImageBitmap(bitmap)

                mBinding.ivAddDishImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_vector_edit
                    )
                )
            }
            if (requestCode == GALLERY) {
                data?.let {
                    val selectedPhotoURI = data.data
                    mBinding.ivDishImage.setImageURI(selectedPhotoURI)

                    mBinding.ivAddDishImage.setImageDrawable(ContextCompat.getDrawable(this,
                        R.drawable.ic_vector_edit))
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Add Update Dish Activity", "User cancelled Image")
        }
    }

    private fun showRationalDialogForPermission() {
        AlertDialog.Builder(this).setMessage("Permissions turned off required for feature." +
                "It can be enabled under Application settings.")
            .setPositiveButton("Go to Settings") {
                    _,_ ->
                run {
                    try {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }
                }
            }
            .setNegativeButton("Cancel") {
                    dialog, _ ->
                run {
                    dialog.dismiss()
                }
            }.show()

    }

    companion object {
        private const val CAMERA = 1
        private const val GALLERY = 2
    }
}
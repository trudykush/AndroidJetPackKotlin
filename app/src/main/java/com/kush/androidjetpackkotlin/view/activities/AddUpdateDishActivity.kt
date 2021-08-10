package com.kush.androidjetpackkotlin.view.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.kush.androidjetpackkotlin.R
import com.kush.androidjetpackkotlin.application.FavDishApplication
import com.kush.androidjetpackkotlin.databinding.ActivityAddUpdateDishBinding
import com.kush.androidjetpackkotlin.databinding.DialogCustomImageSelectionBinding
import com.kush.androidjetpackkotlin.databinding.DialogCustomListBinding
import com.kush.androidjetpackkotlin.utils.Constants
import com.kush.androidjetpackkotlin.view.adapters.CustomListItemAdapter
import com.kush.androidjetpackkotlin.viewmodel.FavDishViewModel
import com.kush.androidjetpackkotlin.viewmodel.FavDishViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityAddUpdateDishBinding
    private var mImagePath: String = ""

    private lateinit var mCustomListDialog: Dialog

    private val mFavDishViewModel : FavDishViewModel by viewModels {
        FavDishViewModelFactory((application as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setUpActionBar()
        mBinding.ivAddDishImage.setOnClickListener(this)

        mBinding.etType.setOnClickListener(this)
        mBinding.etCategory.setOnClickListener(this)
        mBinding.etCookingTime.setOnClickListener(this)

        mBinding.btnAddDish.setOnClickListener(this)
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
                R.id.et_type -> {
                    customItemListDialog(resources.getString(R.string.title_select_dish_type),
                        Constants.dishTypes(),
                        Constants.DISH_TYPE)
                    return
                }
                R.id.et_category -> {
                    customItemListDialog(resources.getString(R.string.title_select_dish_category),
                        Constants.dishCategories(),
                        Constants.DISH_CATEGORY)
                    return
                }
                R.id.et_cooking_time -> {
                    customItemListDialog(resources.getString(R.string.title_select_dish_cooking_time),
                        Constants.dishCookingTime(),
                        Constants.DISH_COOKING_TIME)
                    return
                }
                R.id.btn_add_dish -> {
                    val title = mBinding.etTitle.text.toString().trim { it <= ' ' }
                    val type = mBinding.etType. text.toString().trim { it <= ' ' }
                    val category = mBinding.etCategory. text.toString().trim { it <= ' ' }
                    val ingredients = mBinding.etIngredients. text.toString().trim { it <= ' ' }
                    val cookingTimeInMinutes = mBinding.etCookingTime. text.toString().trim { it <= ' ' }
                    val cookingDirection = mBinding.etDirectionToCook. text.toString().trim { it <= ' ' }

                    when {
                        TextUtils.isEmpty(mImagePath) -> {
                            Toast.makeText(this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_select_dish_image),
                                Toast.LENGTH_LONG).show()
                        }

                        TextUtils.isEmpty(title) -> {
                            Toast.makeText(this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_select_dish_title),
                                Toast.LENGTH_LONG).show()
                        }

                        TextUtils.isEmpty(type) -> {
                            Toast.makeText(this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_select_dish_type),
                                Toast.LENGTH_LONG).show()
                        }

                        TextUtils.isEmpty(category) -> {
                            Toast.makeText(this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_select_dish_category),
                                Toast.LENGTH_LONG).show()
                        }

                        TextUtils.isEmpty(ingredients) -> {
                            Toast.makeText(this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_select_dish_ingredients),
                                Toast.LENGTH_LONG).show()
                        }

                        TextUtils.isEmpty(cookingTimeInMinutes) -> {
                            Toast.makeText(this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_select_dish_cooking_time),
                                Toast.LENGTH_LONG).show()
                        }

                        TextUtils.isEmpty(cookingDirection) -> {
                            Toast.makeText(this@AddUpdateDishActivity,
                                resources.getString(R.string.err_msg_select_dish_cooking_instruction),
                                Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            Toast.makeText(this@AddUpdateDishActivity,
                                getString(R.string.all_valid_entries),
                                Toast.LENGTH_LONG).show()
                        }
                    }
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

    fun selectedListItem(item: String, selection: String) {
        when(selection) {
            Constants.DISH_TYPE -> {
                mCustomListDialog.dismiss()
                mBinding.etType.setText(item)
            }
            Constants.DISH_CATEGORY -> {
                mCustomListDialog.dismiss()
                mBinding.etCategory.setText(item)
            }
            Constants.DISH_COOKING_TIME -> {
                mCustomListDialog.dismiss()
                mBinding.etCookingTime.setText(item)
            }
        }
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
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Log.e("Error", "Error loading image$e")
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                resource?.let {
                                    val bitmap: Bitmap = resource.toBitmap()
                                    mImagePath = saveImageToInternalStorage(bitmap)
                                    Log.i("Image Path", mImagePath)
                                }
                                return false
                            }

                        })
                        .into(mBinding.ivDishImage)
                } else {
                    result.data?.extras?.let {
                        val bitmap: Bitmap = result.data?.extras?.get("data") as Bitmap
//                        mBinding.ivDishImage.setImageBitmap(bitmap)
                        Glide.with(this)
                            .load(bitmap)
                            .centerCrop()
                            .into(mBinding.ivDishImage)

                        mImagePath = saveImageToInternalStorage(bitmap)
                        Log.i("Image Path", mImagePath)

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

    private fun saveImageToInternalStorage (bitmap: Bitmap) : String {
        val wrapper = ContextWrapper(applicationContext)

        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file.absolutePath
    }

    private fun customItemListDialog(title: String, itemList: List<String>, selection: String) {
        mCustomListDialog = Dialog(this)
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)

        mCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = title

        binding.rvList.layoutManager = LinearLayoutManager(this)
        val adapter = CustomListItemAdapter(this, itemList, selection)
        binding.rvList.adapter = adapter
        mCustomListDialog.show()
    }

    companion object {
        private const val CAMERA = 1
        private const val GALLERY = 2

        private const val IMAGE_DIRECTORY = "FavDishImages"
    }
}


/*
*  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
* */
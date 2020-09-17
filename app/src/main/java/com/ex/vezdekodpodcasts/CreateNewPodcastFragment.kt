package com.ex.vezdekodpodcasts

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.RectF
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.ex.vezdehodapp.utils.dpToPx
import com.ex.vezdehodapp.utils.showFragment
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import kotlinx.android.synthetic.main.fragment_create_new_podcast.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class CreateNewPodcastFragment : Fragment() {

    private val GALLERY = 111
    private val PERMISSION_ALL = 2
    lateinit var wallpaperDirectory: File

    private var PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private var currentImage: Bitmap? = null
    lateinit var pref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_new_podcast, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = requireContext().getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)

        val v = layoutInflater.inflate(R.layout.custom_toolbar, null)
        create_podcast_toolbar.addView(v)
        v.toolbar_title.text = "Новый подкаст"
        v.toolbar_back_btn.setOnClickListener {
            fragmentManager!!.popBackStack()
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                fragmentManager!!.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)


        image.setOnClickListener {
            uploadAvatar()
        }

        save_podacst_btn.setOnClickListener {
            saveData()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveData() {
        var encoded = ""
        if (currentImage != null)
        {
            val bm = currentImage
            val baos = ByteArrayOutputStream()
            bm!!.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val b = baos.toByteArray()
            encoded = Base64.getEncoder().encodeToString(b)
        }

        pref.edit().putString("title", edit_text_for_title.text.toString())
            .apply()

        pref.edit().putString("image", encoded)
            .apply()

        pref.edit().putString("description", edit_text_for_description.text.toString())
            .apply()

        showFragment(EditPodcastFragment(), requireFragmentManager())

    }

    //---------------------LOAD PHOTO----------------------
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        if (requestCode == GALLERY) {
            if (data != null) {

                try {
                    var bitmap: Bitmap? = null

                    val selectedPhotoUri = data.data

                    try {
                        if (selectedPhotoUri != null) {

                            if (Build.VERSION.SDK_INT < 28) {
                                bitmap = MediaStore.Images.Media.getBitmap(
                                    requireContext().contentResolver,
                                    selectedPhotoUri
                                )
                            } else {

                                val options = BitmapFactory.Options()
                                bitmap = BitmapFactory.decodeStream(
                                    requireContext().contentResolver.openInputStream(
                                        selectedPhotoUri
                                    ),
                                    null,
                                    options
                                )
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    if (bitmap != null) {

                        val scaleBitmap = scaleCenterCrop(
                            bitmap,
                            dpToPx(requireContext(), 72F),
                            dpToPx(requireContext(), 72F)
                        )

                        currentImage = scaleBitmap

                        imageView2.visibility = View.GONE
                        Glide.with(this).load(scaleBitmap).into(image)
                        image.clipToOutline = true

                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }


    private fun uploadAvatar() {
        setupPermission()
    }

    fun hasPermissions(context: Context, permissions: Array<String>): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    private fun setupPermission() {
        if (!hasPermissions(requireContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(this.requireActivity(), PERMISSIONS, PERMISSION_ALL)
        } else {
            //showPictureDialog()
            choosePhotoFromGallery()
        }
    }

    private fun choosePhotoFromGallery() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun saveImage(myBitmap: Bitmap): String {
        val code = requireContext().packageManager.checkPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            requireContext().packageName
        )
        if (code != PackageManager.PERMISSION_GRANTED) {
            setupPermission()
        }
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        wallpaperDirectory = File(
            (Environment.getExternalStorageDirectory()).toString() + "/VezdehodApp"
        )
        Log.d("fee", wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists()) {

            wallpaperDirectory.mkdirs()
        }

        try {
            Log.d("heel", wallpaperDirectory.toString())
            val f = File(
                wallpaperDirectory, ((Calendar.getInstance()
                    .timeInMillis).toString() + ".jpg")
            )
            f.createNewFile()

            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(
                context,
                arrayOf(f.path),
                arrayOf("image/jpeg"), null
            )
            fo.close()

            Log.d("TAG", "File Saved::--->" + f.absolutePath)

            return f.absolutePath
        } catch (e1: IOException) {

            e1.printStackTrace()
        }

        return ""
    }

    private fun scaleCenterCrop(source: Bitmap, newHeight: Int, newWidth: Int): Bitmap? {
        val sourceWidth = source.width
        val sourceHeight = source.height

        val xScale = newWidth.toFloat() / sourceWidth
        val yScale = newHeight.toFloat() / sourceHeight
        val scale = Math.max(xScale, yScale)

        val scaledWidth = scale * sourceWidth
        val scaledHeight = scale * sourceHeight


        val left = (newWidth - scaledWidth) / 2
        val top = (newHeight - scaledHeight) / 2

        val targetRect = RectF(left, top, left + scaledWidth, top + scaledHeight)

        val dest = Bitmap.createBitmap(newWidth, newHeight, source.config)
        val canvas = Canvas(dest)
        canvas.drawBitmap(source, null, targetRect, null)
        return dest
    }
}
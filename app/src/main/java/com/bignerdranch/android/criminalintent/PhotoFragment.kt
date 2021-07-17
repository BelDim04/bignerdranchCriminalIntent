package com.bignerdranch.android.criminalintent

import android.app.Dialog
import android.media.ExifInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import java.io.File
import java.util.*

private const val ARG_FILE = "file"

class PhotoFragment: DialogFragment() {

    companion object{
        fun newInstance(file: File): PhotoFragment{
            val args = Bundle().apply {
                putSerializable(ARG_FILE, file)
            }
            val fragment = PhotoFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val file by lazy {  arguments?.getSerializable(ARG_FILE) as File }
    private lateinit var imageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_photo, container)
        imageView = view.findViewById(R.id.crime_photo)
        val bitmap = getScaledBitmap(file.path, requireActivity())
        imageView.setImageBitmap(bitmap)
        val exif = ExifInterface(file.path)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        if(orientation == ExifInterface.ORIENTATION_ROTATE_90) imageView.rotation = 90.0F
        return view
    }
}
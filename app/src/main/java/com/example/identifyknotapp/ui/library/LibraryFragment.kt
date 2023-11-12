package com.example.identifyknotapp.ui.library

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.identifyknotapp.R
import com.example.identifyknotapp.databinding.FragmentLibraryBinding
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Calendar
import java.util.Locale


class LibraryFragment : Fragment() {
    private lateinit var _binding: FragmentLibraryBinding
    private lateinit var _bitmapImage: Bitmap
    private lateinit var _resultLauncher: ActivityResultLauncher<Intent>

    private val storageRef = FirebaseStorage.getInstance().reference
    private var _imageName = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        openSelectImageResult()
        _binding.selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            _resultLauncher.launch(intent)
        }

        _binding.imageSegmentation.setOnClickListener {
            val action = LibraryFragmentDirections.actionFragmentLibraryToFragmentDetail(_imageName, convertImageToBase64())
            findNavController().navigate(action)
        }
    }

    private fun openSelectImageResult() {
        _resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                if (result.data != null) {
                    val linkImg: Uri? = result.data?.data
                    linkImg?.let { image ->
                        val now = Calendar.getInstance().time
                        val date = SimpleDateFormat("dd-MM-yyyy-HH-mm-ss", Locale.getDefault()).format(now)
                        _imageName = "image_mobile_${date}.jpg"
                        storageRef.child("images/${_imageName}").putFile(image)

                    }
                    try {
                        when (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                            true -> {
                                val bitmap = MediaStore.Images.Media.getBitmap(
                                    requireActivity().contentResolver,
                                    linkImg
                                )
                                val matrix = Matrix()
                                matrix.postRotate(0.0F)
                                _bitmapImage = Bitmap.createBitmap(
                                    bitmap,
                                    0,
                                    0,
                                    bitmap.width,
                                    bitmap.height,
                                    matrix,
                                    true
                                )
                            }

                            false -> {
                                linkImg?.let {
                                    val source = ImageDecoder.createSource(
                                        requireActivity().contentResolver,
                                        it
                                    )
                                    val bitmap = ImageDecoder.decodeBitmap(source)
                                    val matrix = Matrix()
                                    matrix.postRotate(0.0F)
                                    _bitmapImage = Bitmap.createBitmap(
                                        bitmap,
                                        0,
                                        0,
                                        bitmap.width,
                                        bitmap.height,
                                        matrix,
                                        true
                                    )
                                }
                            }
                        }
                        _binding.imageCapture.setImageBitmap(_bitmapImage)
                        _binding.selectImageButton.visibility = View.INVISIBLE
                        _binding.imageSegmentation.visibility = View.VISIBLE

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(context, "No image selected!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun convertImageToBase64(): String {
        val stream = ByteArrayOutputStream()
        _bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val imageBytes = stream.toByteArray()
        return android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT)
    }
}
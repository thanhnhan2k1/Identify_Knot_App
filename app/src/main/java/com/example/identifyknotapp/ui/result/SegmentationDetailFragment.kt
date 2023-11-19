package com.example.identifyknotapp.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.identifyknotapp.R
import com.example.identifyknotapp.data.RemoteSegmentationImpl
import com.example.identifyknotapp.data.model.Output
import com.example.identifyknotapp.data.model.WoodHistory
import com.example.identifyknotapp.databinding.FragmentSegmentationDetailBinding
import com.example.identifyknotapp.ui.SegmentationViewModel
import com.example.identifyknotapp.ui.SegmentationViewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class SegmentationDetailFragment : Fragment() {
    private lateinit var _binding: FragmentSegmentationDetailBinding
    private lateinit var _viewModel: SegmentationViewModel
    private val _arg: SegmentationDetailFragmentArgs by navArgs()
    private lateinit var _dateSegmentation: String
    private val _db = Firebase.database.reference
    private val _adapter = DescriptionWoodAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSegmentationDetailBinding.inflate(inflater, container, false)
        isLoading(true)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val remoteService = RemoteSegmentationImpl.getRemoteWoodService()
        val remoteDescription = RemoteSegmentationImpl.getRemoteWoodDescriptionsService()
        val viewModelFactory = SegmentationViewModelFactory(remoteService, remoteDescription)
        val image = _arg.image
        val woodBody = _arg.woodBody
        _dateSegmentation = arguments?.getString("date") ?: ""

        _viewModel = ViewModelProvider(this, viewModelFactory)[SegmentationViewModel::class.java]
        _binding.rvDetailWood.adapter = _adapter

        if(_dateSegmentation.isNotEmpty()) {
            isLoading(false)
            setHistoryView(_dateSegmentation)
        }
        else {
            _viewModel.predictSegmentationImage(imageName = image)

            _viewModel.segmentationResult.observe(viewLifecycleOwner) { result ->
                isLoading(false)
                if (result != null) {
                    if (isNotWoodImage(result)) {
                        _binding.noResult.visibility = View.VISIBLE
                    } else {
                        _binding.noResult.visibility = View.GONE
                        setView(result)
                    }
                }
            }

            _viewModel.woodDescriptions.observe(viewLifecycleOwner) { listDescriptions ->
                _adapter.setData(listDescriptions)
                if (listDescriptions.isEmpty()) {
                    Toast.makeText(requireContext(), "No found more information", Toast.LENGTH_SHORT)
                        .show()
                }
                isLoading(false)
            }

            _viewModel.loadingSuccess.observe(viewLifecycleOwner) { status ->
                isLoadingSuccess(status)
            }

            _binding.btnViewMore.setOnClickListener {
                _viewModel.getWoodDescriptions(woodBody, image)
                _binding.rvDetailWood.isVisible = true
                _binding.btnViewMore.visibility = View.INVISIBLE
                isLoading(true)
            }
        }
    }

    private fun setView(result: Output) {
        _binding.numberSingleKnots.text = "${result.numberOfSingle}"
        _binding.numberDoubleKnots.text = "${result.numberOfDouble}"
        _binding.areaSingleKnots.text =
            getString(R.string.format_area).format(result.averageAreaSingle)
        _binding.areaDoubleKnots.text =
            getString(R.string.format_area).format(result.averageAreaDouble)
        Glide.with(this).load(result.resultImage).into(_binding.imageResult)
        _binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setHistoryView(date: String) {
        val query = _db.child("history").orderByChild("date").equalTo(date)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val result = snapshot.children.first().getValue(WoodHistory::class.java)
                result?.apply {
                    _binding.numberSingleKnots.text = "$numberOfSingle"
                    _binding.numberDoubleKnots.text = "$numberOfDouble"
                    _binding.areaSingleKnots.text =
                        getString(R.string.format_area).format(averageAreaSingle)
                    _binding.areaDoubleKnots.text =
                        getString(R.string.format_area).format(averageAreaDouble)
                    Glide.with(requireContext()).load(mask_link).into(_binding.imageResult)
                    _adapter.setData(result.toListWoodDescriptions())
                    _binding.btnViewMore.isVisible = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "No data!", Toast.LENGTH_SHORT).show()
            }
        })
        _binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun isLoading(status: Boolean) {
        when (status) {
            true -> {
                _binding.detailScreen.visibility = View.GONE
                _binding.loading.visibility = View.VISIBLE
            }

            else -> {
                _binding.detailScreen.visibility = View.VISIBLE
                _binding.loading.visibility = View.INVISIBLE
            }
        }
    }

    private fun isLoadingSuccess(status: Boolean) {
        _binding.noData.isVisible = !status
        _binding.detailScreen.isVisible = status
    }

    private fun isNotWoodImage(data: Output): Boolean {
        return data.numberOfSingle == 0 || data.numberOfDouble == 0 ||
                data.averageAreaDouble == 0.toDouble() || data.averageAreaSingle == 0.toDouble()
    }
}
package com.example.identifyknotapp.ui.result

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.identifyknotapp.data.RemoteSegmentationImpl
import com.example.identifyknotapp.data.model.Output
import com.example.identifyknotapp.data.model.WoodRequestBody
import com.example.identifyknotapp.databinding.FragmentSegmentationDetailBinding
import com.example.identifyknotapp.ui.SegmentationViewModel
import com.example.identifyknotapp.ui.SegmentationViewModelFactory
import com.google.firebase.storage.FirebaseStorage

class SegmentationDetailFragment : Fragment() {
    private lateinit var _binding: FragmentSegmentationDetailBinding
    private lateinit var _viewModel: SegmentationViewModel
    private val arg: SegmentationDetailFragmentArgs by navArgs()
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
        val image = arg.image
        val imageBase64 = arg.imageBase64
        val adapter = DescriptionWoodAdapter()
        _viewModel = ViewModelProvider(this, viewModelFactory)[SegmentationViewModel::class.java]
        _binding.rvDetailWood.adapter = adapter

        _viewModel.predictSegmentationImage(imageName = image)

        _viewModel.segmentationResult.observe(viewLifecycleOwner){ result ->
            isLoading(false)
            setView(result)
        }

        _viewModel.woodDescriptions.observe(viewLifecycleOwner) { listDescriptions ->
            adapter.setData(listDescriptions)
            isLoading(false)
        }

        _viewModel.loadingSuccess.observe(viewLifecycleOwner) { status ->

        }

        _binding.btnViewMore.setOnClickListener {
            val body = WoodRequestBody(image = imageBase64)
            _viewModel.getWoodDescriptions(body)
            _binding.rvDetailWood.isVisible = true
            _binding.btnViewMore.isVisible = false
            isLoading(true)
        }
    }
    private fun setView(result: Output){
        _binding.numberSingleKnots.text = "${result.numberOfSingle}"
        _binding.numberDoubleKnots.text = "${result.numberOfDouble}"
        _binding.areaSingleKnots.text = "${result.averageAreaSingle}"
        _binding.areaDoubleKnots.text = "${result.averageAreaDouble}"
        Glide.with(this).load(result.resultImage).into(_binding.imageResult)
        _binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun isLoading(status: Boolean){
        when(status) {
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

    }
}
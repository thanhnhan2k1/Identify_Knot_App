package com.example.identifyknotapp.ui.result

import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.identifyknotapp.R
import com.example.identifyknotapp.data.RemoteSegmentationImpl
import com.example.identifyknotapp.data.SegmentationRepository
import com.example.identifyknotapp.data.model.Output
import com.example.identifyknotapp.databinding.FragmentSegmentationDetailBinding
import com.example.identifyknotapp.ui.SegmentationViewModel
import com.example.identifyknotapp.ui.SegmentationViewModelFactory
import com.google.gson.internal.LinkedTreeMap
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

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

        val remoteService = RemoteSegmentationImpl.getRemoteFoodService()
        val viewModelFactory = SegmentationViewModelFactory(remoteService)
        val image = arg.image
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

        _binding.btnViewMore.setOnClickListener {
            _viewModel.getWoodDescriptions()
            _binding.rvDetailWood.isVisible = true
            _binding.btnViewMore.isVisible = false
            isLoading(true)
        }
    }
    private fun setView(result: Output){
        Picasso.get().load(result.resultImage?.toUri()).into(_binding.imageResult)
        _binding.numberSingleKnots.text = "${result.numberOfSingle}"
        _binding.numberDoubleKnots.text = "${result.numberOfDouble}"
        _binding.areaSingleKnots.text = "${result.averageAreaSingle}"
        _binding.areaDoubleKnots.text = "${result.averageAreaDouble}"
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
}
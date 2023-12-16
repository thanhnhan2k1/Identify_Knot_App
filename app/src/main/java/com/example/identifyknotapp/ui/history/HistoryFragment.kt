package com.example.identifyknotapp.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.identifyknotapp.R
import com.example.identifyknotapp.data.model.WoodHistory
import com.example.identifyknotapp.data.model.WoodRequestBody
import com.example.identifyknotapp.databinding.FragmentHistoryBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryFragment : Fragment() {
    private lateinit var _binding: FragmentHistoryBinding
    private val _adapter = HistoryAdapter()
    private val _db = Firebase.database.reference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding.rvHistory.adapter = _adapter
        getHistory()
        _adapter.setOnClickCallback { date ->
            val bundle = bundleOf(
                "date" to date,
                "woodBody" to WoodRequestBody(image = "")
            )
            findNavController().navigate(R.id.fragment_detail, bundle)
        }
    }

    private fun getHistory() {
        _db.child("history").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listWood = mutableListOf<WoodHistory>()
                for (item: DataSnapshot in snapshot.children) {
                    item.getValue(WoodHistory::class.java).let { wood ->
                        if (wood != null) listWood.add(wood)
                    }
                }
                listWood.sortByDescending {
                    SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    ).parse(it.date.split(" ").first())
                }
                _adapter.setData(listWood)
                _binding.loading.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                _adapter.setData(emptyList())
                _binding.loading.visibility = View.GONE
                Toast.makeText(requireContext(), "Loading Fail!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
package com.example.identifyknotapp.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.identifyknotapp.data.model.WoodHistory
import com.example.identifyknotapp.databinding.ItemHistoryBinding

class HistoryAdapter: RecyclerView.Adapter<HistoryAdapter.HistoryWoodViewHolder>() {
    private val _listData = mutableListOf<WoodHistory>()
    private var _onClickCallback: ((date: String) -> Unit)? = null
    class HistoryWoodViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: WoodHistory, callback: ((date: String) -> Unit)?){
            binding.apply {
                tvName.text = data.name
                tvDate.text = data.date
                Glide.with(itemView.context).load(data.image_link).into(image)
                Glide.with(itemView.context).load(data.mask_link).into(mask)
                root.setOnClickListener {
                    if(data.date.isNotEmpty()) {
                        callback?.invoke(data.date)
                    }
                }
            }
        }
    }
    fun setData(list: List<WoodHistory>) {
        _listData.clear()
        _listData.addAll(list)
        notifyDataSetChanged()
    }

    fun setOnClickCallback(callback: ((date: String) -> Unit)?) {
        _onClickCallback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HistoryWoodViewHolder (
        ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun getItemCount(): Int = _listData.size

    override fun onBindViewHolder(holder: HistoryWoodViewHolder, position: Int) {
        holder.bind(_listData[position], _onClickCallback)
    }
}
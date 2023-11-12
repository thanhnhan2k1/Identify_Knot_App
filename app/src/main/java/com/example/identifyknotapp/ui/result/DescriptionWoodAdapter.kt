package com.example.identifyknotapp.ui.result

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.identifyknotapp.data.model.WoodDescription
import com.example.identifyknotapp.databinding.ItemWoodDescriptionBinding

class DescriptionWoodAdapter: RecyclerView.Adapter<DescriptionWoodAdapter.DescriptionWoodViewHolder>() {
    private val _listData = mutableListOf<WoodDescription>()
    class DescriptionWoodViewHolder(private val binding: ItemWoodDescriptionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: WoodDescription){
            if(data.title.contains("Characteristic")) {
                binding.tvTitle.text = data.title
                binding.tvContent.text = Html.fromHtml(data.content, Html.FROM_HTML_MODE_COMPACT)
            }
            else {
                binding.tvTitle.text = data.title
                binding.tvContent.text = data.content
            }
        }
    }
    fun setData(list: List<WoodDescription>) {
        _listData.clear()
        _listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DescriptionWoodViewHolder (
        ItemWoodDescriptionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun getItemCount(): Int = _listData.size

    override fun onBindViewHolder(holder: DescriptionWoodViewHolder, position: Int) {
        holder.bind(_listData[position])
    }
}
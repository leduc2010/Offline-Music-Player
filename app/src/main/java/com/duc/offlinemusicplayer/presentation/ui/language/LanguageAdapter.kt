package com.duc.offlinemusicplayer.presentation.ui.language

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.duc.offlinemusicplayer.databinding.ItemLanguageBinding
import com.duc.offlinemusicplayer.domain.model.LanguageModel

class LanguageAdapter(
    var onItemClick: ((LanguageModel) -> Unit)? = null,
    var selectedLanguageCode: String = ""
) : RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    private var items: List<LanguageModel> = emptyList()

    fun setItems(newItems: List<LanguageModel>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemLanguageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LanguageModel) {
            binding.tvName.text = item.name
            binding.imgFlag.setImageResource(item.flagRes)
            
            val isSelected = item.code == selectedLanguageCode
            binding.rootView.isSelected = isSelected
            binding.ivSelect.isSelected = isSelected

            binding.rootView.setOnClickListener {
                selectedLanguageCode = item.code
                onItemClick?.invoke(item)
                notifyDataSetChanged()
            }
        }
    }
}

package com.example.conversordemoeda.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.conversordemoeda.R
import com.example.conversordemoeda.model.Asset

class AssetAdapter(
    private val assets: List<Asset>,
    private val onConvertClick: (Asset) -> Unit
) : RecyclerView.Adapter<AssetAdapter.AssetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_assets, parent, false)
        return AssetViewHolder(view)
    }

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        holder.bind(assets[position] , onConvertClick)
    }

    override fun getItemCount(): Int = assets.size

    class AssetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCurrency: TextView = itemView.findViewById(R.id.tvCurrency)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        private val btnConvert: Button = itemView.findViewById(R.id.btnItemConvert)

        fun bind(asset: Asset, onConvertClick: (Asset) -> Unit) {
            tvCurrency.text = asset.currency.code
            tvAmount.text = asset.amount.toPlainString()
            btnConvert.setOnClickListener { onConvertClick(asset) }

        }
    }
}
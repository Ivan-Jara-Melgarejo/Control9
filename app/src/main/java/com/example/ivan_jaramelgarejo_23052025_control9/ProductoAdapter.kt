package com.example.ivan_jaramelgarejo_23052025_control9

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ivan_jaramelgarejo_23052025_control9.R
import com.example.ivan_jaramelgarejo_23052025_control9.Producto

class ProductoAdapter(private val onLongClick: (Producto, View) -> Unit) :
    androidx.recyclerview.widget.ListAdapter<Producto, ProductoAdapter.ProductoViewHolder>(ProductoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = getItem(position)
        holder.bind(producto)
        holder.itemView.setOnLongClickListener {
            onLongClick(producto, it)
            true
        }
    }

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        private val tvProductQuantity: TextView = itemView.findViewById(R.id.tvProductQuantity)
        private val tvProductPriceEuro: TextView = itemView.findViewById(R.id.tvProductPriceEuro)
        private val tvProductPriceCLP: TextView = itemView.findViewById(R.id.tvProductPriceCLP)
        private val tvProductExportLocation: TextView = itemView.findViewById(R.id.tvProductExportLocation)

        fun bind(producto: Producto) {
            tvProductName.text = producto.nombre
            tvProductQuantity.text = "Cantidad: ${producto.cantidad}"
            tvProductPriceEuro.text = "Precio (EUR): ${String.format("%.2f", producto.precioEuro)}€"
            tvProductPriceCLP.text = "Precio (CLP): ${String.format("%.2f", producto.precioPesosChilenos)}$"
            tvProductExportLocation.text = "Lugar de Exportación: ${producto.lugarExportacion}"
        }
    }

    class ProductoDiffCallback : DiffUtil.ItemCallback<Producto>() {
        override fun areItemsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Producto, newItem: Producto): Boolean {
            return oldItem == newItem
        }
    }
}
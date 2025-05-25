package com.example.ivan_jaramelgarejo_23052025_control9

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val cantidad: Int,
    val precioEuro: Double,
    val precioPesosChilenos: Double,
    val lugarExportacion: String
)
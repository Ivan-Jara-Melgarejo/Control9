package com.example.ivan_jaramelgarejo_23052025_control9

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun getAllProducts(): Flow<List<Producto>>

    @Insert
    suspend fun insertProduct(producto: Producto)

    @Delete
    suspend fun deleteProduct(producto: Producto)
}
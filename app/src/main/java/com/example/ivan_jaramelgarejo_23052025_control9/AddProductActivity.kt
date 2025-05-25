package com.example.ivan_jaramelgarejo_23052025_control9

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ivan_jaramelgarejo_23052025_control9.R
import com.example.ivan_jaramelgarejo_23052025_control9.AppDatabase
import com.example.ivan_jaramelgarejo_23052025_control9.Producto
import com.example.ivan_jaramelgarejo_23052025_control9.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddProductActivity : AppCompatActivity() {

    private lateinit var etProductName: EditText
    private lateinit var etProductQuantity: EditText
    private lateinit var etProductPriceEuro: EditText
    private lateinit var etProductExportLocation: EditText
    private lateinit var btnAddProduct: Button
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        database = AppDatabase.getDatabase(applicationContext)

        etProductName = findViewById(R.id.etProductName)
        etProductQuantity = findViewById(R.id.etProductQuantity)
        etProductPriceEuro = findViewById(R.id.etProductPriceEuro)
        etProductExportLocation = findViewById(R.id.etProductExportLocation)
        btnAddProduct = findViewById(R.id.btnAddProduct)

        btnAddProduct.setOnClickListener {
            Log.d("AddProductActivity", "Botón 'Agregar Producto' presionado en el formulario.")
            addProduct()
        }
    }

    private fun addProduct() {
        val name = etProductName.text.toString().trim()
        val quantityStr = etProductQuantity.text.toString().trim()
        val priceEuroStr = etProductPriceEuro.text.toString().trim()
        val exportLocation = etProductExportLocation.text.toString().trim()

        if (name.isEmpty() || quantityStr.isEmpty() || priceEuroStr.isEmpty() || exportLocation.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            Log.w("AddProductActivity", "Campos incompletos.")
            return
        }

        val quantity = quantityStr.toIntOrNull()
        val priceEuro = priceEuroStr.toDoubleOrNull()

        if (quantity == null || priceEuro == null) {
            Toast.makeText(this, "Cantidad y Precio en Euros deben ser números válidos.", Toast.LENGTH_SHORT).show()
            Log.e("AddProductActivity", "Error de formato en cantidad o precio.")
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getEuroValue()
                if (response.isSuccessful && response.body() != null) {
                    val euroValue = response.body()!!.serie[0].valor
                    val pricePesosChilenos = priceEuro * euroValue

                    val newProduct = Producto(
                        nombre = name,
                        cantidad = quantity,
                        precioEuro = priceEuro,
                        precioPesosChilenos = pricePesosChilenos,
                        lugarExportacion = exportLocation
                    )

                    database.productDao().insertProduct(newProduct)
                    Log.d("AddProductActivity", "Producto insertado en la base de datos: $newProduct")

                    withContext(Dispatchers.Main) {
                        val resultIntent = Intent().apply {
                            putExtra("message", "Producto cargado correctamente.")
                        }
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddProductActivity, "Error al obtener el valor del euro. Verifique su conexión.", Toast.LENGTH_LONG).show()
                        Log.e("AddProductActivity", "Error al obtener el valor del euro: ${response.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddProductActivity, "Error de red o API: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("AddProductActivity", "Excepción al consultar API o DB: ${e.message}", e)
                }
            }
        }
    }
}
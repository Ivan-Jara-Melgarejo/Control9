package com.example.ivan_jaramelgarejo_23052025_control9

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.ivan_jaramelgarejo_23052025_control9.AppDatabase
import com.example.ivan_jaramelgarejo_23052025_control9.Producto
import com.example.ivan_jaramelgarejo_23052025_control9.AddProductActivity
import com.example.ivan_jaramelgarejo_23052025_control9.ProductoAdapter
import com.example.ivan_jaramelgarejo_23052025_control9.ChartActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddProduct: FloatingActionButton
    private lateinit var productAdapter: ProductoAdapter
    private lateinit var database: AppDatabase

    private var selectedProduct: Producto? = null
    private var selectedProductView: View? = null

    private val addProductLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val message = result.data?.getStringExtra("message")
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                Log.d("MainActivity", "Mensaje de confirmación: $it")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = AppDatabase.getDatabase(applicationContext)

        recyclerView = findViewById(R.id.recyclerViewProducts)
        fabAddProduct = findViewById(R.id.fabAddProduct)

        productAdapter = ProductoAdapter { product, view ->
            selectedProduct = product
            selectedProductView = view
            registerForContextMenu(view)
            openContextMenu(view)
        }
        recyclerView.adapter = productAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        fabAddProduct.setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java)
            addProductLauncher.launch(intent)
            Log.d("MainActivity", "Botón 'Agregar Producto' presionado.")
        }

        // Observar los cambios en la base de datos y actualizar el RecyclerView
        lifecycleScope.launch {
            database.productDao().getAllProducts().collect { products ->
                productAdapter.submitList(products)
                Log.d("MainActivity", "Lista de productos actualizada. Total: ${products.size}")
            }
        }

        // Menú de opciones (para el gráfico)
        // Por simplicidad, agregaremos el botón del gráfico directamente en la barra de acción o como otro FAB
        // Para este ejemplo, lo pondremos como un botón en el menú de la barra de acción
        // o si queremos ser más fieles al requisito, un botón en el layout o un FAB adicional.
        // Para el ejemplo, lo implementaremos en el menú de opciones de la Activity
        // (los tres puntos en la esquina superior derecha)
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_show_chart -> {
                Log.d("MainActivity", "Opción 'Mostrar Gráfico' seleccionada.")
                val intent = Intent(this, ChartActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.context_add_product -> {
                Log.d("MainActivity", "Opción contextual 'Agregar Producto' seleccionada.")
                val intent = Intent(this, AddProductActivity::class.java)
                addProductLauncher.launch(intent)
                true
            }
            R.id.context_delete_product -> {
                selectedProduct?.let { productToDelete ->
                    lifecycleScope.launch {
                        database.productDao().deleteProduct(productToDelete)
                        Toast.makeText(this@MainActivity, "Producto '${productToDelete.nombre}' eliminado.", Toast.LENGTH_SHORT).show()
                        Log.d("MainActivity", "Producto '${productToDelete.nombre}' eliminado de forma persistente.")
                    }
                }
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }
}
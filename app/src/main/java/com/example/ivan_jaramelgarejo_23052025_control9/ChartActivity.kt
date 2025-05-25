package com.example.ivan_jaramelgarejo_23052025_control9

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.example.ivan_jaramelgarejo_23052025_control9.R
import com.example.ivan_jaramelgarejo_23052025_control9.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class ChartActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        database = AppDatabase.getDatabase(applicationContext)
        barChart = findViewById(R.id.barChart)

        loadChartData()
    }

    private fun loadChartData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val products = database.productDao().getAllProducts().first()
                Log.d("ChartActivity", "Productos obtenidos para el gráfico: ${products.size}")

                val sortedProducts = products.sortedByDescending { it.precioPesosChilenos }.take(4)
                Log.d("ChartActivity", "Los 4 productos más costosos: ${sortedProducts.joinToString { it.nombre }}")

                val entries = ArrayList<BarEntry>()
                val labels = ArrayList<String>()

                sortedProducts.forEachIndexed { index, product ->
                    entries.add(BarEntry(index.toFloat(), product.precioPesosChilenos.toFloat()))
                    labels.add(product.nombre)
                }

                withContext(Dispatchers.Main) {
                    if (entries.isNotEmpty()) {
                        val dataSet = BarDataSet(entries, "Productos Más Costosos (CLP)")
                        dataSet.color = Color.BLUE
                        dataSet.valueTextColor = Color.BLACK
                        dataSet.valueTextSize = 10f

                        val barData = BarData(dataSet)
                        barChart.data = barData
                        barChart.description.isEnabled = false // Deshabilitar descripción
                        barChart.setFitBars(true) // Ajustar barras
                        barChart.animateY(1000) // Animación en el eje Y

                        // Personalizar eje X
                        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                        barChart.xAxis.setDrawGridLines(false)
                        barChart.xAxis.setDrawAxisLine(true)
                        barChart.xAxis.granularity = 1f
                        barChart.xAxis.setLabelCount(labels.size, false)
                        barChart.xAxis.setCenterAxisLabels(false)
                        barChart.xAxis.textSize = 10f
                        barChart.xAxis.labelRotationAngle = -45f // Rotar etiquetas para mejor visualización

                        // Personalizar eje Y
                        barChart.axisRight.isEnabled = false // Deshabilitar eje Y derecho
                        barChart.axisLeft.textSize = 10f

                        barChart.invalidate() // Refrescar el gráfico
                        Log.d("ChartActivity", "Gráfico de barras actualizado.")
                    } else {
                        Log.w("ChartActivity", "No hay datos para mostrar en el gráfico.")
                        barChart.clear()
                        barChart.setNoDataText("No hay productos registrados para mostrar el gráfico.")
                        barChart.invalidate()
                    }
                }
            } catch (e: Exception) {
                Log.e("ChartActivity", "Error al cargar datos del gráfico: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    barChart.setNoDataText("Error al cargar los datos del gráfico.")
                    barChart.invalidate()
                }
            }
        }
    }
}
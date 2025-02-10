package com.carlosvpinto.dollar_mexico.ui.historia

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.carlosvpinto.dollar_mexico.databinding.FragmentHistoriaBinding
import com.carlosvpinto.dollar_mexico.model.ApiMexicoResponse
import com.carlosvpinto.dollar_mexico.model.history.HistoryModelResponseItem
import com.carlosvpinto.dollar_mexico.ui.home.HomeFragment
import com.carlosvpinto.dollar_mexico.utils.ApiService
import com.carlosvpinto.dollar_mexico.utils.Constants
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar

class HistoriaFragment : Fragment() {

    private var _binding: FragmentHistoriaBinding? = null

    private var diasMenos: Int = 7
    private var fechaInicio = 0
    private var fechaSeleccionada= ""
    private val TAG = "HISTORYFRAGMENT"

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHistoriaBinding.inflate(inflater, container, false)
        val root: View = binding.root


       // llamarApiHistory(1, "Afirme")
        configurarCarga()
        return root
    }
    private fun configurarCarga(){
        // Obtener el array de bancos desde los recursos
        //val listaHistory = resources.getStringArray(R.array.lista_history)
        val listaHistory = getResponseFromSharedPreferences7D(requireContext())
        Log.d(TAG, "configurarCarga: listaHistory $listaHistory")
        val adaptadorHist: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            obtenerNombresInstituciones(listaHistory)
        )
        binding.spinnerHistory.adapter = adaptadorHist
        // Establecer un listener para manejar la selección del spinner
        binding.spinnerHistory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {

                val nombreInstitucion = parent.getItemAtPosition(position).toString()
                llamarApiHistory(diasMenos, nombreInstitucion)

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Toast.makeText(requireContext(), "Debe selecionar La institucion", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun llamarApiHistory(diasMenos: Int, nombreInstitucion:String) {
        try {
            val savedResponseDolar = getResponseFromSharedPreferences7D(requireContext())
            if (savedResponseDolar != null) {
                HomeFragment.ApiResponseHolder.setResponse(savedResponseDolar)
            }
        } catch (e: Exception) {
            Log.d(TAG, "llamarApiHistory: error $e")
        } finally {
            // Cualquier código adicional que desees agregar
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            Log.d(TAG, "llamarApiHistory: dentro de viewLifecycleOwner")
            val baseUrl = Constants.URL_BASE
            val client = OkHttpClient.Builder().build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            val apiService = retrofit.create(ApiService::class.java)

            try {
                val dateFormat = SimpleDateFormat("dd-MM-yyyy")

                // Obtener la fecha actual
                val calendar = Calendar.getInstance()
                val diaDeHoy = dateFormat.format(calendar.time)
                Log.d(TAG, "llamarApiHistory: diaDeHoy $diaDeHoy")

                // Restar 7 días
                calendar.add(Calendar.DAY_OF_YEAR, -diasMenos)
                val fechaInicial = dateFormat.format(calendar.time)
                Log.d(TAG, "llamarApiHistory: fechaInicial $fechaInicial")

                // Realizar las solicitudes a la API
                val responseHistory7D = apiService.getDollarHistory7D()
                val responseHistory30D = apiService.getDollarHistory30D()


                // Procesar las respuestas
               // val (dates, pricesBcv, pricesParalelo) = convertirResponseApiHistory(responseHistory7D, responseHistory30D)

                withContext(Dispatchers.Main) {
                    // Guarda y establece ambas respuestas
                    HomeFragment.ApiResponseHolder.setResponseHistory7D(responseHistory7D)
                    HomeFragment.ApiResponseHolder.setResponseHistory3D(responseHistory30D)
                    binding.progressBar.visibility = View.GONE
                    guardarResponseHistory7D(requireContext(), responseHistory7D)
                    guardarResponseHistory30D(requireContext(), responseHistory30D)
                    cargarGrafico(nombreInstitucion)

                    // Actualiza la UI con los datos obtenidos de ambas respuestas
                    // Por ejemplo, puedes mostrar ambos datos en gráficos separados o combinados
                }
            } catch (e: Exception) {
                Log.e(TAG, "llamarApiHistory: Error al llamar a la API: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Problemas de Conexión",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } finally {
                // Cualquier código adicional que deba ejecutarse al finalizar la solicitud
            }
        }
    }

    private fun cargarGrafico(nombreInstitucion: String) {
        // Determinar si está en modo oscuro o claro
        val isDarkTheme = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        val primaryTextColor = if (isDarkTheme) Color.WHITE else Color.BLACK

        val responseHistory7D = HomeFragment.ApiResponseHolder.getResponseHistory7D()
        val responseHistory30D = HomeFragment.ApiResponseHolder.getResponseHistory30D()
        Log.d(TAG, "cargarGrafico:responseHistory74 $responseHistory7D responseHistory30D $responseHistory30D")
        val (dates, dolarVentaValues) = convertirResponseApiHistory(responseHistory7D!!, nombreInstitucion)
        Log.d(TAG, "cargarGrafico: dates $dates dolarVentaValues: $dolarVentaValues")

        // Filtrar datos para no sobresaturar el gráfico
        val filterInterval = 1 // Por ejemplo, cada 3 datos
        val filteredDates = dates.filterIndexed { index, _ -> index % filterInterval == 0 }
        val filteredDolarVentaValues = dolarVentaValues.filterIndexed { index, _ -> index % filterInterval == 0 }

        val entriesVenta = filteredDolarVentaValues.mapIndexed { index, value ->
            Entry(index.toFloat(), value)
        }

        val dataSetVenta = LineDataSet(entriesVenta, nombreInstitucion).apply {
            color = Color.BLUE
            valueTextColor = primaryTextColor
            valueTextSize = 14f
            lineWidth = 3f
        }

        val lineData = LineData(dataSetVenta)
        binding.lineChart.fitScreen()
        binding.lineChart.data = lineData

        val xAxis = binding.lineChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(filteredDates)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = primaryTextColor
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = -45f

        binding.lineChart.description.isEnabled = false

        val description = Description().apply {
            text = "Precio de Venta  $nombreInstitucion"
            textColor = primaryTextColor
            textSize = 10f
        }
        binding.lineChart.description = description

        val legend = binding.lineChart.legend
        legend.textSize = 16f
        legend.textColor = primaryTextColor
        legend.formSize = 14f
        legend.xEntrySpace = 20f

        binding.lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                e?.let {
                    val index = it.x.toInt()
                    val valores = mutableListOf<String>()

                    // Verificar y agregar valores según disponibilidad
                    if (index in filteredDates.indices) {
                        val fecha = filteredDates[index] ?: "N/A"
                        valores.add("Fecha: $fecha")
                    }
                    if (index in filteredDolarVentaValues.indices) {
                        val dolarVenta = filteredDolarVentaValues[index]
                        valores.add("Dólar Venta: $dolarVenta")
                    }

                    // Mostrar los valores disponibles o un mensaje de no disponible
                    binding.txtValores.text = if (valores.isNotEmpty()) valores.joinToString("\n") else "Valores no disponibles"
                    binding.txtValores.setTextColor(primaryTextColor)
                    //animacionCrecerTexto(binding.txtValores)
                } ?: run {
                    binding.txtValores.text = "No se seleccionó ningún valor"
                    binding.txtValores.setTextColor(primaryTextColor)
                }
            }

            override fun onNothingSelected() {
                // No hacer nada si no se selecciona nada
            }
        })

        binding.lineChart.invalidate()
    }



    fun convertirResponseApiHistory(
        responseApiHistory7D: List<HistoryModelResponseItem>,
        nombreInstitucion: String
    ): Pair<List<String>, List<Float>> {
        val dates = mutableListOf<String>()
        val precioVenta = mutableListOf<Float>()

        // Filtrar y procesar los datos solo para el banco "BBVBA"
        responseApiHistory7D.filter { it.name == nombreInstitucion }.forEach { historicalItem ->
            historicalItem.historical.forEach { historical ->
                val dateOnly = historical.date.split("T")[0] // Extraer solo la fecha (sin la hora)
                dates.add(dateOnly)
                precioVenta.add(historical.sell.toFloat())
            }
        }
        Log.d(TAG, "convertirResponseApiHistory: Return dates $dates precioVenta $precioVenta ")
        // Invertir el orden de las listas antes de devolverlas
        return Pair(dates.reversed(), precioVenta.reversed())
    }

    //Obtieme el nombre de las Intituciones para mostrar en el espiner
    fun obtenerNombresInstituciones(responseApiHistory: ApiMexicoResponse?): List<String> {
        val nombresInstituciones = mutableListOf<String>()

        responseApiHistory?.forEach { item ->
            nombresInstituciones.add(item.name)
        }

        return nombresInstituciones
    }



    private fun guardarResponseHistory7D(context: Context, responseHistory: List<HistoryModelResponseItem>) {
        val gson = Gson()
        val responseJson = gson.toJson(responseHistory)

        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MyPreferences", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("ResponseHistory7D", responseJson)
        editor.apply()

    }

    private fun guardarResponseHistory30D(context: Context, responseHistory: List<HistoryModelResponseItem>) {
        val gson = Gson()
        val responseJson = gson.toJson(responseHistory)

        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MyPreferences", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("ResponseHistory30D", responseJson)
        editor.apply()

    }
    private fun getResponseFromSharedPreferences7D(context: Context): ApiMexicoResponse? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MyPreferences", AppCompatActivity.MODE_PRIVATE)
        val responseJson = sharedPreferences.getString("ResponseHistory7D", null)

        if (responseJson != null) {
            val gson = Gson()

            return gson.fromJson(responseJson, ApiMexicoResponse::class.java)
        }

        return null // Retorna null si no se encontró la respuesta en SharedPreferences
    }








    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
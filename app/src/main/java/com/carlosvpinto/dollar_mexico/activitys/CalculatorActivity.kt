package com.carlosvpinto.dollar_mexico.activitys

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.carlosvpinto.dollar_mexico.R
import com.carlosvpinto.dollar_mexico.databinding.ActivityCalculatorBinding
import com.carlosvpinto.dollar_mexico.databinding.ActivityMainBinding
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CalculatorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalculatorBinding
    private val TAG = "Activity_Calculator"
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recuperar los datos del Intent
        val nombre = intent.getStringExtra("nombre")
        val montoCompra = intent.getDoubleExtra("montoCompra", 0.0)
        val montoVenta = intent.getDoubleExtra("montoVenta", 0.0)
        val fechaActu = intent.getStringExtra("fechaActu")

        val image = intent.getStringExtra("image")
        Log.d(TAG, "onCreate: ")





        // Usar los datos en la actividad
        binding.textViewNombre.text = nombre
        binding.textViewMontoCompra .text = montoCompra.toString()
        binding.textViewMontoVenta.text = montoVenta.toString()
        binding.textViewFechaActu.text = fechaActu?.let { convertirUTCaLocal(it) }

        // Cargar la imagen
        Glide.with(this)
            .load(image)
            .placeholder(R.drawable.institution_svgrepo_com)
            .error(R.drawable.institution_svgrepo_com)
            .into(binding.imageView)

        binding.btnCerrarCalculator.setOnClickListener {
            cerrarYVolverAlMainActivity()
        }

    }
    // Manejar el click en la flecha de "atrás"
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed() // Vuelve a la actividad anterior
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun convertirUTCaLocal(utcDateTime: String): String {
        // Formato del datetime de entrada
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")

        // Parsear el datetime de entrada a LocalDateTime
        val localDateTime = LocalDateTime.parse(utcDateTime, formatter)

        // Convertir a ZonedDateTime en UTC
        val zonedUTC = localDateTime.atZone(ZoneId.of("UTC"))

        // Convertir a la hora local del sistema
        val zonedLocal = zonedUTC.withZoneSameInstant(ZoneId.systemDefault())

        // Formatear la salida en el formato deseado
        val formatterSalida = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return zonedLocal.format(formatterSalida)
    }

    private fun cerrarYVolverAlMainActivity() {
        finish() // Cierra la actividad actual
    }

}
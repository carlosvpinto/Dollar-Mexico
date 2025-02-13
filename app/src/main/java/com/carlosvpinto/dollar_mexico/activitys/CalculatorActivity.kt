package com.carlosvpinto.dollar_mexico.activitys

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
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
import android.text.TextWatcher
import android.text.Editable
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.Toast
import com.carlosvpinto.dollar_mexico.model.ApiMexicoResponseItem
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class CalculatorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCalculatorBinding
    private val TAG = "Activity_Calculator"
    private var usd_A_MXN = true

    private var montoCompra: Double = 0.0
    private var montoVenta: Double = 0.0

    private lateinit var preciosBancosMX: ArrayList<ApiMexicoResponseItem>
    private var itemIndex: Int = -1
    private var nombre=""
    private var fechaActu = ""
    private var image = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recuperar los datos del Intent


        preciosBancosMX = intent.getParcelableArrayListExtra<ApiMexicoResponseItem>("listaPrecios") ?: arrayListOf()
        itemIndex = intent.getIntExtra("itemIndex", -1)

        if (itemIndex != -1) {
            val activeItem = preciosBancosMX[itemIndex]
            cargarDatos(activeItem)
        }

        binding.imgCambiobandera.setOnClickListener {
            if (usd_A_MXN){
                usd_A_MXN= false
                cambiaDeDolarAPesoyVice(binding.textViewMXN, binding.textViewUSD,binding.textViewMXN2, binding.textViewUSD2)
            }else{
                usd_A_MXN= true
                cambiaDeDolarAPesoyVice(binding.textViewUSD, binding.textViewMXN,binding.textViewUSD2, binding.textViewMXN2)
            }

        }
        detectarDeslizamiento(binding.imageView)


        // Escuchar cambios en el TextInputEditText
        binding.inputDolares.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                calcularMontos()
            }
        })



        llenardatos()
        binding.btnCerrarCalculator.setOnClickListener {
            cerrarYVolverAlMainActivity()
        }

    }
    // Función para procesar el siguiente elemento

    private fun processNextItem(subir: Boolean) {
        if (itemIndex in 0 until preciosBancosMX.size) {
            val item = preciosBancosMX[itemIndex]
            // Ejecuta la acción deseada con el elemento actual
            cargarDatos(item)
            llenardatos()
            calcularMontos()

            if (subir) {
                itemIndex++ // Incrementa el índice para el siguiente elemento
            } else {
                itemIndex-- // Decrementa el índice para el elemento anterior
            }
        } else {
            // Maneja el caso cuando el índice está fuera de los límites
            println("Índice fuera de los límites: $itemIndex")

            if (itemIndex >= preciosBancosMX.size) {
                itemIndex = preciosBancosMX.size - 1 // Ajusta el índice al último elemento válido
            } else if (itemIndex < 0) {
                itemIndex = 0 // Ajusta el índice al primer elemento válido
            }
        }
    }


    private fun cargarDatos(activeItem: ApiMexicoResponseItem) {
        // Aquí puedes usar el activeItem para lo que necesites
        nombre = activeItem.name
        montoCompra = activeItem.buy
        montoVenta = activeItem.sell
        fechaActu = activeItem.date
        image = activeItem.image

    }


    private fun llenardatos() {
        // Formatear los valores a dos decimales
        val formato = "%.2f"
        val montoCompraFormatted = String.format(formato, montoCompra)
        val montoVentaFormatted = String.format(formato, montoVenta)

        // Usar los datos en la actividad
        binding.textViewNombre.text = nombre
        binding.textViewMontoCompra.text = montoCompraFormatted
        binding.textViewMontoVenta.text = montoVentaFormatted
        binding.textViewFechaActu.text = fechaActu?.let { convertirUTCaLocal(it) }

        // Cargar la imagen
        Glide.with(this)
            .load(image)
            .placeholder(R.drawable.institution_svgrepo_com)
            .error(R.drawable.institution_svgrepo_com)
            .into(binding.imageView)
    }


    private fun calcularMontos() {
        // Obtener el valor del TextInputEditText
        val inputValue = binding.inputDolares.text.toString()

        // Si el input está vacío, usar 1 como valor predeterminado
        val valor = if (inputValue.isNotEmpty() && inputValue.toDoubleOrNull() != null) {
            inputValue.toDouble()
        } else {
            1.0 // Valor predeterminado cuando el input está vacío
        }

        var resultadoCompra = 0.0
        var resultadoVenta = 0.0

        if (usd_A_MXN) {
            // Calcular los montos
            resultadoCompra = valor * (montoCompra ?: 0.0)
            resultadoVenta = valor * (montoVenta ?: 0.0)
        } else {
            resultadoCompra = if (montoCompra != null && montoCompra != 0.0) valor / montoCompra else 0.0
            resultadoVenta = if (montoVenta != null && montoVenta != 0.0) valor / montoVenta else 0.0
        }

        // Mostrar los resultados en los TextView (sin texto adicional, solo el número formateado)
        binding.textViewMontoCompra.text = "%.2f".format(resultadoCompra)
        binding.textViewMontoVenta.text = "%.2f".format(resultadoVenta)
    }


    private fun cambiaDeDolarAPesoyVice(textoBajar: TextView, textoMostrar: TextView,textoBajar2: TextView, textoMostrar2: TextView) {

        //Cambia la propiedad Hint del Inputext*************
        if (usd_A_MXN){
            binding.edtxtDolares.hint= "Monto en Dolares"
            cambiarBanderas(0f,0f)
        }else{
            binding.edtxtDolares.hint= "Monto en Pesos MXN"
            cambiarBanderas(-1f, 1f)
        }

        calcularMontos()

        //Animacion Fecha**************************
        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_anim)
        binding.imgCambiobandera.startAnimation(rotateAnimation)


        //Animacion Letras de Simbolo**************
        val slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down)
        val slideInFromTop = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_top)
        // Configurar un listener para la animación de "MXN"
        slideDown.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                // No es necesario hacer nada aquí
            }

            override fun onAnimationEnd(animation: Animation?) {
                // Ocultar el TextView "MXN" y mostrar el TextView "USD"
               textoBajar.visibility = View.GONE
                textoMostrar.visibility = View.VISIBLE
                textoMostrar.startAnimation(slideInFromTop)
                textoBajar2.visibility = View.GONE
                textoMostrar2.visibility = View.VISIBLE
                textoMostrar2.startAnimation(slideInFromTop)
            }

            override fun onAnimationRepeat(animation: Animation?) {
                // No es necesario hacer nada aquí
            }
        })

        // Iniciar la animación de "MXN"
        textoBajar.startAnimation(slideDown)
        textoBajar2.startAnimation(slideDown)


    }

    private fun cambiarBanderas(izq: Float, derech: Float) {
        // Obtener el ancho de la pantalla en píxeles
        val displayMetrics = Resources.getSystem().displayMetrics
        val screenWidth = displayMetrics.widthPixels

        // Calcular el desplazamiento como el 25% del ancho de la pantalla
        val desplazamiento = screenWidth * 0.28f
        Log.d(TAG, "cambiarBanderas: desplazamiento $desplazamiento")
        // Aplicar el desplazamiento positivo o negativo según las variables izq y derech
        val desplazamientoIzquierda = desplazamiento * izq
        val desplazamientoDerecha = desplazamiento * derech

        // Animaciones para mover las imágenes
        val animatorUsa = ObjectAnimator.ofFloat(binding.imgBandeUsa, "translationX", desplazamientoDerecha)
        val animatorMexico = ObjectAnimator.ofFloat(binding.imgBandeMexico, "translationX", desplazamientoIzquierda)

        animatorUsa.duration = 500
        animatorMexico.duration = 500

        // Iniciar las animaciones
        animatorUsa.start()
        animatorMexico.start()
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
    fun convertirUTCaLocal(utcDateTime: String): String {
        // Formato del datetime de entrada
        val formatterEntrada = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
        formatterEntrada.timeZone = TimeZone.getTimeZone("UTC")

        // Parsear el datetime de entrada a Date
        val date = formatterEntrada.parse(utcDateTime)

        // Convertir a la hora local del sistema
        val formatterSalida = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        formatterSalida.timeZone = TimeZone.getDefault()

        return formatterSalida.format(date)
    }

    private fun detectarDeslizamiento(imageView: ImageView) {
        // Definir el GestureDetector para detectar gestos específicos
        val gestureDetector = GestureDetector(imageView.context, object : GestureDetector.SimpleOnGestureListener() {
            private val SWIPE_THRESHOLD = 50  // Umbral de deslizamiento más bajo
            private val SWIPE_VELOCITY_THRESHOLD = 50  // Umbral de velocidad de deslizamiento más bajo

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 != null && e2 != null) {
                    val diffY = e2.y - e1.y
                    val diffX = e2.x - e1.x

                    if (Math.abs(diffY) > Math.abs(diffX)) {
                        if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffY < 0) {
                                // Deslizó hacia arriba
                                processNextItem(true)
                                //Toast.makeText(imageView.context, "Se movió para arriba", Toast.LENGTH_SHORT).show()
                            } else {
                                // Deslizó hacia abajo
                                processNextItem(false)
                                //Toast.makeText(imageView.context, "Se movió hacia abajo", Toast.LENGTH_SHORT).show()
                            }
                            return true
                        }
                    }
                }
                return false
            }
        })

        // Establecer el OnTouchListener para la imagen
        imageView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }




    private fun cerrarYVolverAlMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("openFragment", "home")
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish() // Cierra la actividad actual
    }


}
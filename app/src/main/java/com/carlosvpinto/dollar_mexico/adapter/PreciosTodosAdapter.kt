package com.carlosvpinto.dollar_mexico.adapter

import android.app.Activity
import android.content.Intent
import android.os.Build

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi

import androidx.cardview.widget.CardView

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.carlosvpinto.dollar_mexico.R
import com.carlosvpinto.dollar_mexico.activitys.CalculatorActivity
import com.carlosvpinto.dollar_mexico.model.ApiMexicoResponseItem
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

class PreciosTodosAdapter(
    val context: Activity,
    var preciosBancosMX: ArrayList<ApiMexicoResponseItem>,
   // private val navController: NavController // Recibe el NavController de la actividad o fragmento
) : RecyclerView.Adapter<PreciosTodosAdapter.BancosMXAdapterViewHolder>() {

    //private lateinit var navController: NavController
    private var itemCount: Int = 0 // variable para almacenar la cantidad de elementos en la lista

    init {


        itemCount = preciosBancosMX.size
    }

//**************************************************************

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BancosMXAdapterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_precio_instituciones, parent, false)
        return BancosMXAdapterViewHolder(view)
    }


    override fun onBindViewHolder(holder: BancosMXAdapterViewHolder, position: Int) {
        val bancoMX = preciosBancosMX[position]

        holder.textViewFechaActu.text = convertirUTCaLocal(bancoMX.date)
        holder.textViewMontoCompra.text = bancoMX.buy.toString()
        holder.textViewMontoVenta.text = bancoMX.sell.toString()
        holder.textViewNombreBanco.text = bancoMX.name

        Glide.with(holder.itemView.context)
            .load(bancoMX.image)
            .placeholder(R.drawable.institution_svgrepo_com)
            .error(R.drawable.institution_svgrepo_com)
            .into(holder.imgCircleInsti)

        // Manejar el click en el itemView
        holder.itemView.setOnClickListener {
                goToDetail(position)
             }



        }

    private fun goToDetail(position: Int) {
        val intent = Intent(context, CalculatorActivity::class.java).apply {
            putParcelableArrayListExtra("listaPrecios", preciosBancosMX)
            putExtra("itemIndex", position)
        }
        context.startActivity(intent)
    }




    // EL TAMAñO DE LA LISTA QUE VAMOS A MOSTRAR
    override fun getItemCount(): Int {
        return preciosBancosMX.size
    }
    fun updatePrecioMexico(precioMexicoList: List<ApiMexicoResponseItem> ){
        Log.d("ADAPTER", " DENTRO updatePrecioBancos precionBancosList $precioMexicoList ")
        this.preciosBancosMX = precioMexicoList as ArrayList<ApiMexicoResponseItem>
        notifyDataSetChanged()
    }

    class BancosMXAdapterViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val textViewFechaActu: TextView
        val textViewMontoCompra: TextView
        val textViewMontoVenta: TextView
        val textViewNombreBanco: TextView
        val cardView: CardView // Nueva referencia a la CardView
        val imgLogo: ImageView

        val imgCircleInsti: CircleImageView

        init {
            textViewFechaActu = view.findViewById(R.id.txtFechaActualizacionBanco)
            textViewMontoCompra = view.findViewById(R.id.txtPrecioCompra)
            textViewMontoVenta = view.findViewById(R.id.txtPrecioVenta)

            imgLogo = view.findViewById(R.id.circleInstitucion)
            textViewNombreBanco = view.findViewById(R.id.txtNombreBanco)
            cardView = view.findViewById(R.id.cardView) // Inicializar la referencia a la CardView

            imgCircleInsti= view.findViewById(R.id.circleInstitucion)


        }
    }

    fun convertirUTCaLocal(utcDateTime: String): String {
        // Formato del datetime de entrada
        val formatterEntrada = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
        formatterEntrada.timeZone = TimeZone.getTimeZone("UTC")

        // Parsear el datetime de entrada a Date
        val date = formatterEntrada.parse(utcDateTime)

        // Convertir a la hora local del sistema
        val formatterSalida = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault())
        formatterSalida.timeZone = TimeZone.getDefault()

        return formatterSalida.format(date)
    }

}
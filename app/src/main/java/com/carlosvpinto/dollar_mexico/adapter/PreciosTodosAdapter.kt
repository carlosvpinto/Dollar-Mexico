package com.carlosvpinto.dollar_mexico.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.carlosvpinto.dollar_mexico.R
import com.carlosvpinto.dollar_mexico.model.PreciosModelAdap
import de.hdodenhof.circleimageview.CircleImageView

class PreciosTodosAdapter(val context: Fragment, var otrosBancos: ArrayList<PreciosModelAdap>): RecyclerView.Adapter<PreciosTodosAdapter.OtrosBancosAdapterViewHolder>() {



    private var itemCount: Int = 0 // variable para almacenar la cantidad de elementos en la lista

    init {
        var totalBs = 0.0
        var totalDollar= 0.0
        var totalSinVeriBs = 0.0
        var totalSinVeriBsDollar = 0.0


        Log.d("RESPUESTA", "otrasPaginas.size: ${otrosBancos.size} ")
       // val textView = context.findViewById<TextView>(R.id.txtRespuesta)

        itemCount = otrosBancos.size
    }
//**************************************************************

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtrosBancosAdapterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_precio_instituciones, parent, false)
        return OtrosBancosAdapterViewHolder(view)
    }

    // ESTABLECER LA INFORMACION
    override fun onBindViewHolder(holder: OtrosBancosAdapterViewHolder, position: Int) {
        val otroBanco =   otrosBancos[position] // UN SOLO HISTORIAL
        holder.textViewFechaActu.text = otroBanco.last_update
        holder.textViewMontoBs.text = otroBanco.precio.toString()
        holder.textViewNombreBanco.text = otroBanco.nombre
        holder.textViewVariacion.text = otroBanco.diferencia.toString()
       // holder.imgflecha.setImageResource(R.drawable.ic_flechaverde)
        Log.d("ADAPTER", " otroBanco.nombre ${otroBanco.nombre} ")

        holder.imgLogo.visibility = View.VISIBLE
        holder.imgCircleInsti.visibility= View.GONE


        // holder.itemView.setOnClickListener { goToDetail(pagoMovil?.id!!) } //para no llamar al activity al gacer click
    }
    fun mostrarFlecha(color:String,holder: OtrosBancosAdapterViewHolder){
        val contexto = holder.itemView.context
        if (color== "green"){
            holder.imgflecha.setImageResource(R.drawable.ic_flechaverde)
            holder.textViewVariacion.setTextColor(ContextCompat.getColor(contexto,R.color.green))
        }
        if (color== "red"){
            holder.imgflecha.setImageResource(R.drawable.ic_flecha_roja)
            holder.textViewVariacion.setTextColor(ContextCompat.getColor(contexto,R.color.red))
        }
        if (color== "neutral"){
            holder.imgflecha.setImageResource(R.drawable.ic_flecha_igual)
            holder.textViewVariacion.setTextColor(ContextCompat.getColor(contexto,R.color.black))
        }
    }


    // EL TAMAñO DE LA LISTA QUE VAMOS A MOSTRAR
    override fun getItemCount(): Int {
        return otrosBancos.size
    }
    fun updatePrecioBancos(precionBancosList: List<PreciosModelAdap> ){
        Log.d("ADAPTER", " DENTRO updatePrecioBancos precionBancosList $precionBancosList ")
        this.otrosBancos = precionBancosList as ArrayList<PreciosModelAdap>
        notifyDataSetChanged()
    }

    class OtrosBancosAdapterViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val textViewFechaActu: TextView
        val textViewMontoBs: TextView
        val textViewNombreBanco: TextView
        val textViewVariacion: TextView
        val cardView: CardView // Nueva referencia a la CardView
        val imgLogo: ImageView
        val imgflecha: ImageView
        val imgCircleInsti: CircleImageView

        init {
            textViewFechaActu = view.findViewById(R.id.txtFechaActualizacionBanco)
            textViewMontoBs = view.findViewById(R.id.txtPrecioBsBanco)
            textViewVariacion = view.findViewById(R.id.txtVariacion)
            imgLogo = view.findViewById(R.id.circleInstitucion)
            textViewNombreBanco = view.findViewById(R.id.txtNombreBanco)
            cardView = view.findViewById(R.id.cardView) // Inicializar la referencia a la CardView
            imgflecha= view.findViewById(R.id.imgfechabanco)
            imgCircleInsti= view.findViewById(R.id.circleInstitucion)


        }
    }

}
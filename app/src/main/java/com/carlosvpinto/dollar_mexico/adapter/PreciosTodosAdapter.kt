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
import com.bumptech.glide.Glide
import com.carlosvpinto.dollar_mexico.R
import com.carlosvpinto.dollar_mexico.model.ApiMexicoResponse
import com.carlosvpinto.dollar_mexico.model.ApiMexicoResponseItem
import com.carlosvpinto.dollar_mexico.model.PreciosModelAdap
import de.hdodenhof.circleimageview.CircleImageView

class PreciosTodosAdapter(val context: Fragment, var preciosBancosMX: ArrayList<ApiMexicoResponseItem>): RecyclerView.Adapter<PreciosTodosAdapter.BancosMXAdapterViewHolder>() {



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

    // ESTABLECER LA INFORMACION
    override fun onBindViewHolder(holder: BancosMXAdapterViewHolder, position: Int) {
        val bancoMX =   preciosBancosMX[position] // UN SOLO HISTORIAL
        holder.textViewFechaActu.text = bancoMX.date
        holder.textViewMontoBs.text = bancoMX.buy.toString()
        holder.textViewNombreBanco.text = bancoMX.name
        holder.textViewVariacion.text = bancoMX.sell.toString()
       // holder.imgflecha.setImageResource(R.drawable.ic_flechaverde)
        Log.d("ADAPTER", " otroBanco.nombre ${bancoMX.name} ")

        Glide.with(context)
            .load(bancoMX.image)
            .placeholder(R.drawable.institution_svgrepo_com)  // Imagen de placeholder mientras se carga la URL
            .error(R.drawable.institution_svgrepo_com)    // Imagen que se muestra si ocurre un error
            .into(holder.imgCircleInsti)



        // holder.itemView.setOnClickListener { goToDetail(pagoMovil?.id!!) } //para no llamar al activity al gacer click
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
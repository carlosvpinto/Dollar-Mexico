package com.carlosvpinto.dollar_mexico.ui.home

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.carlosvpinto.dollar_mexico.adapter.PreciosTodosAdapter
import com.carlosvpinto.dollar_mexico.databinding.FragmentHomeBinding
import com.carlosvpinto.dollar_mexico.model.ApiMexicoResponse
import com.carlosvpinto.dollar_mexico.model.history.HistoryModelResponse
import com.carlosvpinto.dollar_mexico.model.history.HistoryModelResponseItem
import com.carlosvpinto.dollar_mexico.utils.ApiService
import com.carlosvpinto.dollar_mexico.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val TAG = "HomeFragment"
    private lateinit var navController: NavController

    private lateinit var adapter: PreciosTodosAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.recyclerBancosMX.layoutManager = linearLayoutManager

        // Inicializa el NavController utilizando el view que ya se creó
       // navController = findNavController()
        // Crear una instancia del adaptador
        //val adapter = PreciosTodosAdapter(this, ArrayList(), childFragmentManager


        // Obtener el NavController
        //navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main)
        adapter = PreciosTodosAdapter(requireActivity(), arrayListOf())
        //adapter = PreciosTodosAdapter(this@HomeFragment, ArrayList())
        binding.recyclerBancosMX.adapter = adapter

        binding.btnRefresh.setOnClickListener {
            actualizarApi()
        }
        actualizarApi()


        binding.editTexFiltroC.addTextChangedListener { userfilter ->
           val institucionesList = ApiResponseHolder.getResponse()
            val bancosFiltrados = institucionesList?.filter { banco->
                banco.name.lowercase()?.contains(userfilter.toString().lowercase())== true}
            if (bancosFiltrados != null) {
                adapter?.updatePrecioMexico(bancosFiltrados)
            }
        }

        return root
    }

    private fun actualizarApi() {
        comenzarCarga()
        llamarApiMexico { isSuccessful ->

            if (isSuccessful) {
                finalizarCarga()
                Log.d(TAG, "onCreateView: $isSuccessful")
            } else {
                finalizarCarga()
                Log.d(TAG, "onCreateView: $isSuccessful")
            }
        }
    }

    private fun comenzarCarga() {
        binding.viewLoading.isVisible = true
        binding.recyclerBancosMX.isVisible = false
    }

    private fun finalizarCarga() {
        binding.viewLoading.isVisible = false
        binding.recyclerBancosMX.isVisible = true
    }

    fun llamarApiMexico(callback: (Boolean) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val baseUrl = Constants.URL_BASE // URL base

            val client = OkHttpClient.Builder().build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            val apiService = retrofit.create(ApiService::class.java)

            try {
                val responseApiMexico = apiService.getDollar()
                if (responseApiMexico != null) {
                    withContext(Dispatchers.Main) {
                        Log.d(TAG, "llamarApiMexico: $responseApiMexico")
                        ApiResponseHolder.setResponse(responseApiMexico)
                        llamarBancosMXFragment(responseApiMexico)
                        callback(true)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Problemas de Conexion", Toast.LENGTH_SHORT).show()
                    callback(false)
                }
            } finally {
                withContext(Dispatchers.Main) {
                    callback(false)
                }
            }
        }
    }

    fun llamarBancosMXFragment(response: ApiMexicoResponse) {
        try {
            Log.d(TAG, "VALOR DEL RESPONSE en bancosFragment $response")
            if (response != null) {
                adapter.updatePrecioMexico(response)

                // Crear un SmoothScroller con interpolación personalizada para efecto más suave
                val smoothScroller = object : LinearSmoothScroller(requireContext()) {
                    override fun getVerticalSnapPreference(): Int {
                        return SNAP_TO_START // Para que se desplace al inicio de la lista
                    }

                    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                        // Ajustar la velocidad del scroll (a menor valor, más lento)
                        return 60f / displayMetrics.densityDpi
                    }
                }

                // Fijar la posición objetivo para el smoothScroller
                smoothScroller.targetPosition = 0
                binding.recyclerBancosMX.layoutManager?.startSmoothScroll(smoothScroller)
            }
        } catch (e: Exception) {
            Log.d("llamarPrecioOtros", "ERROR DE RESPONSE $e")
            Toast.makeText(requireContext(), "No Actualizo dolar BCV Revise Conexion $e", Toast.LENGTH_LONG).show()
            println("Error: ${e.message}")
        }
    }
    //INTERFACE PARA COMUNICAR CON EL ACTIVITY
    object ApiResponseHolder {
        private var responseApiMexico: ApiMexicoResponse? = null
        private var responseHistory7D: List<HistoryModelResponseItem>? = null
        private var responseHistory30D: List<HistoryModelResponseItem>? = null

        fun getResponse(): ApiMexicoResponse? {
            return responseApiMexico
        }


        fun setResponse(newResponse: ApiMexicoResponse) {
            responseApiMexico = newResponse
        }

        fun setResponseHistory7D(ResponseHistory: List<HistoryModelResponseItem>) {
            responseHistory7D = ResponseHistory
        }
        fun setResponseHistory3D(ResponseHistory: List<HistoryModelResponseItem>) {
            responseHistory30D = ResponseHistory
        }
        fun getResponseHistory7D(): List<HistoryModelResponseItem>? {
            return responseHistory7D
        }

        fun getResponseHistory30D(): List<HistoryModelResponseItem>? {
            return responseHistory30D
        }



    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}






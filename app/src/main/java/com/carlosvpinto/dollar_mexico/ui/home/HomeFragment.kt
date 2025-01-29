package com.carlosvpinto.dollar_mexico.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.carlosvpinto.dollar_mexico.adapter.PreciosTodosAdapter
import com.carlosvpinto.dollar_mexico.databinding.FragmentHomeBinding
import com.carlosvpinto.dollar_mexico.model.ApiMexicoResponse
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

    private lateinit var adapter: PreciosTodosAdapter
    val bancosList = mutableListOf<PreciosTodosAdapter>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.recyclerBancosMX.layoutManager = linearLayoutManager

        // Inicializar el adaptador y asignarlo al RecyclerView
        adapter = PreciosTodosAdapter(this@HomeFragment, ArrayList())
        binding.recyclerBancosMX.adapter = adapter


        llamarApiMexico { isSuccessful ->
            if (isSuccessful) {
                Log.d(TAG, "onCreateView: $isSuccessful")
            } else {
                Log.d(TAG, "onCreateView: $isSuccessful")
            }
        }


//        binding.editTexFiltroC.addTextChangedListener { userfilter ->
//            val bancosFiltrados = bancosList.filter { banco->
//                banco.nombre?.lowercase()?.contains(userfilter.toString().lowercase())== true}
//            adapter?.updatePrecioBancos(bancosFiltrados)
//        }

        return root
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
            }
        } catch (e: Exception) {
            Log.d("llamarPrecioOtros", "ERROR DE RESPONSE $e")
            Toast.makeText(requireContext(), "No Actualizo dolar BCV Revise Conexion $e", Toast.LENGTH_LONG).show()
            println("Error: ${e.message}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


    //INTERFACE PARA COMUNICAR CON EL ACTIVITY
    object ApiResponseHolder {
        private var responseApiMexico: ApiMexicoResponse? = null

        fun getResponse(): ApiMexicoResponse? {
            return responseApiMexico
        }


        fun setResponse(newResponse: ApiMexicoResponse) {
            responseApiMexico = newResponse
        }


    }



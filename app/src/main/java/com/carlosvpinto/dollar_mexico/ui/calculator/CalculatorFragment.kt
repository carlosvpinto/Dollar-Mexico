package com.carlosvpinto.dollar_mexico.ui.calculator

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.carlosvpinto.dollar_mexico.R
import com.carlosvpinto.dollar_mexico.databinding.FragmentCalculatorBinding
import com.carlosvpinto.dollar_mexico.databinding.FragmentHomeBinding
import com.carlosvpinto.dollar_mexico.ui.home.HomeViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private var _binding: FragmentCalculatorBinding? = null
private val binding get() = _binding!!
class CalculatorFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private val  TAG = "Fragment_Calculator"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Recibir los datos del Bundle
        arguments?.let { bundle ->
            val nombreBanco = bundle.getString("nombreBanco")
            val montoCompra = bundle.getDouble("montoCompra")
            val montoVenta = bundle.getDouble("montoVenta")
            val fechaActu = bundle.getString("fechaActu")
            val image = bundle.getString("image")


            Log.d(TAG, "onCreate: nombreBanco $nombreBanco montoCompra $montoCompra")
            // Usar los datos como necesites
            // Por ejemplo, mostrarlos en TextViews o ImageViews
        }



    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener los datos del Bundle
        val nombreBanco = arguments?.getString("nombreBanco")
        val montoCompra = arguments?.getDouble("montoCompra")
        val montoVenta = arguments?.getDouble("montoVenta")
        val fechaActu = arguments?.getString("fechaActu")
        val image = arguments?.getString("image")
        binding.txtBuyInst.text = montoCompra.toString()
        binding.txtNameInst.text = nombreBanco
        // Ahora puedes usar estos datos para mostrar en tu CalculatorFragment
        Log.d("CalculatorFragment", "Banco: $nombreBanco, Compra: $montoCompra, Venta: $montoVenta $fechaActu image $image")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    companion object {

        fun newInstance(param1: String, param2: String) =
            CalculatorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
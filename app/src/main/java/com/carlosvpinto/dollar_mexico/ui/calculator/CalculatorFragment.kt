package com.carlosvpinto.dollar_mexico.ui.calculator

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.carlosvpinto.dollar_mexico.R
import com.carlosvpinto.dollar_mexico.activitys.CalculatorActivity
import com.carlosvpinto.dollar_mexico.databinding.FragmentCalculatorBinding
import com.carlosvpinto.dollar_mexico.databinding.FragmentHomeBinding
import com.carlosvpinto.dollar_mexico.ui.home.HomeFragment
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

        goToDetail(1)



    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }
    private fun goToDetail(position: Int) {
        val preciosBancosMX = HomeFragment.ApiResponseHolder.getResponse()
        val intent = Intent(context, CalculatorActivity::class.java).apply {
            putParcelableArrayListExtra("listaPrecios", preciosBancosMX)
            putExtra("itemIndex", position)
        }
        context?.startActivity(intent)
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
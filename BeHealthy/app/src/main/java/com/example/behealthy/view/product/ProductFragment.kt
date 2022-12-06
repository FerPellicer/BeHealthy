package com.example.behealthy.view.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.behealthy.databinding.FragmentProductBinding
import com.example.behealthy.model.data.Product

class ProductFragment : Fragment() {

    private var _binding: FragmentProductBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProductBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = arguments?.getSerializable("productData") as Product


        binding.productName.text = product.name
        binding.productBrand.text = product.productBrand
        binding.productRating.text = product.productRating
        binding.ingredients.text = product.ingredients


        // Cargar imagen del producto
        Glide.with(requireContext()).asBitmap().load(product.image).into(binding.productImage)



        // Rellenar datos de la tabla
        binding.kj.text = product.nutritionalInformation?.get("0")
        binding.kcal.text = product.nutritionalInformation?.get("1")
        binding.fat.text = product.nutritionalInformation?.get("2")
        binding.saturatedFats.text = product.nutritionalInformation?.get("3")
        binding.carbohydrates.text = product.nutritionalInformation?.get("4")
        binding.sugar.text = product.nutritionalInformation?.get("5")
        binding.fiber.text = product.nutritionalInformation?.get("6")
        binding.protein.text = product.nutritionalInformation?.get("7")
        binding.salt.text = product.nutritionalInformation?.get("8")


    }

    companion object {
        @JvmStatic
        fun newInstance() = ProductFragment()
    }
}
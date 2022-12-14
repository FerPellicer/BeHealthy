package com.example.behealthy.view.product

import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.behealthy.databinding.FragmentProductBinding
import com.example.behealthy.model.data.Product
import com.example.behealthy.model.utils.FaceColor.get_face_drawable


class ProductFragment : Fragment() {

    private var _binding: FragmentProductBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProductBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = arguments?.getSerializable("productData") as Product


        binding.productName.text = product.name
        binding.productBrand.text = product.productBrand
        binding.productRating.text = product.productRating + "/100"
        binding.ingredients.text = product.ingredients


        // Cargar imagen del producto
        Glide.with(requireContext()).asBitmap().load(product.image).into(binding.productImage)




        binding.face.setImageDrawable(get_face_drawable(requireContext(), product.productRating!!))


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


        for (market in product.supermarkets!!) {
            when(market) {

                "Alcampo" -> {
                    binding.alcampo.visibility = View.VISIBLE
                    binding.alcampo.setOnClickListener { openMaps("Alcampo") }
                }

                "Carrefour" -> {
                    binding.carrefour.visibility = View.VISIBLE
                    binding.carrefour.setOnClickListener { openMaps("Carrefour") }
                }

                "Hiperdino" -> {
                    binding.hiperdino.visibility = View.VISIBLE
                    binding.hiperdino.setOnClickListener { openMaps("Hiperdino") }
                }

                "Mercadona" -> {
                    binding.mercadona.visibility = View.VISIBLE
                    binding.mercadona.setOnClickListener { openMaps("Mercadona") }
                }

                "Spar" -> {
                    binding.spar.visibility = View.VISIBLE
                    binding.spar.setOnClickListener { openMaps("Spar") }
                }

            }
        }


    }

    private fun openMaps(market: String) {

        // Create an Intent with the action to view a location on a map
        val mapIntent = Intent(Intent.ACTION_VIEW)

        // Set the data for the map (in this case, a query for "supermarket" on Google Maps)
        mapIntent.data = Uri.parse("geo:0,0?q=${market}")
        startActivity(mapIntent)


    }

    companion object {
        @JvmStatic
        fun newInstance() = ProductFragment()
    }
}
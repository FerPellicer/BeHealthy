package com.example.behealthy.view.scanner

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.budiyev.android.codescanner.*
import com.bumptech.glide.Glide
import com.example.behealthy.R
import com.example.behealthy.databinding.FragmentScannerBinding
import com.example.behealthy.model.data.Product
import com.example.behealthy.viewModel.ProductViewModel
import com.example.fragments.data.Resource
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ScannerFragment : Fragment() {
    private var canShowMessage: Boolean = true
    private var _binding: FragmentScannerBinding? = null
    private val handler = Handler()

    private val binding get() = _binding!!

    private lateinit var codeScanner: CodeScanner

    private lateinit var product: Product

    var CAMERA_REQUEST_CODE = 101

    private lateinit var productViewModel: ProductViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpPermissions()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentScannerBinding.inflate(inflater, container, false)

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        val scannerView = binding.scannerView
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, scannerView)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false
        }

        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                showProduct(it.toString())
            }
        }

        codeScanner.errorCallback = ErrorCallback {
            activity.runOnUiThread {
                Toast.makeText(activity, "No se ha podido iniciar la cámara",
                    Toast.LENGTH_SHORT).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

        binding.closeButton.setOnClickListener { binding.product.visibility = View.INVISIBLE }
        binding.productButton.setOnClickListener { navigateToProduct(it) }
    }

    private fun navigateToProduct(view: View) {

        val bundle = Bundle()

        bundle.putSerializable("productData", product)

        Navigation.createNavigateOnClickListener(R.id.action_nav_scan_to_productFragment, bundle).onClick(view)

    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setUpPermissions(){
        val permissions = ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.CAMERA)

        if(permissions != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }
    }

    private fun makeRequest(){
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(activity, "Necesitas los permisos de la cámara para escanear un código qr", Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private fun showProduct(productId: String) {

        Log.e("Producto", "id: " + productId)

        if (productId.isDigitsOnly()) {

            productViewModel.productDataSnapshot(productId)
            productViewModel.productDataSnapshot.observe(viewLifecycleOwner) {
                it?.let {

                    try {
                        product = it.result.result.toObject(Product::class.java)!!

                        binding.productName.text = product.name

                        val imagen = product.image

                        Glide.with(this).asBitmap().load(imagen)
                            .into(binding.productImage)

                        binding.product.visibility = View.VISIBLE

                    } catch (e: Exception) {

                        binding.product.visibility = View.INVISIBLE

                        if (canShowMessage) {
                            Toast.makeText(
                                activity, "Ese producto no se encuentra en nuestra " +
                                        "base de datos", Toast.LENGTH_SHORT
                            ).show()
                            canShowMessage = false

                            // Habilitar tras 1s poder mostrar el mensaje
                            handler.postDelayed({
                                this.canShowMessage = true
                            }, 1000)
                        }


                    }
                }
            }
        }
    }




    companion object {
        @JvmStatic
        fun newInstance() = ScannerFragment()
    }
}
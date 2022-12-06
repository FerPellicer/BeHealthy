package com.example.behealthy.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.behealthy.model.repository.product.ProductRepositoryImpl
import com.example.fragments.data.Resource
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(private val repository: ProductRepositoryImpl) : ViewModel() {

    private val _productData = MutableLiveData<Resource<MutableMap<String, Any>?>?>(null)
    val product_Data: LiveData<Resource<MutableMap<String, Any>?>?> = _productData

    private val _productDataSnapshot = MutableLiveData<Resource.Success<Task<DocumentSnapshot>>>(null)
    val productDataSnapshot: LiveData<Resource.Success<Task<DocumentSnapshot>>> = _productDataSnapshot


    fun productData(id: String) = viewModelScope.launch {
        val result = repository.productData(id)
        _productData.value = result
    }


    fun productDataSnapshot(id: String) = viewModelScope.launch {
        val result = repository.productDataSnapshot(id)
        _productDataSnapshot.value = result
    }


}

package com.example.behealthy.viewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.behealthy.model.repository.user.UserRepositoryImpl
import com.example.fragments.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepositoryImpl
) : ViewModel() {


    private val _userDataFlow = MutableLiveData<Resource<MutableMap<String, Any>?>?>(null)
    val userDataFlow: LiveData<Resource<MutableMap<String, Any>?>?> = _userDataFlow

    private val _userUpdatedDataFlow = MutableLiveData<Resource<String>?>(null)
    val userUpdatedDataFlow: LiveData<Resource<String>?> = _userUpdatedDataFlow

    private val _imageUrlFlow = MutableLiveData<Resource<String>?>(null)
    val imageUrlFlow: LiveData<Resource<String>?> = _imageUrlFlow

    private val _userNameAvailable = MutableLiveData<Boolean>(null)
    val userNameAvailable: LiveData<Boolean> = _userNameAvailable


    fun userData() = viewModelScope.launch {
        val result = repository.userData()
        _userDataFlow.value = result
    }

    fun updateUserData(updatedData: HashMap<String, Any>) = viewModelScope.launch {
        val result = repository.updateUserData(updatedData)
        _userUpdatedDataFlow.value = result
    }

    fun changeImage(fileUri: Uri, fileName: String, uid: String) {
        val result = repository.changeUserImage(fileUri, fileName, uid)
        _imageUrlFlow.value = result
    }

    fun checkUserName(userName: String) = viewModelScope.launch {
        val result = repository.checkUserName(userName)
        _userNameAvailable.value = result
    }



}
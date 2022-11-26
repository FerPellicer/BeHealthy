package com.example.fragments.data

sealed class Resource<out R> {
    data class Success<out R>(val result: R) : Resource<R>()
    data class Failure(val exception: String?) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}
package com.example.behealthy.model.data

data class Product (var name : String ?= null,
                    var image : String ?= null,
                    var supermarkets: Map<String, String>? = null,
                    var nutritionalInformation: Map<String, String>?= null,
                    var ingredients : String ?= null,
                    var productBrand: String ?= null,
                    var productRating: String ?= null) : java.io.Serializable {}
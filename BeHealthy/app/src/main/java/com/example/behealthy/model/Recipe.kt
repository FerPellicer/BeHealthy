package com.example.behealthy.model

data class Recipe (var description : String ?= null,
                   var image : String ?= null,
                   var userImage : String ?= null,
                   var ingredients : String ?= null,
                   var likes : String ?= null,
                   var name : String ?= null,
                   var steps : String ?= null,
                   var userName : String ?= null,
                   var user : String ?= null) : java.io.Serializable {

}


package com.example.behealthy.model.data

data class Recipe (var description : String ?= null,
                   var image : String ?= null,
                   var ingredients : String ?= null,
                   var likes : String ?= null,
                   var name : String ?= null,
                   var steps : String ?= null,
                   var user : String ?= null,
                   var idRecipe : String ?= null) : java.io.Serializable {

}


package com.example.behealthy.model.data

data class LocalUser(var email : String ?= null,
                     var likesUser : ArrayList<String>  ?= null,
                     var name : String ?= null,
                     var imageProfile : String ?= null,
                     var saveRecipes : List<String> ?= null,
                     var surname : String ?= null,
                     var userName : String ?= null, )

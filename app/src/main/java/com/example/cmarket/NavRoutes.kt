package com.example.cmarket

sealed class NavRoutes(val route: String) {  //Kun dem i samme fil skal kunne extendes fra denne //Step 4

    object List : NavRoutes("list")  //Singleton rep af liste siden.  reutenavn "list"

    object Detail : NavRoutes("detail/{id}") { //Singleton for detalje siden, "id" er en parameter i ruten

        fun createRoute(id: Int) = "detail/$id"
    }

    object Login : NavRoutes("login")
    object Register : NavRoutes("register")
    object CreateItem : NavRoutes ("createItem")
}

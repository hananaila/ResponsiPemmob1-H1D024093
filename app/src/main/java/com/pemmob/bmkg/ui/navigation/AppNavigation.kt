package com.pemmob.bmkg.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.pemmob.bmkg.data.model.Gempa
import com.pemmob.bmkg.ui.screen.DetailScreen
import com.pemmob.bmkg.ui.screen.HomeScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home_screen") {
        composable("home_screen") {
            HomeScreen(
                onNavigateToDetail = { gempa ->
                    val gempaJson = Gson().toJson(gempa)
                    val encodedJson = URLEncoder.encode(gempaJson, StandardCharsets.UTF_8.toString())
                    navController.navigate("detail_screen/$encodedJson")
                }
            )
        }
        composable("detail_screen/{gempaJson}") { backStackEntry ->
            val encodedJson = backStackEntry.arguments?.getString("gempaJson") ?: ""
            val gempaJson = URLDecoder.decode(encodedJson, StandardCharsets.UTF_8.toString())
            val gempa = Gson().fromJson(gempaJson, Gempa::class.java)

            DetailScreen(
                gempa = gempa,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

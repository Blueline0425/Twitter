package com.gachon.twitter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController: NavHostController = rememberNavController()

            NavHost(navController = navController, startDestination = "login") {
                composable("login") { LoginScreen(navController) }
                composable("home") { MainMenuScreen(navController) }
                composable("profile") { ProfileScreen(navController) }
                composable("search") { SearchScreen(navController) }
                composable("post") { WritePostScreen(navController) }
                composable("following") { FollowingScreen(navController) }
                composable("follower") { FollowerScreen(navController) }
            }
        }
    }
}

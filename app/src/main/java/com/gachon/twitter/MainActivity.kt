package com.gachon.twitter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController: NavHostController = rememberNavController()
            val userViewModel: UserViewModel = viewModel()

            NavHost(navController = navController, startDestination = "login") {
                composable("login") { LoginScreen(navController, userViewModel) }
                composable("home") { MainMenuScreen(navController, userViewModel) }
                composable("profile/{userId}") { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId") ?: ""
                    ProfileScreen(navController, userId, userViewModel)
                }
                composable("search") { SearchScreen(navController, userViewModel) }
                composable("post") { WritePostScreen(navController) }
                composable("following/{userId}") { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId") ?: ""
                    FollowingScreen(navController, userId)
                }
                composable("follower/{userId}") { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId") ?: ""
                    FollowerScreen(navController, userId)
                }
                composable("changepw") { ChangePasswordScreen(navController) }
                composable("createuser") { CreateUserUI(navController) }
                composable("seepost") { SeePost(navController) }
                composable("seecomment") { SeeComment(navController) }
                composable("dm") { DMScreen(navController, userViewModel) }
                composable("dmmsg/{userId}/{nickname}") { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId") ?: ""
                    val nickname = backStackEntry.arguments?.getString("nickname") ?: ""
                    DMmsgScreen(navController, userId, nickname, userViewModel)
                }
            }
        }
    }
}

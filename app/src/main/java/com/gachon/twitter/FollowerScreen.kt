package com.gachon.twitter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.Image
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.foundation.Image

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowerScreen(navController: NavHostController, userId: String) {
    val followerList = remember { mutableStateListOf<UserInfo>() }

    LaunchedEffect(userId) {
        val list = getFollowerList(userId)
        followerList.clear()
        followerList.addAll(list)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Follower") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(followerList) { user ->
                FollowerItem(
                    nickname = user.nickname,
                    id = "@${user.userId}",
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun FollowerItem(nickname: String, id: String, navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { navController.navigate("profile/${id.replace("@", "")}") }
    ) {
        Image(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(40.dp)
                .clickable { navController.navigate("profile/${id.replace("@", "")}") },
            colorFilter = ColorFilter.tint(Color.Gray)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = nickname, style = MaterialTheme.typography.titleSmall)
            Text(text = id, style = MaterialTheme.typography.bodyMedium)
        }
    }
}


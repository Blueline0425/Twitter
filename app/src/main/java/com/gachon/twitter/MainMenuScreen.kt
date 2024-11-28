package com.gachon.twitter

import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import android.app.Activity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.filled.AccountCircle

@Composable
fun MainMenuScreen(navController: NavHostController) {
    val posts = remember { mutableStateListOf<Post>() }

    // 게시글 가져오기
    fetchPosts(context = LocalContext.current as Activity, posts)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Main Menu") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("post") },
                backgroundColor = Color(0xFF1DA1F2)
            ) {
                Image(imageVector = Icons.Default.Add,
                    contentDescription = "Add Post",
                    modifier = Modifier.size(48.dp),
                    colorFilter = ColorFilter.tint(Color.White))
            }
        },
        bottomBar = {
            BottomNavigation {
                BottomNavigationItem(
                    selected = true,
                    onClick = { navController.navigate("home") },
                    label = { Text("Home") },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) }
                )
                BottomNavigationItem(
                    selected = false,
                    onClick = { navController.navigate("search") },
                    label = { Text("Search") },
                    icon = { Icon(Icons.Default.Search, contentDescription = null) }
                )
                BottomNavigationItem(
                    selected = false,
                    onClick = { navController.navigate("dm") },
                    label = { Text("DM") },
                    icon = { Icon(Icons.Default.Email, contentDescription = null) }
                )
                BottomNavigationItem(
                    selected = false,
                    onClick = { navController.navigate("profile") },
                    label = { Text("Profile") },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) }
                )
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text("게시글 리스트 표시")
            LazyColumn {
                items(posts) { post ->
                    PostItem(post)
                }
            }
        }
    }
}

@Composable
fun PostItem(post: Post) {
    Column(modifier = Modifier.padding(8.dp)) {
        // 사용자 프로필 이미지 (예시로 원형 아이콘 사용)
        Image(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profile Picture",
            modifier = Modifier.size(40.dp),
            colorFilter = ColorFilter.tint(Color.Gray)
        )
        Spacer(modifier = Modifier.height(4.dp))
        
        // 사용자 ID 및 닉네임 표시
        Text(text = "${"nickname"} @${post.userId}", style = MaterialTheme.typography.h6)
        
        // 게시글 내용 표시
        Text(text = post.content, style = MaterialTheme.typography.body1)
        
        // 댓글 수 및 좋아요 수 표시
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "댓글 수: ${post.commentCount}", style = MaterialTheme.typography.body2)
            Text(text = "Likes: ${post.numOfLikes}", style = MaterialTheme.typography.body2)
        }
        
        Spacer(modifier = Modifier.height(8.dp))
    }
}

package com.gachon.twitter

import android.annotation.SuppressLint
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Divider
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Locale

@SuppressLint("MutableCollectionMutableState")
@Composable
fun MainMenuScreen(navController: NavHostController, userViewModel: UserViewModel) {
    val context = LocalContext.current
    val posts = remember { mutableStateListOf<Post>() }
    val loggedInUserId = userViewModel.loggedInUserId.collectAsState().value
    LaunchedEffect(Unit) {
        val fetchedPosts = fetchPosts(loggedInUserId.toString())
        posts.addAll(fetchedPosts)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Main Menu") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("post") },
                backgroundColor = Color(0xFF1DA1F2)
            ) {
                Image(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Post",
                    modifier = Modifier.size(48.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
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
                    onClick = { navController.navigate("profile/$loggedInUserId") },
                    label = { Text("Profile") },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) }
                )
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            LazyColumn {
                items(posts) { post ->
                    val totalLikes = remember { mutableStateOf(0) }
                    val totalComments = remember { mutableStateOf(0) }
                    
                    // 각 게시글마다 총 좋아요 수와 댓글 수를 가져옴
                    LaunchedEffect(post.postId) {
                        totalLikes.value = getTotalLikes(post.postId)
                        totalComments.value = getTotalComments(post.postId)
                    }
                    
                    Column(modifier = Modifier
                        .padding(8.dp)
                        .clickable { navController.navigate("seepost/${post.postId}") }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Image(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable { navController.navigate("profile/${post.userId}") },
                                colorFilter = ColorFilter.tint(Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${post.nickname} @${post.userId}",
                                fontSize = 20.sp
                            )
                        }
                        Text(post.content ?: "내용 없음")
                        Spacer(modifier = Modifier.height(4.dp))
                        Row {
                            Text("총 댓글 수: ${totalComments.value}", color = Color.Blue)
                            Text(" | 총 좋아요 수: ${totalLikes.value}", color = Color.Blue)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("태그: ${post.tag ?: "없음"}", color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("업로드 시간: ${post.uploadTimestamp?.let { formatDate(it) } ?: "알 수 없음"}")
                    }
                    Divider(color = Color.Black, thickness = 1.dp)
                }
            }
        }
    }
}

fun formatDate(timestamp: java.sql.Timestamp): String {
    val sdf = SimpleDateFormat("yyyy/MM/dd, HH:mm:ss", Locale.getDefault())
    return sdf.format(timestamp)
}
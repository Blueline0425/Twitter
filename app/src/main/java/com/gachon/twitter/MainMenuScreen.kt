package com.gachon.twitter

import android.annotation.SuppressLint
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.graphicsLayer
import androidx.navigation.NavHostController
import com.gachon.twitter.ui.theme.TwitterTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@SuppressLint("MutableCollectionMutableState")
@Composable
fun MainMenuScreen(navController: NavHostController, userViewModel: UserViewModel) {
    TwitterTheme {
        val context = LocalContext.current
        val posts = remember { mutableStateListOf<Post>() }
        val loggedInUserId = userViewModel.loggedInUserId.collectAsState().value

        LaunchedEffect(Unit) {
            val fetchedPosts = fetchPosts(loggedInUserId.toString())
            posts.addAll(fetchedPosts)
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // 앱 아이콘 추가
                            Icon(
                                painter = painterResource(id = R.drawable.app_icon), // 여기에 아이콘 리소스를 넣으세요.
                                contentDescription = "App Icon",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(end = 8.dp)
                            )
                            //Text("Main Menu")
                        }
                    },
                    backgroundColor = Color(0xFF1DA1F2),
                    contentColor = Color.White
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("post") },
                    backgroundColor = Color(0xFF1DA1F2) // Material 2 용 FloatingActionButton
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Post",
                        tint = Color.White
                    )
                }
            },
            bottomBar = {
                BottomNavigation(
                    backgroundColor = Color(0xFF1DA1F2),
                    contentColor = Color.White
                ) {
                    BottomNavigationItem(
                        selected = true,
                        onClick = { navController.navigate("home") },
                        label = { Text("Home", color = Color.White) },
                        icon = { Icon(Icons.Default.Home, contentDescription = null, tint = Color.White) }
                    )
                    BottomNavigationItem(
                        selected = false,
                        onClick = { navController.navigate("search") },
                        label = { Text("Search", color = Color.White) },
                        icon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) }
                    )
                    BottomNavigationItem(
                        selected = false,
                        onClick = { navController.navigate("dm") },
                        label = { Text("DM", color = Color.White) },
                        icon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.White) }
                    )
                    BottomNavigationItem(
                        selected = false,
                        onClick = { navController.navigate("profile/$loggedInUserId") },
                        label = { Text("Profile", color = Color.White) },
                        icon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White) }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.White)
            ) {
                // 반투명한 중앙 아이콘 추가
                Image(
                    painter = painterResource(id = R.drawable.app_icon), // 여기에 앱 아이콘 리소스 추가
                    contentDescription = "App Icon",
                    modifier = Modifier
                        .size(200.dp) // 아이콘 크기 설정
                        .align(Alignment.Center) // 중앙 정렬
                        .graphicsLayer(alpha = 0.2f) // 반투명하게 설정 (0.2은 투명도 값)
                )

                // 포스트 리스트
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp) // 아이콘 위에 겹치지 않도록 패딩 설정 가능
                ) {
                    items(posts) { post ->
                        val totalLikes = remember { mutableStateOf(0) }
                        val totalComments = remember { mutableStateOf(0) }

                        LaunchedEffect(post.postId) {
                            totalLikes.value = getTotalLikes(post.postId)
                            totalComments.value = getTotalComments(post.postId)
                        }

                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable { navController.navigate("seepost/${post.postId}") }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable { navController.navigate("profile/${post.userId}") },
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${post.nickname} @${post.userId}",
                                    fontSize = 20.sp,
                                    color = Color.Black
                                )
                            }
                            Text(post.content ?: "내용 없음", color = Color.Black)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row {
                                Text("총 댓글 수: ${totalComments.value}", color = Color(0xFF1DA1F2))
                                Text(" | 총 좋아요 수: ${totalLikes.value}", color = Color(0xFF1DA1F2))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("태그: ${post.tag ?: "없음"}", color = Color.Gray)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("업로드 시간: ${post.uploadTimestamp?.let { formatDate(it) } ?: "알 수 없음"}", color = Color.Gray)
                        }
                        Divider(color = Color.Gray, thickness = 1.dp)
                    }
                }
            }
        }
    }
}

fun formatDate(timestamp: java.sql.Timestamp): String {
    val sdf = SimpleDateFormat("yyyy/MM/dd, HH:mm:ss", Locale.getDefault())
    return sdf.format(timestamp)
}

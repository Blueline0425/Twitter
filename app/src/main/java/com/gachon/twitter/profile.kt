package com.gachon.twitter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(navController: NavHostController, userId: String, userViewModel: UserViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val nickname = remember { mutableStateOf("") }
    val isFollowing = remember { mutableStateOf(false) }
    val loggedInUserId = userViewModel.loggedInUserId.collectAsState().value

    LaunchedEffect(userId) {
        nickname.value = getNicknameFromUserId(userId)
        isFollowing.value = checkIfFollowing(loggedInUserId.toString(), userId)
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
                        Text(
                            text = "Profile",
                            color = Color.White, // 텍스트 색상을 명확하게 흰색으로 설정합니다.
                            fontSize = 20.sp
                        )
                    }
                },
                backgroundColor = Color(0xFF1DA1F2),
                contentColor = Color.White
            )
        },
        bottomBar = {
            BottomNavigation(
                backgroundColor = Color(0xFF1DA1F2),
                contentColor = Color.White
            ) {
                BottomNavigationItem(
                    selected = false,
                    onClick = { navController.navigate("home") },
                    label = { Text("Home") },
                    icon = { Icon(Icons.Default.Home, contentDescription = null, tint = Color.White) }
                )
                BottomNavigationItem(
                    selected = false,
                    onClick = { navController.navigate("search") },
                    label = { Text("Search") },
                    icon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) }
                )
                BottomNavigationItem(
                    selected = false,
                    onClick = { navController.navigate("dm") },
                    label = { Text("DM") },
                    icon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.White) }
                )
                BottomNavigationItem(
                    selected = true,
                    onClick = { navController.navigate("profile/${loggedInUserId}") },
                    label = { Text("Profile") },
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
                painter = painterResource(id = R.drawable.app_icon), // 여기에 앱 아이콘 리소스를 추가하세요
                contentDescription = "App Icon",
                modifier = Modifier
                    .size(200.dp) // 아이콘 크기 설정
                    .align(Alignment.Center) // 중앙 정렬
                    .graphicsLayer(alpha = 0.2f) // 반투명하게 설정 (0.2은 투명도 값)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp) // 아이콘 위에 겹치지 않도록 패딩 설정 가능
            ) {
                ProfileSection(
                    nickname.value, userId, navController,
                    isFollowing.value, loggedInUserId.toString(),
                    onFollowClick = {
                        coroutineScope.launch {
                            if (isFollowing.value) {
                                unfollowUser(loggedInUserId.toString(), userId)
                                isFollowing.value = false
                            } else {
                                followUser(loggedInUserId.toString(), userId)
                                isFollowing.value = true
                            }
                        }
                    }
                )
                PostList(userId, navController)
            }
        }
    }
}

@Composable
fun ProfileSection(
    nickname: String,
    userId: String,
    navController: NavHostController,
    isFollowing: Boolean,
    loggedInUserId: String,
    onFollowClick: () -> Unit
) {
    val followingCount = remember { mutableStateOf(0) }
    val followerCount = remember { mutableStateOf(0) }
    val isFollowingState = remember(isFollowing) { mutableStateOf(isFollowing) }

    LaunchedEffect(userId) {
        followingCount.value = getFollowingCount(userId)
        followerCount.value = getFollowerCount(userId)
        isFollowingState.value = isFollowing
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color(0xFF1DA1F2))
                .align(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(64.dp)
                    .padding(8.dp),
                tint = Color.Gray
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = nickname, style = MaterialTheme.typography.h6)
                    Text(text = "@$userId", style = MaterialTheme.typography.body2, color = Color.Gray)
                }

                if (loggedInUserId != userId) {
                    Button(
                        onClick = {
                            isFollowingState.value = !isFollowingState.value
                            if (isFollowingState.value) {
                                followerCount.value += 1
                            } else {
                                followerCount.value -= 1
                            }
                            onFollowClick()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (isFollowingState.value) Color.White else Color(0xFF1DA1F2),
                            contentColor = if (isFollowingState.value) Color.Red else Color.White
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(if (isFollowingState.value) "언팔로잉" else "팔로잉")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                // 팔로잉 텍스트
                Text(
                    text = followingCount.value.toString(),
                    style = MaterialTheme.typography.h6,
                    color = Color.Black,
                    modifier = Modifier.clickable { navController.navigate("following/$userId") }
                )
                Text(
                    text = "팔로잉",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray,
                    modifier = Modifier.clickable { navController.navigate("following/$userId") }
                )
                Spacer(modifier = Modifier.width(16.dp))
                // 팔로워 텍스트
                Text(
                    text = followerCount.value.toString(),
                    style = MaterialTheme.typography.h6,
                    color = Color.Black,
                    modifier = Modifier.clickable { navController.navigate("follower/$userId") }
                )
                Text(
                    text = "팔로워",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray,
                    modifier = Modifier.clickable { navController.navigate("follower/$userId") }
                )
            }
        }
    }
}

@Composable
fun PostList(userId: String, navController: NavHostController) {
    val posts = remember { mutableStateListOf<Post>() }

    LaunchedEffect(userId) {
        val fetchedPosts = fetchPostsForUser(userId)
        posts.addAll(fetchedPosts)
    }

    LazyColumn {
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

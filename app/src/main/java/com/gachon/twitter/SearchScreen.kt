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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.unit.sp

@Composable
fun SearchScreen(navController: NavHostController, userViewModel: UserViewModel) {
    val loggedInUserId = userViewModel.loggedInUserId.collectAsState().value
    val searchText = remember { mutableStateOf("") }
    val searchResults = remember { mutableStateOf<Any>(emptyList<Any>()) }
    val scope = rememberCoroutineScope()

    val twitterBlue = Color(0xFF1DA1F2)
    val white = Color.White
    val gray = Color.Gray
    val lightGray = Color.LightGray
    val black = Color.Black
    val placeholderColor = Color.DarkGray

    // 검색 결과 실시간 업데이트를 위한 LaunchedEffect 추가
    LaunchedEffect(searchText.value) {
        if (searchText.value.isNotEmpty()) {
            if (searchText.value.startsWith("@") && searchText.value.length > 1) {
                // @로 시작하면 사용자 검색
                val query = searchText.value.substring(1)
                searchResults.value = searchUsers(query)
            } else {
                // 그 외에는 게시글 검색
                searchResults.value = searchPosts(searchText.value)
            }
        } else {
            searchResults.value = emptyList<Any>()
        }
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
                            tint = white,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(end = 8.dp)
                        )
                        Text("Search")
                    }
                },
                backgroundColor = twitterBlue,
                contentColor = white
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("post") },
                backgroundColor = twitterBlue
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Post",
                    tint = white
                )
            }
        },
        bottomBar = {
            BottomNavigation(
                backgroundColor = twitterBlue
            ) {
                BottomNavigationItem(
                    selected = true,
                    onClick = { navController.navigate("home") },
                    label = { Text("Home", color = white) },
                    icon = { Icon(Icons.Default.Home, contentDescription = null, tint = white) }
                )
                BottomNavigationItem(
                    selected = false,
                    onClick = { navController.navigate("search") },
                    label = { Text("Search", color = white) },
                    icon = { Icon(Icons.Default.Search, contentDescription = null, tint = white) }
                )
                BottomNavigationItem(
                    selected = false,
                    onClick = { navController.navigate("dm") },
                    label = { Text("DM", color = white) },
                    icon = { Icon(Icons.Default.Email, contentDescription = null, tint = white) }
                )
                BottomNavigationItem(
                    selected = false,
                    onClick = { navController.navigate("profile/$loggedInUserId") },
                    label = { Text("Profile", color = white) },
                    icon = { Icon(Icons.Default.Person, contentDescription = null, tint = white) }
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        value = searchText.value,
                        onValueChange = { searchText.value = it },
                        label = { Text("검색어를 입력하세요") },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = twitterBlue,
                            unfocusedBorderColor = gray,
                            focusedLabelColor = twitterBlue,
                            cursorColor = twitterBlue,
                            placeholderColor = placeholderColor
                        ),
                        leadingIcon = {  // 검색 아이콘을 trailing에서 leading으로 변경
                            Icon(Icons.Default.Search, "검색", tint = twitterBlue)
                        }
                    )
                }

                when (val results = searchResults.value) {
                    is List<*> -> {
                        when {
                            results.firstOrNull() is UserInfo -> {
                                LazyColumn {
                                    items(results as List<UserInfo>) { user ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    navController.navigate("profile/${user.userId}")
                                                }
                                                .padding(16.dp)
                                        ) {
                                            Image(
                                                imageVector = Icons.Default.AccountCircle,
                                                contentDescription = "Profile Picture",
                                                modifier = Modifier.size(40.dp),
                                                colorFilter = ColorFilter.tint(gray)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(text = user.nickname, style = MaterialTheme.typography.subtitle1)
                                                Text(text = "@${user.userId}", style = MaterialTheme.typography.body1)
                                            }
                                        }
                                        Divider(color = lightGray, thickness = 0.5.dp)
                                    }
                                }
                            }
                            results.firstOrNull() is Post -> {
                                LazyColumn {
                                    items(results as List<Post>) { post ->
                                        Column(
                                            modifier = Modifier
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
                                                    colorFilter = ColorFilter.tint(gray)
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
                                                Text("댓글 수: ${post.numOfComments}", color = twitterBlue)
                                                Text(" | 좋아요 수: ${post.numOfLikes}", color = twitterBlue)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("태그: ${post.tag ?: "없음"}", color = gray)
                                            Text("업로드 시간: ${post.uploadTimestamp?.let { formatDate(it) } ?: "알 수 없음"}")
                                        }
                                        Divider(color = black, thickness = 1.dp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

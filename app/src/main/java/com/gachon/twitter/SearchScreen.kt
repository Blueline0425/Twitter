package com.gachon.twitter

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ColorFilter

//@Preview(showBackground = true)
@Composable
fun SearchScreen(navController: NavHostController, userViewModel: UserViewModel) {
    val loggedInUserId = userViewModel.loggedInUserId.collectAsState().value
    val searchText = remember { mutableStateOf("") }
    val searchResults = remember { mutableStateOf<Any>(emptyList<Any>()) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("검색") })
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
            BottomNavigation(
                backgroundColor = Color(0xFF6200EE) // 원하는 색상으로 변경
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
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {
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
                    trailingIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (searchText.value.startsWith("@")) {
                                    // @로 시작하면 사용자 검색
                                    val query = searchText.value.substring(1)
                                    searchResults.value = searchUsers(query)
                                } else {
                                    // 그 외에는 게시글 검색
                                    searchResults.value = searchPosts(searchText.value)
                                }
                            }
                        }) {
                            Icon(Icons.Default.Search, "검색")
                        }
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
                                            colorFilter = ColorFilter.tint(Color.Gray)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(text = user.nickname, style = MaterialTheme.typography.subtitle1)
                                            Text(text = "@${user.userId}", style = MaterialTheme.typography.body1)
                                        }
                                    }
                                    Divider(color = Color.LightGray, thickness = 0.5.dp)
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
                                            Text("댓글 수: ${post.numOfComments}", color = Color.Blue)
                                            Text(" | 좋아요 수: ${post.numOfLikes}", color = Color.Blue)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("태그: ${post.tag ?: "없음"}", color = Color.Gray)
                                        Text("업로드 시간: ${post.uploadTimestamp?.let { formatDate(it) } ?: "알 수 없음"}")
                                    }
                                    Divider(color = Color.Black, thickness = 1.dp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostItem(nickname: String, id: String, text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, Color.Gray)
    ) {
        // 닉네임 및 아이디
        Text(text = "$nickname $id", style = MaterialTheme.typography.subtitle1)
        Text(text = text, style = MaterialTheme.typography.body1)

        // 댓글 및 좋아요 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { /* 댓글 로직 */ }) {
                Text("댓글 수", color = Color.Blue)
            }
            TextButton(onClick = { /* 좋아요 로직 */ }) {
                Text("좋아요 수", color = Color.Blue)
            }
        }
    }
}


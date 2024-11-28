package com.gachon.twitter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.navigation.NavHostController

//@Preview(showBackground = true)
@Composable
fun ProfileScreen(navController: NavHostController) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("post") },
                backgroundColor = Color(0xFF1DA1F2))

            {
                Image(imageVector = Icons.Default.Add,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(8.dp),
                    colorFilter = ColorFilter.tint(Color.White))

            }
        },
        bottomBar = {
            BottomNavigation {
                BottomNavigationItem(
                    selected = false,
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
                    selected = true,
                    onClick = { navController.navigate("profile") },
                    label = { Text("Profile") },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 프로필 섹션
            ProfileSection(navController)

            // 게시글 리스트
            PostList()
        }
    }
}

@Composable
fun ProfileSection(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 배경 색상
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
            // 프로필 사진
            Image(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(64.dp)
                    .padding(8.dp),
                colorFilter = ColorFilter.tint(Color.Gray)
            )

            // 닉네임 및 아이디
            Text(text = "한웅재", style = MaterialTheme.typography.h6)
            Text(text = "@woongjae2435", style = MaterialTheme.typography.body2, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))

            // Following 및 Follower 정보
            Row {
                // 팔로잉 텍스트
                Text(
                    text = "1",
                    style = MaterialTheme.typography.h6,
                    color = Color.Black,
                    modifier = Modifier
                        //.clickable { navController.navigate("following") } // 클릭 시 이동
                        .padding(0.dp)
                )
                Text(
                    text = "팔로잉",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray,
                    modifier = Modifier
                        .clickable { navController.navigate("following") } // 클릭 시 이동
                        .padding(0.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                // 팔로워 텍스트
                Text(
                    text = "0",
                    style = MaterialTheme.typography.h6,
                    color = Color.Black,
                    modifier = Modifier
                        //.clickable { navController.navigate("follower") } // 클릭 시 이동
                        .padding(0.dp)
                )
                Text(
                    text = "팔로워",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray,
                    modifier = Modifier
                        .clickable { navController.navigate("follower") } // 클릭 시 이동
                        .padding(0.dp)
                )
            }
        }
    }
}

@Composable
fun PostList() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 예시 게시글
        repeat(3) { index ->
            PostItem(nickname = "Nickname $index", text = "게시글 내용 $index")
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PostItem(nickname: String, text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // 닉네임
        Text(text = "$nickname @username", style = MaterialTheme.typography.subtitle1)

        Spacer(modifier = Modifier.height(4.dp))

        // 게시글 내용
        Text(text = text, style = MaterialTheme.typography.body1)

        Spacer(modifier = Modifier.height(8.dp))

        // 좋아요 및 댓글 버튼
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = { /* 댓글 로직 */ }) {
                Text("댓글 수")
            }
            TextButton(onClick = { /* 좋아요 로직 */ }) {
                Text("좋아요 수")
            }
        }
    }
}

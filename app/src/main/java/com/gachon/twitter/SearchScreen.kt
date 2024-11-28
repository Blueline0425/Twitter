package com.gachon.twitter

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

//@Preview(showBackground = true)
@Composable
fun SearchScreen(navController: NavHostController) {

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("검색") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("post") },
                backgroundColor = Color(0xFF1DA1F2)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Write a post",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(8.dp),
                    tint = Color.White
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
                    onClick = { navController.navigate("profile") },
                    label = { Text("Profile", color = Color.White) },
                    icon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White) }
                )
            }
        }
    ) { innerPadding ->
        // 게시글 리스트 UI 구현
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {
            // 검색 입력창
            OutlinedTextField(
                value = "",
                onValueChange = { /* 검색어 입력 처리 */ },
                label = { Text("정확히 입력하세요") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // 게시물 리스트
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(7) { index ->
                    PostItem(
                        nickname = "nickname$index",
                        id = "@id$index",
                        text = "text field $index"
                    )
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


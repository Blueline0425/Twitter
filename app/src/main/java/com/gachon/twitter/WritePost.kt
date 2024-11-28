package com.gachon.twitter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.navigation.NavHostController

//@Preview(showBackground = true)
@Composable
fun WritePostScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Write a Post") },
                navigationIcon = {
                    IconButton(onClick = { /* 뒤로가기 로직 */ }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    TextButton(onClick = { /* 게시하기 로직 */ }) {
                        Text("게시하기", color = Color.White)
                    }
                },
                backgroundColor = MaterialTheme.colors.primary
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 사용자 아이콘 및 텍스트 입력 필드
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 프로필 아이콘
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground), // 샘플 이미지
                    contentDescription = "Profile Icon",
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Gray, CircleShape)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // 텍스트 필드
                TextField(
                    value = TextFieldValue("무슨 일이 일어나고 있나요?"),
                    onValueChange = { /* 게시글 입력 로직 */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 미디어 추가 버튼
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 8.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(
                    onClick = { /* 미디어 추가 로직 */ },
                    backgroundColor = Color(0xFF1DA1F2)
                )             {
                    Image(imageVector = Icons.Default.Add,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(48.dp)
                            .padding(8.dp),
                        colorFilter = ColorFilter.tint(Color.White))

                }
            }
        }
    }
}

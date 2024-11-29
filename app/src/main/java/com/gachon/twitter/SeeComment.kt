package com.gachon.twitter

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.unit.sp

@Composable
    fun SeeComment(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("게시하기") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 상단 구역: 게시글 내용
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(40.dp),
                        colorFilter = ColorFilter.tint(Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "nickname", style = MaterialTheme.typography.h6)
                        Text(text = "@id", style = MaterialTheme.typography.body2)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "게시글 내용", style = MaterialTheme.typography.body1)

                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    Text("댓글 수: 0", color = Color.Blue)
                    Text(" | 좋아요 수: 0", color = Color.Blue)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("태그: 없음", color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text("업로드 시간: 알 수 없음")
            }

            Divider(color = Color.Black, thickness = 1.dp)

            // 하단 구역: 댓글 구역
            Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                // 댓글 내용
                Text("댓글 내용", style = MaterialTheme.typography.body1)
            }

            Divider(color = Color.Black, thickness = 1.dp)

            // 최하단 구역: 댓글 작성
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("댓글 작성하기") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { /* 댓글 전송 로직 */ }) {
                    Text("보내기")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSeeComment() {
    val navController = rememberNavController()
    SeePost(navController)
}


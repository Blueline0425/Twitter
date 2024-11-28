package com.gachon.twitter

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.navigation.compose.rememberNavController

@Composable
fun SeeComment(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("댓글 보기") },
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
                .padding(16.dp)
        ) {
            // 댓글 리스트
            LazyColumn {
                items(5) { index -> // 5개의 댓글을 표시
                    CommentItem(nickname = "nickname$index", comment = "댓글 내용 $index")
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // 댓글 입력 필드
            TextField(
                value = "",
                onValueChange = { /* 댓글 입력 처리 */ },
                label = { Text("댓글을 입력하세요") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { /* 댓글 게시 처리 */ }) {
                Text("댓글 게시하기")
            }
        }
    }
}

@Composable
fun CommentItem(nickname: String, comment: String) {
    Column {
        Text(text = nickname, style = MaterialTheme.typography.body1)
        Text(text = comment, style = MaterialTheme.typography.body2)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSeeComment() {
   //Preview를 위한 NavHostController 필요
  val navController = rememberNavController()
  SeeComment(navController)
}


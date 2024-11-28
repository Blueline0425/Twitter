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

@Composable
fun SeePost(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post") },
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
            // 게시글 작성자 정보
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

            // 게시글 내용
            Text(text = "이미지/동영상", style = MaterialTheme.typography.body1)

            Spacer(modifier = Modifier.height(16.dp))

            // 댓글 보기 버튼
            Button(onClick = { navController.navigate("seeComment") }) {
                Text("댓글 보기")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSeePost() {
   //  Preview를 위한 NavHostController 필요
    val navController = rememberNavController()
    SeePost(navController)
}


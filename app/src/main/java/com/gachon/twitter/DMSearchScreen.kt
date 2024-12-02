package com.gachon.twitter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.gachon.twitter.ui.theme.TwitterTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DMSearchScreen(navController: NavHostController) {
    var searchText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(emptyList<UserInfo>()) }
    val coroutineScope = rememberCoroutineScope()

    // 검색 결과 업데이트하는 효과
    LaunchedEffect(searchText) {
        if (searchText.startsWith("@") && searchText.length > 1) {
            coroutineScope.launch {
                val query = searchText.substring(1)
                searchResults = searchUsers(query)
            }
        } else {
            searchResults = emptyList()
        }
    }

    TwitterTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("새 메시지", style = MaterialTheme.typography.h6) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    backgroundColor = Color(0xFF1DA1F2), // Twitter Blue
                    contentColor = Color.White // Text color in the top app bar
                )
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

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    // 검색창
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text("@사용자 검색") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF1DA1F2), // Twitter Blue
                            unfocusedBorderColor = Color.LightGray,
                            cursorColor = Color(0xFF1DA1F2), // Twitter Blue
                            textColor = Color.Black,
                            focusedLabelColor = Color(0xFF1DA1F2), // Twitter Blue (텍스트가 선택되었을 때 색상 변경)
                            unfocusedLabelColor = Color.Gray // 비활성화된 상태에서 라벨 색상
                        )
                    )

                    // 검색 결과
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        items(searchResults) { user ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate("dmmsg/${user.userId}/${user.nickname}")
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.size(40.dp),
                                    tint = Color.Gray // Profile icon tint color
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = user.nickname,
                                        style = MaterialTheme.typography.subtitle1,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = "@${user.userId}",
                                        style = MaterialTheme.typography.body2,
                                        color = Color.Gray.copy(alpha = 0.7f)
                                    )
                                }
                            }
                            Divider(color = Color.Gray.copy(alpha = 0.1f))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DMSearchScreenPreview() {
    DMSearchScreen(navController = rememberNavController())
}

package com.gachon.twitter

import android.annotation.SuppressLint
import androidx.compose.foundation.Image // 추가된 부분
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.gachon.twitter.ui.theme.TwitterTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Timestamp

data class ChatListItem(
    val userId: String,
    val nickname: String,
    val content: String,
    val timestamp: Timestamp
)

@SuppressLint("MutableCollectionMutableState")
@Composable
fun DMScreen(navController: NavHostController, userViewModel: UserViewModel) {
    val messages = remember { mutableStateListOf<ChatListItem>() }
    val loggedInUserId by userViewModel.loggedInUserId.collectAsState()

    LaunchedEffect(loggedInUserId) {
        loggedInUserId?.let { userId ->
            withContext(Dispatchers.IO) {
                val fetchedMessages = fetchChatList(userId)
                messages.clear()
                messages.addAll(fetchedMessages.map { message ->
                    ChatListItem(
                        userId = if (message.senderId == userId) message.receiverId else message.senderId,
                        nickname = getNicknameFromUserId(if (message.senderId == userId) message.receiverId else message.senderId),
                        content = message.content,
                        timestamp = message.timestamp
                    )
                })
            }
        }
    }

    // Twitter 테마 적용
    val twitterBlue = Color(0xFF1DA1F2)
    val twitterLightGray = Color(0xFFE1E8ED)
    val white = Color.White
    val gray = Color.Gray
    val black = Color.Black

    TwitterTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.app_icon), // 여기에 아이콘 리소스를 넣으세요.
                                contentDescription = "App Icon",
                                tint = white,
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(end = 8.dp)
                            )
                            Text("DM")
                        }
                    },
                    backgroundColor = twitterBlue,
                    contentColor = white
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("dmsearch") },
                    backgroundColor = twitterBlue,
                    contentColor = white
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Message",
                        tint = white
                    )
                }
            },
            bottomBar = {
                BottomNavigation(
                    backgroundColor = twitterBlue,
                    contentColor = white
                ) {
                    BottomNavigationItem(
                        selected = false,
                        onClick = { navController.navigate("home") },
                        label = { Text("Home") },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        selectedContentColor = white,
                        unselectedContentColor = twitterLightGray
                    )
                    BottomNavigationItem(
                        selected = false,
                        onClick = { navController.navigate("search") },
                        label = { Text("Search") },
                        icon = { Icon(Icons.Default.Search, contentDescription = null) },
                        selectedContentColor = white,
                        unselectedContentColor = twitterLightGray
                    )
                    BottomNavigationItem(
                        selected = true,
                        onClick = { navController.navigate("dm") },
                        label = { Text("DM") },
                        icon = { Icon(Icons.Default.Email, contentDescription = null) },
                        selectedContentColor = white,
                        unselectedContentColor = twitterLightGray
                    )
                    BottomNavigationItem(
                        selected = false,
                        onClick = {
                            navController.navigate("profile/$loggedInUserId")
                        },
                        label = { Text("Profile") },
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        selectedContentColor = white,
                        unselectedContentColor = twitterLightGray
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(white)
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

                LazyColumn( //같은 시간에 보내면 한사람당 여러개가 나오는 동시성 문제 발견, 추후에 수정예정
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp)
                ) {
                    items(messages) { chatItem ->
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    navController.navigate("dmmsg/${chatItem.userId}/${chatItem.nickname}")
                                }
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
                                        .clickable { navController.navigate("profile/${chatItem.userId}") },
                                    tint = gray
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${chatItem.nickname} @${chatItem.userId}",
                                    fontSize = 20.sp,
                                    color = black
                                )
                            }
                            Text(chatItem.content, color = black)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "보낸 시간: ${formatDate(chatItem.timestamp)}",
                                color = gray,
                                fontSize = 12.sp
                            )
                        }
                        Divider(color = gray, thickness = 1.dp)
                    }
                }
            }
        }
    }
}

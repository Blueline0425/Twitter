package com.gachon.twitter

import android.annotation.SuppressLint
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Divider
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

// DirectMessage를 Message로 변경하고, 필요한 추가 데이터 클래스 정의
data class ChatListItem(
    val userId: String,
    val nickname: String,
    val content: String,
    val timestamp: Timestamp
)

@SuppressLint("MutableCollectionMutableState")
@Composable
fun DMScreen(navController: NavHostController, userViewModel: UserViewModel) {
    val context = LocalContext.current
    val messages = remember { mutableStateListOf<ChatListItem>() }
    val loggedInUserId = userViewModel.loggedInUserId.collectAsState().value

    LaunchedEffect(loggedInUserId) {
        loggedInUserId?.let {
            val fetchedMessages = fetchChatList(it)
            messages.clear()
            messages.addAll(fetchedMessages.map { message ->
                ChatListItem(
                    userId = if (message.senderId == loggedInUserId) message.receiverId else message.senderId,
                    nickname = getNicknameFromUserId(if (message.senderId == loggedInUserId) message.receiverId else message.senderId),
                    content = message.content,
                    timestamp = message.timestamp
                )
            })
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("DM") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("dmsearch") },
                backgroundColor = Color(0xFF1DA1F2)
            ) {
                Image(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Message",
                    modifier = Modifier.size(48.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        },
        bottomBar = {
            BottomNavigation {
                BottomNavigationItem(
                    selected = true,
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
                    selected = false,
                    onClick = {
                        navController.navigate("profile/$loggedInUserId")
                    },
                    label = { Text("Profile") },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) }
                )
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            LazyColumn {
                items(messages) { chatItem ->
                    Column(modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            navController.navigate("dmmsg/${chatItem.userId}/${chatItem.nickname}")
                        }
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
                                    .clickable { navController.navigate("profile/${chatItem.userId}") },
                                colorFilter = ColorFilter.tint(Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${chatItem.nickname} @${chatItem.userId}",
                                fontSize = 20.sp
                            )
                        }
                        Text(chatItem.content)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("보낸 시간: ${formatDate(chatItem.timestamp)}", color = Color.Gray)
                    }
                    Divider(color = Color.Black, thickness = 1.dp)
                }
            }
        }
    }
}
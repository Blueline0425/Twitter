package com.gachon.twitter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

@Composable
fun DMmsgScreen(navController: NavHostController, userId: String, nickname: String, userViewModel: UserViewModel) {
    var messageText by remember { mutableStateOf("") }  // 문자열로 변경
    val messages = remember { mutableStateListOf<Message>() }
    val coroutineScope = rememberCoroutineScope()
    val loggedInUserId = userViewModel.loggedInUserId.collectAsState().value

    // 초기 메시지 로드와 읽음 처리를 위한 LaunchedEffect
    LaunchedEffect(userId, loggedInUserId) {
        // 메시지 로드
        val initialMessages = fetchMessagesWithUser(userId, loggedInUserId.toString())
        messages.clear()
        messages.addAll(initialMessages)
        
        // 읽음 처리
        markMessagesAsRead(userId, loggedInUserId.toString())
    }

    val listState = rememberLazyListState()
    
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // 색상 정의
    val twitterBlue = Color(0xFF1DA1F2)
    val white = Color(0xFFFFFFFF)
    val gray = Color(0xFF808080)
    val black = Color(0xFF000000)
    val lightGray = Color(0xFFE1E8ED)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(nickname, color = white) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = white)
                    }
                },
                backgroundColor = twitterBlue
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .background(
                            color = lightGray,
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(8.dp),
                    textStyle = LocalTextStyle.current.copy(
                        color = black
                    )
                )
                IconButton(onClick = {
                    if (messageText.isNotBlank()) {
                        coroutineScope.launch {
                            sendMessage(loggedInUserId.toString(), userId, messageText)
                            messageText = "" // 전송 후 입력 필드 초기화
                            // 메시지 리스트 업데이트
                            val updatedMessages = fetchMessagesWithUser(userId, loggedInUserId.toString())
                            messages.clear()
                            messages.addAll(updatedMessages)
                        }
                    }
                }) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send",
                        tint = twitterBlue
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(white),
            state = listState
        ) {
            items(messages) { message ->
                if (message.senderId == loggedInUserId) { // 본인이 보낸 메시지
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(
                                text = message.content,
                                modifier = Modifier
                                    .background(
                                        twitterBlue,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .padding(8.dp),
                                color = white
                            )
                            Text(
                                text = "보낸 시간: ${formatDate(message.timestamp)} · ${if (message.isRead == 1) "읽음" else "읽지 않음"}",
                                color = gray,
                                fontSize = 12.sp
                            )
                        }
                    }
                } else { // 상대방이 보낸 메시지
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(
                                text = message.content,
                                modifier = Modifier
                                    .background(
                                        lightGray,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .padding(8.dp),
                                color = black
                            )
                            Text(
                                text = "받은 시간: ${formatDate(message.timestamp)}",
                                color = gray,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DMmsgScreenPreview() {
    DMmsgScreen(navController = rememberNavController(), userId = "user123", nickname = "닉네임", userViewModel = UserViewModel())
}

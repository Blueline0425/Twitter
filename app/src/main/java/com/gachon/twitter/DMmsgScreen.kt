package com.gachon.twitter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

@Composable
fun DMmsgScreen(navController: NavHostController, userId: String, nickname: String, userViewModel: UserViewModel) {
    var messageText by remember { mutableStateOf(TextFieldValue("")) }
    val messages = remember { mutableStateListOf<Message>() }
    val coroutineScope = rememberCoroutineScope()
    val loggedInUserId = userViewModel.loggedInUserId.collectAsState().value

    LaunchedEffect(userId) {
        // 초기 메시지 가져오기
        val fetchedMessages = fetchMessagesWithUser(userId, loggedInUserId.toString())
        messages.clear()
        messages.addAll(fetchedMessages)
        // 읽음 처리
        markMessagesAsRead(userId, loggedInUserId.toString())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(nickname) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
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
                        .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                        .padding(8.dp)
                )
                IconButton(onClick = {
                    if (messageText.text.isNotBlank()) {
                        coroutineScope.launch {
                            sendMessage(loggedInUserId.toString(), userId, messageText.text)
                            messageText = TextFieldValue("") // 전송 후 입력 필드 초기화
                            // 메시지 리스트 업데이트
                            val updatedMessages = fetchMessagesWithUser(userId, loggedInUserId.toString())
                            messages.clear()
                            messages.addAll(updatedMessages)
                        }
                    }
                }) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            items(messages) { message ->
                if (message.senderId == loggedInUserId) { // 본인이 보낸 메시지
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Column {
                            Text(
                                text = message.content,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .background(Color.Blue, shape = MaterialTheme.shapes.medium)
                                    .padding(8.dp),
                                color = Color.White
                            )
                            Text(
                                text = "보낸 시간: ${formatDate(message.timestamp)} · ${if (message.isRead == 1) "읽음" else "읽지 않음"}",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }
                } else { // 상대방이 보낸 메시지
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Column {
                            Text(
                                text = message.content,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .background(Color.Gray, shape = MaterialTheme.shapes.medium)
                                    .padding(8.dp),
                                color = Color.Black
                            )
                            Text(
                                text = "받은 시간: ${formatDate(message.timestamp)}",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

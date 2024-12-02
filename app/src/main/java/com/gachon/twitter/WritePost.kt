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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.navigation.NavHostController
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import kotlinx.coroutines.launch
//@Preview(showBackground = true)
@Composable
fun WritePostScreen(navController: NavHostController, userViewModel: UserViewModel) {
    val context = LocalContext.current
    val content = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val loggedInUserId = userViewModel.loggedInUserId.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("새 게시글 작성", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                val taggedUsers = extractTaggedUsers(content.value)
                                when {
                                    taggedUsers.size > 1 -> {
                                        Toast.makeText(context, "태그는 한명만 할 수 있습니다", Toast.LENGTH_SHORT).show()
                                    }
                                    taggedUsers.isNotEmpty() && !validateTaggedUsers(taggedUsers) -> {
                                        Toast.makeText(context, "태그된 유저가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                    else -> {
                                        val postId = generateUniquePostId(15)
                                        createPost(postId, loggedInUserId.toString(), content.value, taggedUsers.firstOrNull())
                                        navController.navigateUp()
                                    }
                                }
                            }
                        },
                        enabled = content.value.isNotBlank()
                    ) {
                        Text("게시하기", color = if (content.value.isNotBlank()) Color.White else Color.Gray)
                    }
                },
                backgroundColor = Color(0xFF1DA1F2)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(40.dp),
                    tint = Color.Gray
                )

                Spacer(modifier = Modifier.width(8.dp))

                TextField(
                    value = content.value,
                    onValueChange = { content.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        textColor = Color.Black
                    ),
                    placeholder = { Text("무슨 일이 일어나고 있나요?", color = Color.Gray) },
                    textStyle = LocalTextStyle.current.copy(
                        color = Color.Black
                    ),
                    visualTransformation = VisualTransformation { text ->
                        buildAnnotatedString {
                            val regex = Regex("(?<=\\s|^)@\\w+(?=\\s|$)")
                            var lastIndex = 0

                            regex.findAll(text.text).forEach { matchResult ->
                                withStyle(SpanStyle(color = Color.Black)) {
                                    append(text.text.substring(lastIndex, matchResult.range.first))
                                }
                                withStyle(SpanStyle(color = Color(0xFF1DA1F2))) {
                                    append(text.text.substring(matchResult.range.first, matchResult.range.last + 1))
                                }
                                lastIndex = matchResult.range.last + 1
                            }
                            if (lastIndex < text.text.length) {
                                withStyle(SpanStyle(color = Color.Black)) {
                                    append(text.text.substring(lastIndex))
                                }
                            }
                        }.let { TransformedText(it, OffsetMapping.Identity) }
                    }
                )
            }
        }
    }
}

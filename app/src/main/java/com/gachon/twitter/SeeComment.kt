package com.gachon.twitter

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.launch
import android.widget.Toast

@Composable
fun SeeComment(navController: NavHostController, postId: String, commentId: String, userViewModel: UserViewModel) {
    val comment = remember { mutableStateOf<Comment?>(null) }
    val replies = remember { mutableStateOf<List<Comment>>(emptyList()) }
    val newReplyText = remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val loggedInUserId = userViewModel.loggedInUserId.collectAsState().value
    val replyLikeStates = remember { mutableStateOf(mapOf<String, Boolean>()) }

    // 명확한 색상 값 설정
    val primaryColor = Color(0xFF1DA1F2)  // Twitter Blue
    val onPrimaryColor = Color.White
    val backgroundColor = Color.White
    val onSurfaceColor = Color.Gray
    val likedColor = Color.Blue

    // 초기 데이터 로딩
    LaunchedEffect(commentId) {
        val mainCommentLiked = checkIfLiked(loggedInUserId.toString(), null, commentId)
        replyLikeStates.value = mapOf(commentId to mainCommentLiked)

        val rawComment = fetchCommentById(commentId)
        val stats = getCommentStats(commentId)
        comment.value = rawComment?.copy(
            numOfReplies = stats.totalReplies,
            totalLikes = stats.totalLikes
        )

        val rawReplies = fetchReplies(commentId)
        replies.value = rawReplies.map { reply ->
            val replyStats = getCommentStats(reply.commentId)
            reply.copy(
                numOfReplies = replyStats.totalReplies,
                totalLikes = replyStats.totalLikes
            )
        }
        replyLikeStates.value = replyLikeStates.value + replies.value.associate { reply ->
            reply.commentId to checkIfLiked(loggedInUserId.toString(), null, reply.commentId)
        }
    }

    // 댓글 좋아요 처리 함수
    val handleMainCommentLike = {
        scope.launch {
            if (replyLikeStates.value[commentId] == true) {
                unlikeItem(loggedInUserId.toString(), null, commentId)
            } else {
                likeItem(loggedInUserId.toString(), null, commentId)
            }
            val rawComment = fetchCommentById(commentId)
            val stats = getCommentStats(commentId)
            comment.value = rawComment?.copy(
                numOfReplies = stats.totalReplies,
                totalLikes = stats.totalLikes
            )
            replyLikeStates.value = replyLikeStates.value + (commentId to !(replyLikeStates.value[commentId] ?: false))
        }
    }

    // 대댓글 좋아요 처리 함수
    val handleReplyLike = { replyId: String ->
        scope.launch {
            if (replyLikeStates.value[replyId] == true) {
                unlikeItem(loggedInUserId.toString(), null, replyId)
            } else {
                likeItem(loggedInUserId.toString(), null, replyId)
            }
            val rawReplies = fetchReplies(commentId)
            replies.value = rawReplies.map { reply ->
                val stats = getCommentStats(reply.commentId)
                reply.copy(
                    numOfReplies = stats.totalReplies,
                    totalLikes = stats.totalLikes
                )
            }
            replyLikeStates.value = replies.value.associate { r ->
                r.commentId to checkIfLiked(loggedInUserId.toString(), null, r.commentId)
            }
        }
    }

    // 댓글 작성 처리 함수
    val handleReplySubmit = {
        if (newReplyText.value.isBlank()) {
            Toast.makeText(context, "내용을 입력하세요", Toast.LENGTH_SHORT).show()
        } else {
            scope.launch {
                val newCommentId = generateUniqueCommentId(15)
                createComment(newCommentId, postId, loggedInUserId.toString(), newReplyText.value, commentId)
                newReplyText.value = ""

                replies.value = fetchReplies(commentId)
                comment.value = fetchCommentById(commentId)
                replyLikeStates.value = replies.value.associate { r ->
                    r.commentId to checkIfLiked(loggedInUserId.toString(), null, r.commentId)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("댓글 보기") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = onPrimaryColor)
                    }
                },
                backgroundColor = primaryColor,
                contentColor = onPrimaryColor
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 80.dp)
            ) {
                comment.value?.let { currentComment ->
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.size(48.dp),
                                colorFilter = ColorFilter.tint(onSurfaceColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "${currentComment.nickname} @${currentComment.userId}",
                                    fontSize = 20.sp,
                                    color = Color.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(currentComment.content, color = Color.Black)
                        Spacer(modifier = Modifier.height(4.dp))

                        Row {
                            Text(
                                text = "댓글 수: ${currentComment.numOfReplies}",
                                color = onSurfaceColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "좋아요 ${currentComment.totalLikes}",
                                modifier = Modifier.clickable { handleMainCommentLike() },
                                color = if (replyLikeStates.value[commentId] == true) likedColor else onSurfaceColor
                            )
                        }
                    }

                    Divider(color = onSurfaceColor, thickness = 1.dp)

                    replies.value.forEach { reply ->
                        CommentItem(
                            comment = reply,
                            isLiked = replyLikeStates.value[reply.commentId] ?: false,
                            onLikeClick = { handleReplyLike(reply.commentId) },
                            onCommentClick = { navController.navigate("seeComment/${postId}/${reply.commentId}") }
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(backgroundColor)
            ) {
                Divider(color = onSurfaceColor)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = newReplyText.value,
                        onValueChange = { newReplyText.value = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        placeholder = { Text("댓글을 입력하세요", color = onSurfaceColor) },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
                            focusedIndicatorColor = primaryColor,
                            unfocusedIndicatorColor = onSurfaceColor,
                            cursorColor = primaryColor
                        ),
                        singleLine = true
                    )
                    Button(
                        onClick = { handleReplySubmit() },
                        enabled = newReplyText.value.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = primaryColor,
                            contentColor = onPrimaryColor
                        )
                    ) {
                        Text("전송")
                    }
                }
            }
        }
    }
}

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.launch

@Composable
fun SeeComment(navController: NavHostController, postId: String, commentId: String, userViewModel: UserViewModel) {
    val comment = remember { mutableStateOf<Comment?>(null) }
    val replies = remember { mutableStateOf<List<Comment>>(emptyList()) }
    val newReplyText = remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val loggedInUserId = userViewModel.loggedInUserId.collectAsState().value
    val replyLikeStates = remember { mutableStateOf(mapOf<String, Boolean>()) }

    // 초기 데이터 로딩
    LaunchedEffect(commentId) {
        // 메인 댓글의 좋아요 상태도 초기화해야 함
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
        // 대댓글들의 좋아요 상태를 기존 맵에 추가
        replyLikeStates.value = replyLikeStates.value + replies.value.associate { reply ->
            reply.commentId to checkIfLiked(loggedInUserId.toString(), null, reply.commentId)
        }
    }

    // 댓글 좋아요 처리 함수
    val handleReplyLike = { replyId: String ->
        scope.launch {
            if (replyLikeStates.value[replyId] == true) {
                unlikeItem(loggedInUserId.toString(), null, replyId)
            } else {
                likeItem(loggedInUserId.toString(), null, replyId)
            }
            // 댓글 목록과 메인 댓글 모두 새로고침
            val rawReplies = fetchReplies(commentId)
            replies.value = rawReplies.map { reply ->
                val stats = getCommentStats(reply.commentId)
                reply.copy(
                    numOfReplies = stats.totalReplies,
                    totalLikes = stats.totalLikes
                )
            }
            val rawComment = fetchCommentById(commentId)
            val mainStats = getCommentStats(commentId)
            comment.value = rawComment?.copy(
                numOfReplies = mainStats.totalReplies,
                totalLikes = mainStats.totalLikes
            )
            replyLikeStates.value = replies.value.associate { r ->
                r.commentId to checkIfLiked(loggedInUserId.toString(), null, r.commentId)
            }
        }
    }

    // 댓인 댓글 좋아요 처리 함수
    val handleMainCommentLike = {
        scope.launch {
            if (replyLikeStates.value[commentId] == true) {
                unlikeItem(loggedInUserId.toString(), null, commentId)
            } else {
                likeItem(loggedInUserId.toString(), null, commentId)
            }
            // 메인 댓글 정보 새로고침
            val rawComment = fetchCommentById(commentId)
            val stats = getCommentStats(commentId)
            comment.value = rawComment?.copy(
                numOfReplies = stats.totalReplies,
                totalLikes = stats.totalLikes
            )
            // 좋아요 상태 토글
            replyLikeStates.value = replyLikeStates.value + (commentId to !(replyLikeStates.value[commentId] ?: false))
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

                // 데이터 새로고침 (메인 댓글 포함)
                replies.value = fetchReplies(commentId)
                comment.value = fetchCommentById(commentId)  // 메인 댓글 정보도 업데이트
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
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
                                colorFilter = ColorFilter.tint(Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "${currentComment.nickname} @${currentComment.userId}",
                                    fontSize = 20.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(currentComment.content)
                        Spacer(modifier = Modifier.height(4.dp))

                        Row {
                            Text(
                                text = "댓글 수: ${currentComment.numOfReplies}",
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "좋아요 ${currentComment.totalLikes}",
                                modifier = Modifier.clickable { handleMainCommentLike() },
                                color = if (replyLikeStates.value[commentId] == true) Color.Blue else Color.Gray
                            )
                        }
                    }

                    Divider(color = Color.Black, thickness = 1.dp)

                    // 댓글 목록
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

            // 하단 댓글 입력 필드
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                Divider(color = Color.LightGray)
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
                        placeholder = { Text("댓글을 입력하세요") },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                    Button(
                        onClick = { handleReplySubmit() },
                        enabled = newReplyText.value.isNotBlank()
                    ) {
                        Text("전송")
                    }
                }
            }
        }
    }
}




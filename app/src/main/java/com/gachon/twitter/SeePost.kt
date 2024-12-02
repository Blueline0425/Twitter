package com.gachon.twitter

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

data class Comment(
    val commentId: String,
    val postId: String,
    val userId: String,
    val parentCommentId: String? = null,
    val content: String,
    val numOfLikes: Int = 0,
    val totalLikes: Int = 0,
    val numOfReplies: Int = 0,
    val timestamp: java.sql.Timestamp,
    val nickname: String
)

@Composable
fun SeePost(navController: NavHostController, postId: String, userViewModel: UserViewModel) {
    val post = remember { mutableStateOf<Post?>(null) }
    val comments = remember { mutableStateOf<List<Comment>>(emptyList()) }
    val newCommentText = remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val loggedInUserId = userViewModel.loggedInUserId.collectAsState().value
    val isPostLiked = remember { mutableStateOf(false) }
    val commentLikeStates = remember { mutableStateOf(mapOf<String, Boolean>()) }
    val totalLikes = remember { mutableStateOf(0) }
    val totalComments = remember { mutableStateOf(0) }

    // 초기 데이터 로딩
    LaunchedEffect(postId) {
        post.value = fetchPostById(postId)
        val rawComments = fetchComments(postId)
        comments.value = rawComments.map { comment ->
            val stats = getCommentStats(comment.commentId)
            comment.copy(
                numOfReplies = stats.totalReplies,
                totalLikes = stats.totalLikes
            )
        }
        isPostLiked.value = checkIfLiked(loggedInUserId.toString(), postId, null)
        commentLikeStates.value = comments.value.associate { comment ->
            comment.commentId to checkIfLiked(loggedInUserId.toString(), null, comment.commentId)
        }
        totalLikes.value = getTotalLikes(postId)
        totalComments.value = getTotalComments(postId)
    }

    // 게시글 좋아요 처리 함수
    val handlePostLike: () -> Unit = {
        scope.launch {
            if (isPostLiked.value) {
                unlikeItem(loggedInUserId.toString(), postId, null)
            } else {
                likeItem(loggedInUserId.toString(), postId, null)
            }
            isPostLiked.value = !isPostLiked.value
            totalLikes.value = getTotalLikes(postId)
        }
    }

    // 댓글 좋아요 처리 함수
    val handleCommentLike = { commentId: String ->
        scope.launch {
            if (commentLikeStates.value[commentId] == true) {
                unlikeItem(loggedInUserId.toString(), null, commentId)
            } else {
                likeItem(loggedInUserId.toString(), null, commentId)
            }
            val updatedComments = fetchComments(postId)
            comments.value = updatedComments.map { comment ->
                val stats = getCommentStats(comment.commentId)
                comment.copy(
                    numOfReplies = stats.totalReplies,
                    totalLikes = stats.totalLikes
                )
            }
            commentLikeStates.value = comments.value.associate { comment ->
                comment.commentId to checkIfLiked(loggedInUserId.toString(), null, comment.commentId)
            }
            totalLikes.value = getTotalLikes(postId)
        }
    }

    // 댓글 작성 처리 함수
    val handleCommentSubmit = {
        if (newCommentText.value.isBlank()) {
            Toast.makeText(context, "내용을 입력하세요", Toast.LENGTH_SHORT).show() // Toast 사용
        } else {
            scope.launch {
                val commentId = generateUniqueCommentId(15)
                createComment(commentId, postId, loggedInUserId.toString(), newCommentText.value)
                newCommentText.value = ""

                // 데이터 새로고침
                val updatedComments = fetchComments(postId)
                comments.value = updatedComments.map { comment ->
                    val stats = getCommentStats(comment.commentId)
                    comment.copy(
                        numOfReplies = stats.totalReplies,
                        totalLikes = stats.totalLikes
                    )
                }
                totalLikes.value = getTotalLikes(postId)
                totalComments.value = getTotalComments(postId)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("게시물 보기") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                backgroundColor = Color(0xFF1DA1F2),
                contentColor = Color.White
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 80.dp)
            ) {
                post.value?.let { currentPost ->
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
                                    text = "${currentPost.nickname} @${currentPost.userId}",
                                    fontSize = 20.sp,
                                    color = Color.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(currentPost.content ?: "내용 없음", color = Color.Black)
                        Spacer(modifier = Modifier.height(4.dp))

                        Row {
                            Text("댓글 수: ${totalComments.value}", color = Color(0xFF1DA1F2))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "좋아요 ${totalLikes.value}",
                                modifier = Modifier.clickable { handlePostLike() },
                                color = if (isPostLiked.value) Color.Blue else Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("태그: ${currentPost.tag ?: "없음"}", color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("업로드 시간: ${currentPost.uploadTimestamp?.let { formatDate(it) } ?: "알 수 없음"}", color = Color.Gray)
                    }

                    Divider(color = Color.Gray, thickness = 1.dp)

                    // 댓글 목록
                    comments.value.forEach { comment ->
                        CommentItem(
                            comment = comment,
                            isLiked = commentLikeStates.value[comment.commentId] ?: false,
                            onLikeClick = { handleCommentLike(comment.commentId) },
                            onCommentClick = {
                                navController.navigate("seecomment/${comment.postId}/${comment.commentId}")
                            }
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
                Divider(color = Color.Gray)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = newCommentText.value,
                        onValueChange = { newCommentText.value = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        placeholder = { Text("댓글을 입력하세요", color = Color.Gray) },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
                            focusedIndicatorColor = Color(0xFF1DA1F2),
                            unfocusedIndicatorColor = Color.Gray,
                            cursorColor = Color(0xFF1DA1F2)
                        ),
                        singleLine = true
                    )
                    Button(
                        onClick = { handleCommentSubmit() },
                        enabled = newCommentText.value.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF1DA1F2),
                            contentColor = Color.White
                        )
                    ) {
                        Text("전송")
                    }
                }
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onCommentClick() }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Picture",
                modifier = Modifier.size(32.dp),
                colorFilter = ColorFilter.tint(Color.Gray)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = comment.nickname, style = MaterialTheme.typography.subtitle1, color = Color.Black)
                Text(text = "@${comment.userId}", style = MaterialTheme.typography.caption, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(text = comment.content, style = MaterialTheme.typography.body1, color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatDate(comment.timestamp),
                style = MaterialTheme.typography.caption,
                color = Color.Gray
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "댓글 수: ${comment.numOfReplies}",
                    style = MaterialTheme.typography.caption,
                    color = Color.Gray
                )
                Text(
                    text = "좋아요 ${comment.totalLikes}",
                    modifier = Modifier.clickable { onLikeClick() },
                    color = if (isLiked) Color.Blue else Color.Gray,
                    style = MaterialTheme.typography.caption
                )
            }
        }
        Divider(modifier = Modifier.padding(top = 8.dp), color = Color.Gray)
    }
}

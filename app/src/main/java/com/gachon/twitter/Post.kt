package com.gachon.twitter

data class Post(
    val postId: String,          // post_id
    val content: String,         // content
    val numOfLikes: Int,        // num_of_likes
    val userId: String,         // user_user_id
    val tag: String?,           // tag (nullable)
    val uploadTimestamp: String,  // upload_timestamp
    val commentCount: Int        // 댓글 수
)
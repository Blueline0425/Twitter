package com.gachon.twitter

import android.app.Activity
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

fun fetchPosts(context: Activity, posts: SnapshotStateList<Post>) {
    val url = "jdbc:mysql://localhost/twitter3"
    val user = "root"
    val password = "1234"

 Thread {
        try {
            // 데이터베이스 연결
            val connection: Connection = DriverManager.getConnection(url, user, password)
            val statement: Statement = connection.createStatement()
            System.out.println(connection);
            // 게시글과 댓글 수를 가져오는 쿼리
            val query = """
                SELECT p.post_id, p.content, p.num_of_likes, p.user_user_id, p.tag, p.upload_timestamp,
                       (SELECT COUNT(*) FROM comment WHERE post_id = p.post_id) AS comment_count,
                       (SELECT COUNT(*) FROM child_comment WHERE post_id = p.post_id) AS child_comment_count
                FROM posts p
            """
            val resultSet: ResultSet = statement.executeQuery(query)

            // 게시글 리스트를 저장할 리스트
            val fetchedPosts = mutableListOf<Post>()

            while (resultSet.next()) {
                val postId = resultSet.getString("post_id")
                val content = resultSet.getString("content")
                val numOfLikes = resultSet.getInt("num_of_likes")
                val userId = resultSet.getString("user_user_id")
                val tag = resultSet.getString("tag") // nullable
                val uploadTimestamp = resultSet.getString("upload_timestamp")
                val commentCount = resultSet.getInt("comment_count") + resultSet.getInt("child_comment_count")

                fetchedPosts.add(Post(postId, content, numOfLikes, userId, tag, uploadTimestamp, commentCount))
            }

            // 연결 종료
            resultSet.close()
            statement.close()
            connection.close()

            // UI 업데이트 (메인 스레드에서 실행)
            context.runOnUiThread {
                posts.clear() // 기존 리스트 초기화
                posts.addAll(fetchedPosts) // 새로 가져온 게시글 추가
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }.start()
}
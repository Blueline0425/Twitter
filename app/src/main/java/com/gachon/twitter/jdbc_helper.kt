package com.gachon.twitter

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Timestamp



suspend fun fetchPosts(loggedInUserId: String): List<Post> = withContext(Dispatchers.IO) {
    val posts = mutableListOf<Post>()
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"

    try {
        Class.forName("com.mysql.jdbc.Driver")
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(
            """
            SELECT p.post_id, p.content, p.num_of_likes, 
                   (SELECT COUNT(*) FROM comment c WHERE c.post_id = p.post_id) as num_of_comments,
                   p.user_id, p.tag, p.upload_timestamp, u.nickname
            FROM post p
            JOIN user u ON p.user_id = u.user_id
            WHERE p.user_id IN (
                SELECT following_id 
                FROM follow 
                WHERE follower_id = '$loggedInUserId'
            )
            OR p.user_id = '$loggedInUserId'
            ORDER BY p.upload_timestamp DESC
            """
        )

        while (resultSet.next()) {
            val post = Post(
                postId = resultSet.getString("post_id"),
                content = resultSet.getString("content"),
                numOfLikes = resultSet.getInt("num_of_likes"),
                numOfComments = resultSet.getInt("num_of_comments"),
                userId = resultSet.getString("user_id"),
                tag = resultSet.getString("tag"),
                uploadTimestamp = resultSet.getTimestamp("upload_timestamp"),
                nickname = resultSet.getString("nickname")
            )
            posts.add(post)
        }

        resultSet.close()
        statement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return@withContext posts
}

suspend fun getNicknameFromUserId(userId: String): String = withContext(Dispatchers.IO) {
    var nickname = "unknown"
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"

    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery("SELECT nickname FROM user WHERE user_id='$userId'")

        if (resultSet.next()) {
            nickname = resultSet.getString("nickname")
        }

        resultSet.close()
        statement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return@withContext nickname
}

suspend fun fetchChatList(loggedInUserId: String): List<Message> = withContext(Dispatchers.IO) {
    val messages = mutableListOf<Message>()
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"

    try {
        Class.forName("com.mysql.jdbc.Driver")
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(
            """
            SELECT m.message_id, m.content, m.timestamp, u.nickname, u.user_id, m.is_read,
                   m.sender_id, m.receiver_id
            FROM message m
            JOIN user u ON (m.sender_id = u.user_id OR m.receiver_id = u.user_id)
            WHERE (m.receiver_id = '$loggedInUserId' OR m.sender_id = '$loggedInUserId')
            AND u.user_id != '$loggedInUserId'
            AND m.timestamp = (
                SELECT MAX(m2.timestamp)
                FROM message m2
                WHERE (m2.receiver_id = '$loggedInUserId' OR m2.sender_id = '$loggedInUserId')
                AND (m2.sender_id = u.user_id OR m2.receiver_id = u.user_id)
            )
            ORDER BY m.timestamp DESC
            """
        )

        while (resultSet.next()) {
            val message = Message(
                messageId = resultSet.getInt("message_id"),
                senderId = resultSet.getString("sender_id"),
                receiverId = resultSet.getString("receiver_id"),
                content = resultSet.getString("content"),
                timestamp = resultSet.getTimestamp("timestamp"),
                isRead = resultSet.getInt("is_read")
            )
            messages.add(message)
        }

        resultSet.close()
        statement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return@withContext messages
}

suspend fun validateUser(userId: String, password: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"
    var message = "일치하는 ID가 없습니다"

    try {
        Class.forName("com.mysql.jdbc.Driver")
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(
            "SELECT pwd FROM user WHERE user_id = '$userId'"
        )

        if (resultSet.next()) {
            val dbPassword = resultSet.getString("pwd")
            if (dbPassword == password) {
                return@withContext Pair(true, "로그인 성공")
            } else {
                message = "잘못된 비밀번호입니다"
            }
        }

        resultSet.close()
        statement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return@withContext Pair(false, message)
}


suspend fun fetchMessagesWithUser(userId: String, loggedInUserId: String): List<Message> = withContext(Dispatchers.IO) {
    val messages = mutableListOf<Message>()
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"

    try {
        Class.forName("com.mysql.jdbc.Driver")
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(
            """
            SELECT m.message_id, m.content, m.timestamp, m.sender_id, m.receiver_id, m.is_read
            FROM message m
            WHERE (m.sender_id = '$loggedInUserId' AND m.receiver_id = '$userId')
               OR (m.sender_id = '$userId' AND m.receiver_id = '$loggedInUserId')
            ORDER BY m.timestamp ASC
            """
        )

        while (resultSet.next()) {
            val message = Message(
                messageId = resultSet.getInt("message_id"),
                senderId = resultSet.getString("sender_id"),
                receiverId = resultSet.getString("receiver_id"),
                content = resultSet.getString("content"),
                timestamp = resultSet.getTimestamp("timestamp"),
                isRead = resultSet.getInt("is_read")
            )
            messages.add(message)
        }

        resultSet.close()
        statement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return@withContext messages
}


suspend fun markMessagesAsRead(senderId: String, loggedInUserId: String) = withContext(Dispatchers.IO) {
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"

    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val updateQuery = """
            UPDATE message 
            SET is_read = 1 
            WHERE sender_id = ? 
            AND receiver_id = ? 
            AND is_read = 0
        """
        
        connection.prepareStatement(updateQuery).use { stmt ->
            stmt.setString(1, senderId)        // 메시지를 보낸 사람 (대화 상대)
            stmt.setString(2, loggedInUserId)  // 메시지를 받은 사람 (로그인한 사용자)
            stmt.executeUpdate()
        }
        
        connection.close()
    } catch (e: Exception) {
        println("Error in markMessagesAsRead: ${e.message}")
        e.printStackTrace()
    }
}

suspend fun sendMessage(senderId: String, receiverId: String, content: String) {
    withContext(Dispatchers.IO) {
        val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
        val user = "admin"
        val passwd = "1234"

        try {
            val connection: Connection = DriverManager.getConnection(url, user, passwd)
            val preparedStatement = connection.prepareStatement(
                """
                INSERT INTO message (content, timestamp, is_read, sender_id, receiver_id)
                VALUES (?, NOW(), 0, ?, ?)
                """
            )
            preparedStatement.setString(1, content)
            preparedStatement.setString(2, senderId)
            preparedStatement.setString(3, receiverId)
            preparedStatement.executeUpdate()

            preparedStatement.close()
            connection.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

suspend fun checkUserIdDuplicate(userId: String): Boolean = withContext(Dispatchers.IO) {
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"
    var isDuplicate = false

    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery("SELECT user_id FROM user WHERE user_id='$userId'")

        if (resultSet.next()) {
            isDuplicate = true
        }

        resultSet.close()
        statement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return@withContext isDuplicate
}

suspend fun createUser(userId: String, password: String, nickname: String) {
    withContext(Dispatchers.IO) {
        val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
        val user = "admin"
        val passwd = "1234"

        try {
            val connection: Connection = DriverManager.getConnection(url, user, passwd)
            val preparedStatement = connection.prepareStatement(
                "INSERT INTO user (user_id, pwd, nickname) VALUES (?, ?, ?)"
            )
            preparedStatement.setString(1, userId)
            preparedStatement.setString(2, password)
            preparedStatement.setString(3, nickname)
            preparedStatement.executeUpdate()

            preparedStatement.close()
            connection.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
suspend fun changePassword(userId: String, newPassword: String) {
    withContext(Dispatchers.IO) {
        val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
        val user = "admin"
        val passwd = "1234"

        try {
            val connection: Connection = DriverManager.getConnection(url, user, passwd)
            val preparedStatement = connection.prepareStatement(
                "UPDATE user SET pwd = ? WHERE user_id = ?"
            )
            preparedStatement.setString(1, newPassword)
            preparedStatement.setString(2, userId)
            preparedStatement.executeUpdate()

            preparedStatement.close()
            connection.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

suspend fun checkIfFollowing(loggedInUserId: String, userId: String): Boolean = withContext(Dispatchers.IO) {
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"
    var isFollowing = false

    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(
            "SELECT * FROM follow WHERE follower_id='$loggedInUserId' AND following_id='$userId'"
        )

        if (resultSet.next()) {
            isFollowing = true
        }

        resultSet.close()
        statement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return@withContext isFollowing
}

suspend fun followUser(followerId: String, followingId: String) = withContext(Dispatchers.IO) {
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"

    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val preparedStatement = connection.prepareStatement(
            "INSERT INTO follow (follower_id, following_id) VALUES (?, ?)"
        )
        preparedStatement.setString(1, followerId)
        preparedStatement.setString(2, followingId)
        preparedStatement.executeUpdate()

        preparedStatement.close()
        connection.close()
    } catch (e: Exception) {
        println("Follow Error: ${e.message}")
        e.printStackTrace()
    }
}

suspend fun unfollowUser(followerId: String, followingId: String) = withContext(Dispatchers.IO) {
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"

    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val preparedStatement = connection.prepareStatement(
            "DELETE FROM follow WHERE follower_id = ? AND following_id = ?"
        )
        preparedStatement.setString(1, followerId)
        preparedStatement.setString(2, followingId)
        preparedStatement.executeUpdate()

        preparedStatement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

suspend fun fetchPostsForUser(userId: String): List<Post> = withContext(Dispatchers.IO) {
    val posts = mutableListOf<Post>()
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"

    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(
            """
            SELECT p.*, u.nickname 
            FROM post p
            JOIN user u ON p.user_id = u.user_id
            WHERE p.user_id = '$userId'
            ORDER BY p.upload_timestamp DESC
            """
        )

        while (resultSet.next()) {
            val post = Post(
                postId = resultSet.getString("post_id"),
                content = resultSet.getString("content"),
                numOfLikes = resultSet.getInt("num_of_likes"),
                userId = resultSet.getString("user_id"),
                tag = resultSet.getString("tag"),
                uploadTimestamp = resultSet.getTimestamp("upload_timestamp"),
                nickname = resultSet.getString("nickname")
            )
            posts.add(post)
        }

        resultSet.close()
        statement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return@withContext posts
}


// 팔로잉 수를 가져오는 함수 (내가 팔로우한 사람의 수)
suspend fun getFollowingCount(userId: String): Int = withContext(Dispatchers.IO) {
    var count = 0
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"

    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(
            "SELECT COUNT(*) as count FROM follow WHERE follower_id='$userId'"
        )

        if (resultSet.next()) {
            count = resultSet.getInt("count")
        }

        resultSet.close()
        statement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return@withContext count
}

// 특로워 수를 가져오는 함수 (나를 팔로우하는 사람의 수)
suspend fun getFollowerCount(userId: String): Int = withContext(Dispatchers.IO) {
    var count = 0
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"

    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(
            "SELECT COUNT(*) as count FROM follow WHERE following_id='$userId'"
        )

        if (resultSet.next()) {
            count = resultSet.getInt("count")
        }

        resultSet.close()
        statement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return@withContext count
}

// 특정 사용자가 팔로우하는 사용자 목록 가져오기
suspend fun getFollowingList(userId: String): List<UserInfo> = withContext(Dispatchers.IO) {
    val followingList = mutableListOf<UserInfo>()
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"

    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(
            """
            SELECT u.user_id, u.nickname
            FROM follow f
            JOIN user u ON f.following_id = u.user_id
            WHERE f.follower_id = '$userId'
            """
        )

        while (resultSet.next()) {
            val userInfo = UserInfo(
                userId = resultSet.getString("user_id"),
                nickname = resultSet.getString("nickname")
            )
            followingList.add(userInfo)
        }

        resultSet.close()
        statement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return@withContext followingList
}

// 특정 사용자를 팔로우하는 사용자 목록 가져오기
suspend fun getFollowerList(userId: String): List<UserInfo> = withContext(Dispatchers.IO) {
    val followerList = mutableListOf<UserInfo>()
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"

    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(
            """
            SELECT u.user_id, u.nickname
            FROM follow f
            JOIN user u ON f.follower_id = u.user_id
            WHERE f.following_id = '$userId'
            """
        )

        while (resultSet.next()) {
            val userInfo = UserInfo(
                userId = resultSet.getString("user_id"),
                nickname = resultSet.getString("nickname")
            )
            followerList.add(userInfo)
        }

        resultSet.close()
        statement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return@withContext followerList
}

// 사용자 ID로 검색
suspend fun searchUsers(searchText: String): List<UserInfo> = withContext(Dispatchers.IO) {
    val users = mutableListOf<UserInfo>()
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"
    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(
            """
            SELECT user_id, nickname
            FROM user
            WHERE user_id LIKE '%$searchText%'
            """
        )

        while (resultSet.next()) {
            val userInfo = UserInfo(
                userId = resultSet.getString("user_id"),
                nickname = resultSet.getString("nickname")
            )
            users.add(userInfo)
        }

        resultSet.close()
        statement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return@withContext users
}

// 게시글 내용으로 검색
suspend fun searchPosts(searchText: String): List<Post> = withContext(Dispatchers.IO) {
    val posts = mutableListOf<Post>()
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"
    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(
            """
            SELECT p.*, u.nickname
            FROM post p
            JOIN user u ON p.user_id = u.user_id
            WHERE p.content LIKE '%$searchText%'
            ORDER BY p.upload_timestamp DESC
            """
        )

        while (resultSet.next()) {
            val post = Post(
                postId = resultSet.getString("post_id"),
                content = resultSet.getString("content"),
                numOfLikes = resultSet.getInt("num_of_likes"),
                userId = resultSet.getString("user_id"),
                tag = resultSet.getString("tag"),
                uploadTimestamp = resultSet.getTimestamp("upload_timestamp"),
                nickname = resultSet.getString("nickname")
            )
            posts.add(post)
        }

        resultSet.close()
        statement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return@withContext posts
}

data class UserInfo(
    val userId: String,
    val nickname: String
)
data class Message(
    val messageId: Int,  // AUTO_INCREMENT로 변경
    val senderId: String,
    val receiverId: String,
    val content: String,
    val timestamp: Timestamp,
    val isRead: Int
)

data class Post(
    val postId: String,
    val userId: String,
    val content: String?,
    val numOfLikes: Int = 0,
    val numOfComments: Int = 0,
    val tag: String?,
    val uploadTimestamp: Timestamp,
    val nickname: String?
)

suspend fun fetchPostById(postId: String): Post? = withContext(Dispatchers.IO) {
    var post: Post? = null
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"

    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(
            """
            SELECT p.*, u.nickname,
                   (SELECT COUNT(*) FROM comment c WHERE c.post_id = p.post_id) as num_of_comments
            FROM post p
            JOIN user u ON p.user_id = u.user_id
            WHERE p.post_id = '$postId'
            """
        )

        if (resultSet.next()) {
            post = Post(
                postId = resultSet.getString("post_id"),
                content = resultSet.getString("content"),
                numOfLikes = resultSet.getInt("num_of_likes"),
                numOfComments = resultSet.getInt("num_of_comments"),
                userId = resultSet.getString("user_id"),
                tag = resultSet.getString("tag"),
                uploadTimestamp = resultSet.getTimestamp("upload_timestamp"),
                nickname = resultSet.getString("nickname")
            )
        }

        resultSet.close()
        statement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return@withContext post
}

// 태그된 유저 출
fun extractTaggedUsers(content: String): List<String> {
    val regex = Regex("(?<=\\s|^)@\\w+(?=\\s|$)")
    return regex.findAll(content)
        .map { it.value.substring(1) } // @ 제거
        .toList()
}

// 태그된 유저가 존재하는지 확인
suspend fun validateTaggedUsers(taggedUsers: List<String>): Boolean = withContext(Dispatchers.IO) {
    if (taggedUsers.size > 1) {
        return@withContext false
    }
    
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"

    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.createStatement()
        val userId = taggedUsers.first()
        val resultSet = statement.executeQuery(
            "SELECT user_id FROM user WHERE user_id = '$userId'"
        )
        
        val exists = resultSet.next()
        
        resultSet.close()
        statement.close()
        connection.close()
        
        return@withContext exists
    } catch (e: Exception) {
        e.printStackTrace()
        return@withContext false
    }
}

// 게시글 생성
suspend fun createPost(postId: String, userId: String, content: String, tag: String?) = withContext(Dispatchers.IO) {
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"

    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val preparedStatement = connection.prepareStatement(
            """
            INSERT INTO post (post_id, user_id, content, tag)
            VALUES (?, ?, ?, ?)
            """
        )
        preparedStatement.setString(1, postId)
        preparedStatement.setString(2, userId)
        preparedStatement.setString(3, content)
        preparedStatement.setString(4, tag)
        preparedStatement.executeUpdate()

        preparedStatement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// 랜덤 문자열 생성 (post_id용)
fun generateRandomString(length: Int): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

suspend fun generateUniquePostId(length: Int): String = withContext(Dispatchers.IO) {
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"
    
    var postId: String
    do {
        val chars = ('A'..'Z').toList() + ('a'..'z').toList() + ('0'..'9').toList()
        postId = (1..length)
            .map { chars[kotlin.random.Random.nextInt(chars.size)] }
            .joinToString("")   
        
        var isDuplicate = false
        try {
            val connection: Connection = DriverManager.getConnection(url, user, passwd)
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(
                "SELECT post_id FROM post WHERE post_id = '$postId'"
            )
            
            isDuplicate = resultSet.next()
            
            resultSet.close()
            statement.close()
            connection.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        if (!isDuplicate) {
            break
        }
    } while (true)
    
    postId // 명시적 반환
}

// 게시물의 댓글 조회
suspend fun fetchComments(postId: String): List<Comment> = withContext(Dispatchers.IO) {
    val comments = mutableListOf<Comment>()
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"

    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val query = """
            SELECT 
                c.*,
                u.nickname,
                (SELECT COUNT(*) FROM `like` l WHERE l.comment_id = c.comment_id) as num_of_likes,
                (SELECT COUNT(*) FROM comment WHERE parent_comment_id = c.comment_id) as num_replies
            FROM comment c
            JOIN user u ON c.user_id = u.user_id
            WHERE c.post_id = ? AND c.parent_comment_id IS NULL
            ORDER BY c.timestamp DESC
        """
        
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setString(1, postId)
        
        val resultSet = preparedStatement.executeQuery()
        while (resultSet.next()) {
            val commentId = resultSet.getString("comment_id")
            comments.add(Comment(
                commentId = commentId,
                postId = resultSet.getString("post_id"),
                userId = resultSet.getString("user_id"),
                parentCommentId = resultSet.getString("parent_comment_id"),
                content = resultSet.getString("content"),
                numOfLikes = resultSet.getInt("num_of_likes"),
                totalLikes = getTotalCommentLikes(commentId),
                numOfReplies = getTotalCommentReplies(commentId),
                timestamp = resultSet.getTimestamp("timestamp"),
                nickname = resultSet.getString("nickname")
            ))
        }
        
        resultSet.close()
        preparedStatement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return@withContext comments
}

// 댓글의 대댓글 조회
suspend fun fetchReplies(commentId: String): List<Comment> = withContext(Dispatchers.IO) {
    val replies = mutableListOf<Comment>()
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"

    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val query = """
            SELECT 
                c.*,
                u.nickname,
                (SELECT COUNT(*) FROM `like` l WHERE l.comment_id = c.comment_id) as num_of_likes,
                (SELECT COUNT(*) FROM comment WHERE parent_comment_id = c.comment_id) as num_replies
            FROM comment c
            JOIN user u ON c.user_id = u.user_id
            WHERE c.parent_comment_id = ?
            ORDER BY c.timestamp DESC
        """
        
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setString(1, commentId)
        
        val resultSet = preparedStatement.executeQuery()
        while (resultSet.next()) {
            val replyId = resultSet.getString("comment_id")
            replies.add(Comment(
                commentId = replyId,
                postId = resultSet.getString("post_id"),
                userId = resultSet.getString("user_id"),
                parentCommentId = resultSet.getString("parent_comment_id"),
                content = resultSet.getString("content"),
                numOfLikes = resultSet.getInt("num_of_likes"),
                totalLikes = getTotalCommentLikes(replyId),
                numOfReplies = getTotalCommentReplies(replyId),
                timestamp = resultSet.getTimestamp("timestamp"),
                nickname = resultSet.getString("nickname")
            ))
        }
        
        resultSet.close()
        preparedStatement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return@withContext replies
}

// 좋아 상태 인
suspend fun checkIfLiked(userId: String, postId: String?, commentId: String?): Boolean = withContext(Dispatchers.IO) {
    var isLiked = false
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"

    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val query = when {
            postId != null -> "SELECT like_id FROM `like` WHERE user_id = ? AND post_id = ?"
            commentId != null -> "SELECT like_id FROM `like` WHERE user_id = ? AND comment_id = ?"
            else -> throw IllegalArgumentException("Either postId or commentId must be provided")
        }
        
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setString(1, userId)
        preparedStatement.setString(2, postId ?: commentId)
        
        val resultSet = preparedStatement.executeQuery()
        isLiked = resultSet.next()

        resultSet.close()
        preparedStatement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return@withContext isLiked
}

// 게시글/댓글 좋아요
suspend fun likeItem(userId: String, postId: String?, commentId: String?) = withContext(Dispatchers.IO) {
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"
    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        
        // 댓글 좋아요인 경우 해당 댓글이 속한 post_id 가져오기
        val targetPostId = if (commentId != null) {
            val query = "SELECT post_id FROM comment WHERE comment_id = ?"
            connection.prepareStatement(query).use { stmt ->
                stmt.setString(1, commentId)
                val rs = stmt.executeQuery()
                if (rs.next()) rs.getString("post_id") else null
            }
        } else {
            postId
        }
        
        // 좋아요 추가
        val likeId = generateRandomString(15)
        val insertLikeQuery = """
            INSERT INTO `like` (like_id, user_id, post_id, comment_id)
            VALUES (?, ?, ?, ?)
        """
        
        connection.prepareStatement(insertLikeQuery).use { stmt ->
            stmt.setString(1, likeId)
            stmt.setString(2, userId)
            stmt.setString(3, targetPostId)  // 댓글 좋아요일 경우 해당 댓글이 속한 post_id
            stmt.setString(4, commentId)
            stmt.executeUpdate()
        }
        
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// 게시글/댓글 좋아요 취소
suspend fun unlikeItem(userId: String, postId: String?, commentId: String?) = withContext(Dispatchers.IO) {
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"
    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        
        // 좋아요 삭제 쿼리 수정
        val deleteLikeQuery = when {
            postId != null -> "DELETE FROM `like` WHERE user_id = ? AND post_id = ? AND comment_id IS NULL"  // 게시물 좋아요만 삭제
            commentId != null -> "DELETE FROM `like` WHERE user_id = ? AND comment_id = ?"  // 특정 댓글의 좋아요만 삭제
            else -> throw IllegalArgumentException("Either postId or commentId must be provided")
        }
        
        connection.prepareStatement(deleteLikeQuery).use { stmt ->
            stmt.setString(1, userId)
            stmt.setString(2, postId ?: commentId)
            stmt.executeUpdate()
        }
        
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// 게시글의 총 좋아요 수 계산 (게시글 직접 좋아요 + 댓글 좋아요)
suspend fun getTotalLikes(postId: String): Int = withContext(Dispatchers.IO) {
    var totalLikes = 0
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"
    
    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val query = """
            SELECT COUNT(*) as total_likes 
            FROM `like` 
            WHERE post_id = ?
        """
        
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setString(1, postId)
        
        val resultSet = preparedStatement.executeQuery()
        if (resultSet.next()) {
            totalLikes = resultSet.getInt("total_likes")
        }
        
        resultSet.close()
        preparedStatement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return@withContext totalLikes
}

// 게시글의 총 댓글 수 계산 (직접 댓글 + 대댓글)
suspend fun getTotalComments(postId: String): Int = withContext(Dispatchers.IO) {
    var totalComments = 0
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"
    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val query = """
            SELECT COUNT(*) as total_comments 
            FROM comment 
            WHERE post_id = ?
        """
        
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setString(1, postId)
        
        val resultSet = preparedStatement.executeQuery()
        if (resultSet.next()) {
            totalComments = resultSet.getInt("total_comments")
        }
        
        resultSet.close()
        preparedStatement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return@withContext totalComments
}

// 고유한 댓글 ID 생성 함수
suspend fun generateUniqueCommentId(length: Int): String = withContext(Dispatchers.IO) {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    var commentId: String
    do {
        commentId = (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    } while (isCommentIdExists(commentId))
    return@withContext commentId
}

// 댓글 ID 존재 여부 확인
suspend fun isCommentIdExists(commentId: String): Boolean = withContext(Dispatchers.IO) {
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"
    
    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.prepareStatement(
            "SELECT COUNT(*) FROM comment WHERE comment_id = ?"
        )
        statement.setString(1, commentId)
        val resultSet = statement.executeQuery()
        resultSet.next()
        val exists = resultSet.getInt(1) > 0
        
        resultSet.close()
        statement.close()
        connection.close()
        
        return@withContext exists
    } catch (e: Exception) {
        e.printStackTrace()
        return@withContext true // 에러 발생 시 true 반환하여 새로운 ID 생성
    }
}

// 댓글 생성 함수 (대댓글 생성도 가능)
suspend fun createComment(
    commentId: String, 
    postId: String, 
    userId: String, 
    content: String,
    parentCommentId: String? = null  // 대댓글인 경우 부모 댓글 ID 전달
) = withContext(Dispatchers.IO) {
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"
    
    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val query = """
            INSERT INTO comment (comment_id, post_id, user_id, content, parent_comment_id)
            VALUES (?, ?, ?, ?, ?)
        """
        
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setString(1, commentId)
        preparedStatement.setString(2, postId)
        preparedStatement.setString(3, userId)
        preparedStatement.setString(4, content)
        if (parentCommentId != null) {
            preparedStatement.setString(5, parentCommentId)
        } else {
            preparedStatement.setNull(5, java.sql.Types.VARCHAR)
        }
        
        preparedStatement.executeUpdate()
        preparedStatement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

suspend fun fetchCommentById(commentId: String): Comment? = withContext(Dispatchers.IO) {
    var comment: Comment? = null
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"
    
    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val query = """
            SELECT 
                c.*,
                u.nickname,
                (SELECT COUNT(*) FROM `like` WHERE comment_id = c.comment_id) as num_of_likes,
                (SELECT COUNT(*) FROM comment WHERE parent_comment_id = c.comment_id) as num_replies
            FROM comment c
            JOIN user u ON c.user_id = u.user_id
            WHERE c.comment_id = ?
        """
        
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setString(1, commentId)
        
        val resultSet = preparedStatement.executeQuery()
        if (resultSet.next()) {
            comment = Comment(
                commentId = resultSet.getString("comment_id"),
                postId = resultSet.getString("post_id"),
                userId = resultSet.getString("user_id"),
                parentCommentId = resultSet.getString("parent_comment_id"),
                content = resultSet.getString("content"),
                numOfLikes = resultSet.getInt("num_of_likes"),
                totalLikes = resultSet.getInt("num_of_likes"),
                numOfReplies = resultSet.getInt("num_replies"),
                timestamp = resultSet.getTimestamp("timestamp"),
                nickname = resultSet.getString("nickname")
            )
        }
        
        resultSet.close()
        preparedStatement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    
    return@withContext comment
}

// 댓글의 총 좋아요 수 계산 (현재 댓글 + 모든 하위 댓글의 좋아요)
suspend fun getTotalCommentLikes(commentId: String): Int = withContext(Dispatchers.IO) {
    var totalLikes = 0
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"
    
    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val query = """
            SELECT 
                (SELECT COUNT(*) FROM `like` WHERE comment_id = ?) +
                (SELECT COALESCE(SUM(
                    (SELECT COUNT(*) FROM `like` WHERE comment_id = c.comment_id)
                ), 0)
                FROM comment c
                WHERE c.parent_comment_id = ?) as total_likes
        """
        
        var preparedStatement = connection.prepareStatement(query)
        preparedStatement.setString(1, commentId)
        preparedStatement.setString(2, commentId)
        
        var resultSet = preparedStatement.executeQuery()
        if (resultSet.next()) {
            totalLikes = resultSet.getInt("total_likes")
        }
        
        resultSet.close()
        preparedStatement.close()
        
        // 디버깅 출력
        println("Debug - CommentID: $commentId")
        println("Debug - Total Likes: $totalLikes")
        
    } catch (e: Exception) {
        println("Error in getCommentStats: ${e.message}")
        e.printStackTrace()
    }
    
    return@withContext totalLikes
}

// 댓글의 총 대댓글 수 계산 (모든 하위 댓글 포함)
suspend fun getTotalCommentReplies(commentId: String): Int = withContext(Dispatchers.IO) {
    var totalReplies = 0
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"
    
    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val query = """
            SELECT COUNT(*) as total_replies
            FROM comment
            WHERE parent_comment_id = ?
        """
        
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setString(1, commentId)
        
        val resultSet = preparedStatement.executeQuery()
        if (resultSet.next()) {
            totalReplies = resultSet.getInt("total_replies")
        }
        
        resultSet.close()
        preparedStatement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return@withContext totalReplies
}
// 댓글의 총 하위 댓글 수와 좋아요 수를 함께 반환하는 함수
data class CommentStats(
    val totalReplies: Int,
    val totalLikes: Int
)

suspend fun getCommentStats(commentId: String): CommentStats = withContext(Dispatchers.IO) {
    var totalReplies = 0
    var totalLikes = 0
    val url = "jdbc:mysql://192.168.123.104/twitter2?useSSL=false"
    val user = "admin"
    val passwd = "1234"
    
    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        
        // 1. 먼저 총 좋아요 수 계산
        val likesQuery = """
            SELECT 
                (SELECT COUNT(*) FROM `like` WHERE comment_id = ?) +
                (SELECT COALESCE(SUM(
                    (SELECT COUNT(*) FROM `like` WHERE comment_id = c.comment_id)
                ), 0)
                FROM comment c
                WHERE c.parent_comment_id = ?) as total_likes
        """
        
        var preparedStatement = connection.prepareStatement(likesQuery)
        preparedStatement.setString(1, commentId)
        preparedStatement.setString(2, commentId)
        
        var resultSet = preparedStatement.executeQuery()
        if (resultSet.next()) {
            totalLikes = resultSet.getInt("total_likes")
        }
        
        resultSet.close()
        preparedStatement.close()
        
        // 2. 총 대댓글 수 계산
        val repliesQuery = """
            SELECT COUNT(*) as total_replies
            FROM comment
            WHERE parent_comment_id = ?
        """
        
        preparedStatement = connection.prepareStatement(repliesQuery)
        preparedStatement.setString(1, commentId)
        
        resultSet = preparedStatement.executeQuery()
        if (resultSet.next()) {
            totalReplies = resultSet.getInt("total_replies")
        }
        
        resultSet.close()
        preparedStatement.close()
        connection.close()
        
        // 디버깅 출력
        println("Debug - CommentID: $commentId")
        println("Debug - Total Replies: $totalReplies")
        println("Debug - Total Likes: $totalLikes")
        
    } catch (e: Exception) {
        println("Error in getCommentStats: ${e.message}")
        e.printStackTrace()
    }
    
    return@withContext CommentStats(totalReplies, totalLikes)
}
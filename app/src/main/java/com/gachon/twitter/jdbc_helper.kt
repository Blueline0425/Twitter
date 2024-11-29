package com.gachon.twitter

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Timestamp


suspend fun fetchPosts(loggedInUserId: String): List<Post> = withContext(Dispatchers.IO) {
    val posts = mutableListOf<Post>()
    val url = "jdbc:mysql://192.168.219.101/twitter?useSSL=false"
    val user = "root"
    val passwd = "1234"

    try {
        Class.forName("com.mysql.jdbc.Driver")
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(
            """
            SELECT p.post_id, p.content, p.num_of_likes, p.user_user_id, p.tag, p.upload_timestamp, u.nickname
            FROM posts p
            JOIN user u ON p.user_user_id = u.user_id
            WHERE p.user_user_id IN (
                SELECT user_user_id 
                FROM following 
                WHERE following_f_id = '$loggedInUserId'
            )
            OR p.user_user_id = '$loggedInUserId'
            ORDER BY p.upload_timestamp DESC
            """
        )

        while (resultSet.next()) {
            val post = Post(
                postId = resultSet.getString("post_id"),
                content = resultSet.getString("content"),
                numOfLikes = resultSet.getInt("num_of_likes"),
                userId = resultSet.getString("user_user_id"),
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
    val url = "jdbc:mysql://192.168.219.101/twitter?useSSL=false"
    val user = "root"
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

suspend fun fetchChatList(loggedInUserId: String): List<DirectMessage> = withContext(Dispatchers.IO) {
    val messages = mutableListOf<DirectMessage>()
    val url = "jdbc:mysql://192.168.219.101/twitter?useSSL=false"
    val user = "root"
    val passwd = "1234"

    try {
        Class.forName("com.mysql.jdbc.Driver")
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(
            """
            SELECT dm.message_id, dm.content, dm.timestamp, u.nickname, u.user_id, dm.is_read
            FROM direct_message dm
            JOIN user u ON (dm.sender_user_id = u.user_id OR dm.receiver_user_id = u.user_id)
            WHERE (dm.receiver_user_id = '$loggedInUserId' OR dm.sender_user_id = '$loggedInUserId')
            AND u.user_id != '$loggedInUserId'
            AND dm.timestamp = (
                SELECT MAX(dm2.timestamp)
                FROM direct_message dm2
                WHERE (dm2.receiver_user_id = '$loggedInUserId' OR dm2.sender_user_id = '$loggedInUserId')
                AND (dm2.sender_user_id = u.user_id OR dm2.receiver_user_id = u.user_id)
            )
            ORDER BY dm.timestamp DESC
            """
        )

        while (resultSet.next()) {
            val message = DirectMessage(
                messageId = resultSet.getString("message_id"),
                content = resultSet.getString("content"),
                timestamp = resultSet.getTimestamp("timestamp"),
                nickname = resultSet.getString("nickname"),
                userId = resultSet.getString("user_id"),
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
    val url = "jdbc:mysql://192.168.219.101/twitter?useSSL=false"
    val user = "root"
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


suspend fun fetchMessagesWithUser(userId: String): List<DirectMessage> = withContext(Dispatchers.IO) {
    val messages = mutableListOf<DirectMessage>()
    val url = "jdbc:mysql://192.168.219.101/twitter?useSSL=false"
    val user = "root"
    val passwd = "1234"

    try {
        Class.forName("com.mysql.jdbc.Driver")
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(
            """
            SELECT dm.message_id, dm.content, dm.timestamp, u.nickname, dm.sender_user_id, dm.is_read
            FROM direct_message dm
            JOIN user u ON dm.sender_user_id = u.user_id
            WHERE (dm.receiver_user_id = '$userId' OR dm.sender_user_id = '$userId')
            ORDER BY dm.timestamp ASC
            """
        )

        while (resultSet.next()) {
            val message = DirectMessage(
                messageId = resultSet.getString("message_id"),
                content = resultSet.getString("content"),
                timestamp = resultSet.getTimestamp("timestamp"),
                nickname = resultSet.getString("nickname"),
                userId = resultSet.getString("sender_user_id"),
                isRead = resultSet.getInt("is_read") // isRead 값 설정
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


suspend fun markMessagesAsRead(userId: String, loggedInUserId: String) {
    withContext(Dispatchers.IO) {
        val url = "jdbc:mysql://192.168.219.101/twitter?useSSL=false"
        val user = "root"
        val passwd = "1234"

        try {
            val connection: Connection = DriverManager.getConnection(url, user, passwd)
            val statement = connection.createStatement()
            statement.executeUpdate(
                """
                UPDATE direct_message 
                SET is_read = 1 
                WHERE sender_user_id = '$userId' AND receiver_user_id = '$loggedInUserId'
                """
            )
            statement.close()
            connection.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

suspend fun sendMessage(senderId: String, receiverId: String, content: String) {
    withContext(Dispatchers.IO) {
        val url = "jdbc:mysql://192.168.219.101/twitter?useSSL=false"
        val user = "root"
        val passwd = "1234"

        try {
            val connection: Connection = DriverManager.getConnection(url, user, passwd)
            val preparedStatement = connection.prepareStatement(
                """
                INSERT INTO direct_message (content, timestamp, is_read, sender_user_id, receiver_user_id)
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
    val url = "jdbc:mysql://192.168.219.101/twitter?useSSL=false"
    val user = "root"
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
        val url = "jdbc:mysql://192.168.219.101/twitter?useSSL=false"
        val user = "root"
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
        val url = "jdbc:mysql://192.168.219.101/twitter?useSSL=false"
        val user = "root"
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
    val url = "jdbc:mysql://192.168.219.101/twitter?useSSL=false"
    val user = "root"
    val passwd = "1234"
    var isFollowing = false

    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(
            "SELECT * FROM following WHERE following_f_id='$loggedInUserId' AND user_user_id='$userId'"
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
    val url = "jdbc:mysql://192.168.219.101/twitter?useSSL=false"
    val user = "root"
    val passwd = "1234"

    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        
        // following 테이블에 추가 (followerId가 followingId를 팔로우)
        val followingStatement = connection.prepareStatement(
            "INSERT INTO following (following_f_id, user_user_id) VALUES (?, ?)"
        )
        followingStatement.setString(1, followerId)
        followingStatement.setString(2, followingId)
        followingStatement.executeUpdate()
        
        // follower 테이블에 추가 (followingId의 팔로워로 followerId 추가)
        val followerStatement = connection.prepareStatement(
            "INSERT INTO follower (follower_f_id, user_user_id) VALUES (?, ?)"
        )
        followerStatement.setString(1, followingId)  // 팔로우 당하는 사람
        followerStatement.setString(2, followerId)   // 팔로우 하는 사람
        followerStatement.executeUpdate()

        followingStatement.close()
        followerStatement.close()
        connection.close()
    } catch (e: Exception) {
        // 에러 로그를 더 자세히 출력
        println("Follow Error: ${e.message}")
        e.printStackTrace()
    }
}

suspend fun unfollowUser(followerId: String, followingId: String) = withContext(Dispatchers.IO) {
    val url = "jdbc:mysql://192.168.219.101/twitter?useSSL=false"
    val user = "root"
    val passwd = "1234"

    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        
        // following 테이블에서 삭제
        val followingStatement = connection.prepareStatement(
            "DELETE FROM following WHERE following_f_id = ? AND user_user_id = ?"
        )
        followingStatement.setString(1, followerId)
        followingStatement.setString(2, followingId)
        followingStatement.executeUpdate()
        
        // follower 테이블에서 삭제
        val followerStatement = connection.prepareStatement(
            "DELETE FROM follower WHERE follower_f_id = ? AND user_user_id = ?"
        )
        followerStatement.setString(1, followingId)  // 팔로우 당하는 사람
        followerStatement.setString(2, followerId)   // 팔로우 하는 사람
        followerStatement.executeUpdate()

        followingStatement.close()
        followerStatement.close()
        connection.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

suspend fun fetchPostsForUser(userId: String): List<Post> = withContext(Dispatchers.IO) {
    val posts = mutableListOf<Post>()
    val url = "jdbc:mysql://192.168.219.101/twitter?useSSL=false"
    val user = "root"
    val passwd = "1234"

    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(
            """
            SELECT p.*, u.nickname 
            FROM posts p
            JOIN user u ON p.user_user_id = u.user_id
            WHERE p.user_user_id = '$userId'
            ORDER BY p.upload_timestamp DESC
            """
        )

        while (resultSet.next()) {
            val post = Post(
                postId = resultSet.getString("post_id"),
                content = resultSet.getString("content"),
                numOfLikes = resultSet.getInt("num_of_likes"),
                userId = resultSet.getString("user_user_id"),
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
    val url = "jdbc:mysql://192.168.219.101/twitter?useSSL=false"
    val user = "root"
    val passwd = "1234"

    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(
            // following_f_id가 팔로우하는 사람(나)이고, user_user_id가 팔로우 당하는 사람
            "SELECT COUNT(*) as count FROM following WHERE following_f_id='$userId'"
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
    val url = "jdbc:mysql://192.168.219.101/twitter?useSSL=false"
    val user = "root"
    val passwd = "1234"

    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(
            // user_user_id가 팔로우 당하는 사람(나)이고, following_f_id가 팔로우하는 사람
            "SELECT COUNT(*) as count FROM following WHERE user_user_id='$userId'"
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
    val url = "jdbc:mysql://192.168.219.101/twitter?useSSL=false"
    val user = "root"
    val passwd = "1234"

    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(
            """
            SELECT u.user_id, u.nickname
            FROM following f
            JOIN user u ON f.user_user_id = u.user_id
            WHERE f.following_f_id = '$userId'
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
    val url = "jdbc:mysql://192.168.219.101/twitter?useSSL=false"
    val user = "root"
    val passwd = "1234"

    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(
            """
            SELECT u.user_id, u.nickname
            FROM following f
            JOIN user u ON f.following_f_id = u.user_id
            WHERE f.user_user_id = '$userId'
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
    val url = "jdbc:mysql://192.168.219.101/twitter?useSSL=false"
    val user = "root"
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
    val url = "jdbc:mysql://192.168.219.101/twitter?useSSL=false"
    val user = "root"
    val passwd = "1234"
    try {
        val connection: Connection = DriverManager.getConnection(url, user, passwd)
        val statement = connection.createStatement()
        val resultSet: ResultSet = statement.executeQuery(
            """
            SELECT p.*, u.nickname
            FROM posts p
            JOIN user u ON p.user_user_id = u.user_id
            WHERE p.content LIKE '%$searchText%'
            ORDER BY p.upload_timestamp DESC
            """
        )

        while (resultSet.next()) {
            val post = Post(
                postId = resultSet.getString("post_id"),
                content = resultSet.getString("content"),
                numOfLikes = resultSet.getInt("num_of_likes"),
                userId = resultSet.getString("user_user_id"),
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
data class DirectMessage(
    val messageId: String,
    val content: String,
    val timestamp: Timestamp,
    val nickname: String,
    val userId: String,
    val isRead: Int // isRead 속성 추가
)

data class Post(
    val postId: String,
    val userId: String,
    val content: String? = null,
    val numOfLikes: Int? = null,
    val tag: String? = null,
    val uploadTimestamp: Timestamp? = null,
    val nickname: String? = null
)
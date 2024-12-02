package com.gachon.twitter

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavHostController
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.launch
import com.gachon.twitter.R

@Composable
fun LoginScreen(navController: NavHostController, userViewModel: UserViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // 명확한 색상 값 설정
    val primaryColor = Color(0xFF1DA1F2)  // Twitter Blue
    val onPrimaryColor = Color.White
    val backgroundColor = Color.White
    val onSurfaceColor = Color.Gray

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)  // 배경 색상 변경
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = "앱 아이콘"
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("ID 입력", color = onSurfaceColor) },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = primaryColor,
                    unfocusedIndicatorColor = onSurfaceColor,
                    cursorColor = primaryColor
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("비밀번호 입력", color = onSurfaceColor) },
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    focusedIndicatorColor = primaryColor,
                    unfocusedIndicatorColor = onSurfaceColor,
                    cursorColor = primaryColor
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        val (success, message) = validateUser(username, password)
                        if (success) {
                            userViewModel.setLoggedInUserId(username)
                            navController.navigate("home")
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = primaryColor,
                    contentColor = onPrimaryColor
                )
            ) {
                Text("로그인")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = { navController.navigate("changepw") }) {
                Text("비밀번호 변경", color = primaryColor)
            }
            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = { navController.navigate("createuser") }) {
                Text("회원가입", color = primaryColor)
            }
        }
    }
}

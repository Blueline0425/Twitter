package com.gachon.twitter

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

@Composable
fun ChangePasswordScreen(navController: NavHostController) {
    // 명확한 색상 값 설정
    val primaryColor = Color(0xFF1DA1F2)  // Twitter Blue
    val onPrimaryColor = Color.White
    val backgroundColor = Color.White
    val buttonDisabledColor = Color.LightGray

    val userId = remember { mutableStateOf("") }
    val currentPassword = remember { mutableStateOf("") }
    val newPassword = remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val isButtonEnabled = userId.value.isNotBlank() && currentPassword.value.isNotBlank() && newPassword.value.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(backgroundColor)  // 배경 색상 변경
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "←",
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .padding(8.dp),
                fontSize = 24.sp  // 텍스트 크기 명확하게 설정
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = userId.value,
            onValueChange = { userId.value = it },
            label = { Text("아이디", color = primaryColor) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                focusedLabelColor = primaryColor, // 선택된 레이블 색상 설정
                focusedIndicatorColor = primaryColor,
                unfocusedIndicatorColor = Color.Gray,
                cursorColor = primaryColor,
                backgroundColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = currentPassword.value,
            onValueChange = { currentPassword.value = it },
            label = { Text("현재 비밀번호", color = primaryColor) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                focusedLabelColor = primaryColor, // 선택된 레이블 색상 설정
                focusedIndicatorColor = primaryColor,
                unfocusedIndicatorColor = Color.Gray,
                cursorColor = primaryColor,
                backgroundColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = newPassword.value,
            onValueChange = { newPassword.value = it },
            label = { Text("새 비밀번호", color = primaryColor) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                focusedLabelColor = primaryColor, // 선택된 레이블 색상 설정
                focusedIndicatorColor = primaryColor,
                unfocusedIndicatorColor = Color.Gray,
                cursorColor = primaryColor,
                backgroundColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    val (isValid, message) = validateUser(userId.value, currentPassword.value)
                    if (isValid) {
                        changePassword(userId.value, newPassword.value)
                        Toast.makeText(context, "비밀번호 변경 완료!", Toast.LENGTH_SHORT).show()
                        navController.navigate("login") // 로그인 화면으로 이동
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (isButtonEnabled) primaryColor else buttonDisabledColor
            ),
            enabled = isButtonEnabled
        ) {
            Text("비밀번호 변경", color = onPrimaryColor)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChangePasswordPreview() {
    ChangePasswordScreen(navController = rememberNavController())
}

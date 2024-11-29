package com.gachon.twitter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@Composable
fun ChangePasswordScreen(navController: NavHostController) {
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
                style = MaterialTheme.typography.h4
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = userId.value,
            onValueChange = { userId.value = it },
            label = { Text("아이디") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = currentPassword.value,
            onValueChange = { currentPassword.value = it },
            label = { Text("현재 비밀번호") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = newPassword.value,
            onValueChange = { newPassword.value = it },
            label = { Text("새 비밀번호") },
            modifier = Modifier.fillMaxWidth()
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
            colors = ButtonDefaults.buttonColors(backgroundColor = if (isButtonEnabled) Color(0xFF1DA1F2) else Color.LightGray),
            enabled = isButtonEnabled
        ) {
            Text("비밀번호 변경", color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChangePasswordPreview() {
    ChangePasswordScreen(navController = rememberNavController())
}

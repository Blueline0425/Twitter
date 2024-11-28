package com.gachon.twitter

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.TextField
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavHostController

//@Preview(showBackground = true)
@Composable
fun LoginScreen(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "APP 로고가 들어갈 곳", style = MaterialTheme.typography.h5)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("ID 입력") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호 입력") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("home") }) {
            Text("로그인")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { /* 비밀번호 찾기 로직 */ }) {
            Text("비밀번호 찾기")
        }
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { /* 회원가입 */ }) {
            Text("회원가입")
        }
    }
}

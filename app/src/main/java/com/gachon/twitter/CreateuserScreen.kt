package com.gachon.twitter

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@Composable
fun CreateUserUI(navController: NavHostController) {
    val nickname = remember { mutableStateOf("") }
    val userId = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val isButtonEnabled = nickname.value.isNotBlank() && userId.value.isNotBlank() && password.value.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "←",
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .padding(8.dp),
                style = MaterialTheme.typography.h4
            )
            Image(
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = null,
                modifier = Modifier.size(50.dp)



            )
            Spacer(modifier = Modifier.width(24.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "계정을 생성하세요",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(80.dp))

        TextField(
            value = nickname.value,
            onValueChange = { nickname.value = it },
            label = { Text("닉네임") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = userId.value,
            onValueChange = { userId.value = it },
            label = { Text("아이디") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("비밀번호") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    val isDuplicate = checkUserIdDuplicate(userId.value)
                    if (isDuplicate) {
                        Toast.makeText(context, "중복된 id", Toast.LENGTH_SHORT).show()
                    } else {
                        createUser(userId.value, password.value, nickname.value)
                        Toast.makeText(context, "생성완료!", Toast.LENGTH_SHORT).show()
                        navController.navigate("login")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = if (isButtonEnabled) Color.Gray else Color.LightGray),
            enabled = isButtonEnabled
        ) {
            Text("다음", color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateUserPreview() {
    CreateUserUI(navController = rememberNavController())
}



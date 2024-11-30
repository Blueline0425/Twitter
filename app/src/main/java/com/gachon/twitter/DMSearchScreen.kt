package com.gachon.twitter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DMSearchScreen(navController: NavHostController) {
    var searchText by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(emptyList<UserInfo>()) }
    
    LaunchedEffect(searchText) {
        if (searchText.startsWith("@") && searchText.length > 1) {
            val query = searchText.substring(1)
            searchResults = searchUsers(query)
        } else {
            searchResults = emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("새 메시지") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 검색창
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("@사용자 검색") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            )

            // 검색 결과
            LazyColumn {
                items(searchResults) { user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                navController.navigate("dmmsg/${user.userId}/${user.nickname}")
                            }
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = user.nickname,
                                style = MaterialTheme.typography.subtitle1
                            )
                            Text(
                                text = "@${user.userId}",
                                style = MaterialTheme.typography.body1,
                                color = Color.Gray
                            )
                        }
                    }
                    Divider()
                }
            }
        }
    }
}


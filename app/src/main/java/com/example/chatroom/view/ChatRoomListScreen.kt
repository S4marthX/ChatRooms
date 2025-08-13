package com.example.chatroom.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.chatroom.data.Room
import com.example.chatroom.screen.Screen
import com.example.chatroom.viewModel.AuthViewModel
import com.example.chatroom.viewModel.MessageViewModel
import com.example.chatroom.viewModel.RoomViewModel

@Composable
fun ChatRoomListScreen(
    roomViewModel: RoomViewModel = viewModel(),
    onJoinClicked: (Room) -> Unit,
    messageViewModel: MessageViewModel = viewModel(),
    authViewModel : AuthViewModel = viewModel(),
    navController: NavController
){
    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }

    val rooms by roomViewModel.rooms.collectAsState()
    val user by messageViewModel.currentUser.collectAsState()

    Column (
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ){
        Spacer(modifier = Modifier.height(24.dp))
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text("Chat Rooms", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = {
                authViewModel.signOut()
                navController.navigate(Screen.LoginScreen.route) {
                    // Clear the entire back stack
                    popUpTo(0) { inclusive = true }
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sign Out")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        user?.let { Text(it.email) }

        LazyColumn {
            items(rooms){
                RoomItem(it) { onJoinClicked(it) }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {showDialog = true},
            modifier = Modifier.fillMaxWidth()) {
            Text("Create Room")
        }
        Spacer(modifier = Modifier.height(24.dp))

        if(showDialog){
            AlertDialog(
                onDismissRequest = {showDialog = true},
                title = { Text("Create a new room") },
                text = {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {name = it},
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    )
                },
                confirmButton = {
                    Row (
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Button(
                            onClick = {showDialog = false}
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                if (name.isNotBlank()) {
                                    roomViewModel.createRoom(name)
                                    showDialog = false
                                }
                            }
                        ) {
                            Text("Add")
                        }
                    }
                }

            )
        }
    }
}

@Composable
fun RoomItem(room: Room , onJoinClicked: (Room)  -> Unit) {
    Row (
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(room.name, fontSize = 16.sp, fontWeight = FontWeight.Normal)

        OutlinedButton(
            onClick = {
                onJoinClicked(room)
            }
        ) {
            Text("Join")
        }
    }
}
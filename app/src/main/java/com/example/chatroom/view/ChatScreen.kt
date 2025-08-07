package com.example.chatroom.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chatroom.R
import com.example.chatroom.data.Message
import com.example.chatroom.data.formatTimestamp
import com.example.chatroom.viewModel.MessageViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(roomId : String,
               messageViewModel : MessageViewModel = viewModel()){
    val text = remember { mutableStateOf("") }
    val message by messageViewModel.messages.observeAsState(emptyList())
    val focusManager = LocalFocusManager.current
    messageViewModel.setRoomId(roomId)

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)

    ) {
        Spacer(modifier = Modifier.height(24.dp))
        LazyColumn (
            modifier = Modifier.weight(1f)
        ){
            items(message){message ->
                ChatItem(message = message.copy(
                    isSentByCurrentUser = message.senderId == messageViewModel.currentUser.value?.email
                ))
            }
        }
        Row (
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            OutlinedTextField(
                value = text.value,
                onValueChange = {
                    text.value = it
                },
                shape = RoundedCornerShape(32.dp),
                textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                modifier = Modifier.weight(1f).padding(8.dp),
            )
            IconButton(
                onClick = {
                    if(text.value.isNotEmpty())
                    {
                        messageViewModel.sendMessage(text.value.trim())
                        text.value = ""
                    }
                    messageViewModel.loadMessages()
                    focusManager.clearFocus()
                }
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatItem(message: Message) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = if (message.isSentByCurrentUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (message.isSentByCurrentUser) colorResource(id = R.color.purple_700) else Color.Gray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        ) {
            Text(
                text = message.text,
                color = Color.White,
                style = TextStyle(fontSize = 16.sp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = message.senderFirstName,
            style = TextStyle(
                fontSize = 12.sp,
                color = Color.Gray
            )
        )
        Text(
            text = formatTimestamp(message.timestamp), // Replace with actual timestamp logic
            style = TextStyle(
                fontSize = 12.sp,
                color = Color.Gray
            )
        )
    }
}

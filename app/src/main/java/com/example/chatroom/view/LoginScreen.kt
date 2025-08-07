package com.example.chatroom.view

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatroom.viewModel.AuthViewModel
import com.example.chatroom.data.Result

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onNavigateToSignup: ()-> Unit,
    onLogInSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context  = LocalContext.current
    val focusManager = LocalFocusManager.current

    val result by authViewModel.authResult.observeAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
        )
        Spacer(Modifier.height(32.dp))
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = { Text("Email") },
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = { Text("Password") },
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        Button(
            onClick = {
                authViewModel.login(email,password)
                when (result) {
                    is Result.Success ->{
                        Toast.makeText(context,"Login Success",Toast.LENGTH_SHORT).show()
                        onLogInSuccess()
                    }
                    is Result.Error ->{
                        Toast.makeText(context,"Error Login",Toast.LENGTH_SHORT).show()
                    }
                    else ->{
                        Toast.makeText(context,"Unknown Error",Toast.LENGTH_SHORT).show()
                    }
                }
                focusManager.clearFocus()
            },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("Login")
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Don't have account? Sign up",
            modifier = Modifier.clickable {
                onNavigateToSignup()
            })
    }
}
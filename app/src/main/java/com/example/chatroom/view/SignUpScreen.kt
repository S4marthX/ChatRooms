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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatroom.viewModel.AuthViewModel

@Composable
fun SignUpScreen(
    onNavigateToLogin : () -> Unit,
    authViewModel : AuthViewModel
){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "New User? SignUp",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
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
        OutlinedTextField(
            value = firstName,
            onValueChange = {
                firstName = it
            },
            label = { Text("First Name") },
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = lastName,
            onValueChange = {
                lastName = it
            },
            label = { Text("Last Name") },
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                authViewModel.signUp(email,password,firstName,lastName)
                focusManager.clearFocus()
                Toast.makeText(context,"Sign Up Successful", Toast.LENGTH_SHORT).show()
                email = ""
                password = ""
                firstName = ""
                lastName = ""

            },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("SignUp")
        }
        Spacer(Modifier.height(8.dp))
        Text(
            "Already have an account? Log in",
            modifier = Modifier.clickable {
                onNavigateToLogin()
            }
        )
    }
}
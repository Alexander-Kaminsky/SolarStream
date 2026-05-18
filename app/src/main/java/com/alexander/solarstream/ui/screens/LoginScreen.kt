package com.alexander.solarstream.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.alexander.solarstream.R

@Composable
fun LoginScreen(onLoginSuccess: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }

    // Manages moving from Email -> Password when you hit "Next"
    val focusManager = LocalFocusManager.current

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.solar_grid_anim))
    val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

    val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,7}$".toRegex()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text("Welcome to SunStream", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Text("Visualize Your Power. Share Your Grid.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = false
            },
            label = { Text("Email", color = Color.Gray) },
            singleLine = true, // FIX: Stops the box from expanding
            textStyle = LocalTextStyle.current.copy(color = Color.White), // FIX: Bright white text
            isError = emailError,
            supportingText = {
                if (emailError) {
                    Text("Please enter a valid email format.", color = MaterialTheme.colorScheme.error)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next // Changes "Enter" to "Next"
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) } // Moves to password
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4CAF50),
                unfocusedBorderColor = Color.DarkGray,
                cursorColor = Color(0xFF4CAF50),
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", color = Color.Gray) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true, // FIX: Stops the box from expanding
            textStyle = LocalTextStyle.current.copy(color = Color.White), // FIX: Bright white text
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done // Changes "Enter" to a checkmark
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus() // Hides keyboard
                    if (email.matches(emailPattern) && password.isNotBlank()) {
                        onLoginSuccess(email, password) // Pass both variables!
                    } else {
                        emailError = true
                    }
                }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF4CAF50),
                unfocusedBorderColor = Color.DarkGray,
                cursorColor = Color(0xFF4CAF50)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                focusManager.clearFocus()
                if (email.matches(emailPattern) && password.isNotBlank()) {
                    onLoginSuccess(email, password) // Pass both variables!
                } else {
                    emailError = true
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("ENTER DASHBOARD", color = Color.White)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
fun LoginScreenPreview() {
    LoginScreen(onLoginSuccess = { _, _ -> }) // Accept two ignored parameters
}
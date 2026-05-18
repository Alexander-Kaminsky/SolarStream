package com.alexander.solarstream.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alexander.solarstream.data.model.ChatMessage
import com.alexander.solarstream.viewmodel.ChatViewModel

// 1. THE STATEFUL WRAPPER (Talks to Firebase via ViewModel)
@Composable
fun ChatScreen(viewModel: ChatViewModel = viewModel()) {
    val messages by viewModel.messages.collectAsState()
    val currentUser = viewModel.getCurrentUser()

    ChatScreenContent(
        messages = messages,
        currentUser = currentUser,
        onSendMessage = { text -> viewModel.sendMessage(text) }
    )
}

// 2. THE STATELESS UI (Only draws data, perfect for Previews)
@Composable
fun ChatScreenContent(
    messages: List<ChatMessage>,
    currentUser: String,
    onSendMessage: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
    ) {
        Text(
            text = "MAKER CHAT",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray,
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(messages) { msg ->
                val isMe = msg.userPrefix == currentUser
                ChatBubble(msg, isMe)
            }
        }

        Surface(
            color = Color(0xFF161616),
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .padding(bottom = 72.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Discuss your rig...", color = Color.DarkGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1E1E1E),
                        unfocusedContainerColor = Color(0xFF1E1E1E),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                FloatingActionButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    },
                    containerColor = Color(0xFF4CAF50),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
    }
}

// 3. THE REUSABLE BUBBLE COMPONENT
@Composable
fun ChatBubble(message: ChatMessage, isMe: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(
                text = "@${message.userPrefix}",
                color = Color.Gray,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 4.dp, start = 4.dp, end = 4.dp)
            )

            Box(
                modifier = Modifier
                    .background(
                        color = if (isMe) Color(0xFF2E7D32) else Color(0xFF1E1E1E),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMe) 16.dp else 4.dp,
                            bottomEnd = if (isMe) 4.dp else 16.dp
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(text = message.text, color = Color.White, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

// 4. THE PREVIEW (Injects fake maker data)
@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
fun ChatScreenPreview() {
    val dummyMessages = listOf(
        ChatMessage(userPrefix = "Timon", text = "Hey, has anyone tested the CN3791 under heavy load?", timestamp = 1000),
        ChatMessage(userPrefix = "Alex", text = "Yeah, I'm running a 20W panel into it right now. Keeping the 18650s perfectly balanced.", timestamp = 2000),
        ChatMessage(userPrefix = "Pumba", text = "Nice build! Did you 3D print the enclosure?", timestamp = 3000),
        ChatMessage(userPrefix = "Alex", text = "Working on the BMCU mount for it today.", timestamp = 4000)
    )

    ChatScreenContent(
        messages = dummyMessages,
        currentUser = "Alex",
        onSendMessage = {}
    )
}
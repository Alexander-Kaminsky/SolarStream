package com.alexander.solarstream.viewmodel

import androidx.lifecycle.ViewModel
import com.alexander.solarstream.core.utils.SessionManager
import com.alexander.solarstream.data.model.ChatMessage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChatViewModel : ViewModel() {

    private val dbRef = FirebaseDatabase.getInstance().getReference("community_chat")
    private val session = SessionManager.getInstance()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    init {
        listenForMessages()
    }

    private fun listenForMessages() {
        // Realtime Database listener
        dbRef.orderByChild("timestamp").limitToLast(50)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatList = mutableListOf<ChatMessage>()
                    for (child in snapshot.children) {
                        val msg = child.getValue(ChatMessage::class.java)
                        if (msg != null) chatList.add(msg)
                    }
                    _messages.value = chatList
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val newMessage = ChatMessage(
            userPrefix = session.currentUserPrefix,
            text = text.trim(),
            timestamp = System.currentTimeMillis()
        )
        // Push generates a unique ID for the message
        dbRef.push().setValue(newMessage)
    }

    fun getCurrentUser(): String = session.currentUserPrefix
}
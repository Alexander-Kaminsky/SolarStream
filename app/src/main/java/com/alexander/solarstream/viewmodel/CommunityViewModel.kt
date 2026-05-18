package com.alexander.solarstream.viewmodel

import androidx.lifecycle.ViewModel
import com.alexander.solarstream.core.utils.SessionManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class BuildPost(
    val id: String = "",
    val userPrefix: String = "",
    val title: String = "",
    val specs: String = "",
    val likes: Int = 0,
    val isLikedByMe: Boolean = false, // Added to track UI state dynamically
    val timestamp: Long = 0L
)

class CommunityViewModel : ViewModel() {

    private val dbRef = FirebaseDatabase.getInstance().getReference("community_builds")
    private val session = SessionManager.getInstance()

    private val _posts = MutableStateFlow<List<BuildPost>>(emptyList())
    val posts: StateFlow<List<BuildPost>> = _posts.asStateFlow()

    init {
        // Listen to Firebase Realtime Database
        dbRef.orderByChild("timestamp").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val postList = mutableListOf<BuildPost>()
                val currentUser = session.currentUserPrefix

                for (child in snapshot.children) {
                    val id = child.key ?: continue
                    val userPrefix = child.child("userPrefix").getValue(String::class.java) ?: ""
                    val title = child.child("title").getValue(String::class.java) ?: ""
                    val specs = child.child("specs").getValue(String::class.java) ?: ""
                    val timestamp = child.child("timestamp").getValue(Long::class.java) ?: 0L

                    // PRO ARCHITECTURE: Parse the "liked_by" map
                    val likedBySnapshot = child.child("liked_by")
                    val likesCount = likedBySnapshot.childrenCount.toInt()
                    val isLikedByMe = likedBySnapshot.hasChild(currentUser)

                    postList.add(0, BuildPost(id, userPrefix, title, specs, likesCount, isLikedByMe, timestamp))
                }
                _posts.value = postList
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun addPost(title: String, specs: String) {
        val newRef = dbRef.push()
        val postMap = mapOf(
            "userPrefix" to session.currentUserPrefix,
            "title" to title,
            "specs" to specs,
            "timestamp" to System.currentTimeMillis()
            // We don't save 'likes' here anymore! It starts empty.
        )
        newRef.setValue(postMap)
    }

    fun deletePost(postId: String) {
        dbRef.child(postId).removeValue()
    }

    // NEW: Production-ready Like/Unlike logic
    fun toggleLike(postId: String, isCurrentlyLikedByMe: Boolean) {
        val currentUser = session.currentUserPrefix
        val likeRef = dbRef.child(postId).child("liked_by").child(currentUser)

        if (isCurrentlyLikedByMe) {
            // UNLIKE: Remove the user from the database node
            likeRef.removeValue()
        } else {
            // LIKE: Add the user to the database node
            likeRef.setValue(true)
        }
    }

    fun getCurrentUser() = session.currentUserPrefix
}
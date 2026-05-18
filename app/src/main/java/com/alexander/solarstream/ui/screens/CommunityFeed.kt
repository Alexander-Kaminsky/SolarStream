package com.alexander.solarstream.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alexander.solarstream.viewmodel.BuildPost
import com.alexander.solarstream.viewmodel.CommunityViewModel

// 1. THE STATEFUL WRAPPER (Talks to Firebase)
@Composable
fun CommunityFeed(viewModel: CommunityViewModel = viewModel()) {
    val posts by viewModel.posts.collectAsState()
    val currentUser = viewModel.getCurrentUser()

    CommunityFeedContent(
        posts = posts,
        currentUser = currentUser,
        onAddPost = { title, specs -> viewModel.addPost(title, specs) },
        onLikeClick = { postId, isLikedByMe -> viewModel.toggleLike(postId, isLikedByMe) },
        onDeleteClick = { postId -> viewModel.deletePost(postId) }
    )
}

// 2. THE STATELESS UI (Only knows how to draw, perfect for previews)
@Composable
fun CommunityFeedContent(
    posts: List<BuildPost>,
    currentUser: String,
    onAddPost: (String, String) -> Unit,
    onLikeClick: (String, Boolean) -> Unit, // FIX: Changed from Int to Boolean
    onDeleteClick: (String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFF0A0A0A),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Post")
                Spacer(modifier = Modifier.width(8.dp))
                Text("SHARE BUILD", fontWeight = FontWeight.Bold)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "COMMUNITY BUILDS",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(posts, key = { it.id }) { post ->
                    PostCard(
                        post = post,
                        currentUser = currentUser,
                        onLikeClick = { onLikeClick(post.id, post.isLikedByMe) },
                        onDeleteClick = { onDeleteClick(post.id) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddPostDialog(
            onDismiss = { showAddDialog = false },
            onPostSubmit = { title, specs ->
                onAddPost(title, specs)
                showAddDialog = false
            }
        )
    }
}

// 3. REUSABLE COMPONENTS
@Composable
fun AddPostDialog(onDismiss: () -> Unit, onPostSubmit: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var specs by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1E1E),
        title = { Text("Share Your Build", color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Project Title", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF4CAF50)
                    )
                )
                OutlinedTextField(
                    value = specs,
                    onValueChange = { specs = it },
                    label = { Text("Specs (e.g., 20W Panel, ESP32)", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF4CAF50)
                    ),
                    modifier = Modifier.height(100.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank()) onPostSubmit(title, specs) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("POST")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = Color.Gray)
            }
        }
    )
}

@Composable
fun PostCard(post: BuildPost, currentUser: String, onLikeClick: () -> Unit, onDeleteClick: () -> Unit) {
    val isMyPost = post.userPrefix == currentUser

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFF333333), shape = RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(post.userPrefix.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("@${post.userPrefix.lowercase()}", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.weight(1f))

                if (isMyPost) {
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFE53935))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(post.title, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(post.specs, color = Color.DarkGray, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFF333333), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onLikeClick() }.padding(4.dp)
                ) {
                    // FIX: UI strictly uses isLikedByMe to determine if the heart is red
                    Icon(
                        imageVector = if (post.isLikedByMe) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLikedByMe) Color(0xFFE53935) else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${post.likes} Likes", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

// 4. THE SAFE PREVIEW
@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
fun CommunityFeedPreview() {
    val dummyPosts = listOf(
        BuildPost("1", "Alex", "Portable 20W Rig", "Cells: 18650 x4 | Controller: CN3791", 42, true),
        BuildPost("2", "Timon", "Balcony Solar", "Cells: LiFePO4 12V | Controller: Victron", 0, false)
    )

    CommunityFeedContent(
        posts = dummyPosts,
        currentUser = "Alex",
        onAddPost = { _, _ -> },
        onLikeClick = { _, _ -> },
        onDeleteClick = { _ -> }
    )
}
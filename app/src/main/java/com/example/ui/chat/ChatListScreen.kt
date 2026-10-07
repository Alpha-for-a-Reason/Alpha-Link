package com.example.ui.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.data.repository.AlphaLinkRepository
import com.example.ui.components.*
import com.example.ui.theme.LocalCyberColors
import com.example.ui.theme.LocalReducedMotion

@Composable
fun ChatListScreen(
    repository: AlphaLinkRepository,
    onChatClick: (Chat) -> Unit,
    onStoryClick: (Story) -> Unit,
    onSettingsClick: () -> Unit,
    onStartCallClick: (String, String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val cyberColors = LocalCyberColors.current
    val reducedMotion = LocalReducedMotion.current
    val chats by repository.chats.collectAsState()
    val stories by repository.stories.collectAsState()
    val currentUser by repository.currentUser.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, DIRECT, GROUP, CHANNEL, UNREAD
    var showCreateDialog by remember { mutableStateOf(false) }

    // Filter logic
    val filteredChats = remember(chats, searchQuery, selectedFilter) {
        chats.filter { chat ->
            val matchesSearch = chat.title.contains(searchQuery, ignoreCase = true) ||
                    chat.description.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (selectedFilter) {
                "DIRECT" -> chat.type == ChatType.DIRECT
                "GROUP" -> chat.type == ChatType.GROUP
                "CHANNEL" -> chat.type == ChatType.CHANNEL
                "UNREAD" -> chat.unreadCount > 0
                else -> true
            }
            matchesSearch && matchesFilter
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(cyberColors.cyberBackground),
        containerColor = cyberColors.cyberBackground,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cyberColors.cyberGraphite.copy(alpha = 0.9f))
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Command Center Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(cyberColors.neonAccent.copy(alpha = 0.15f))
                                .border(1.dp, cyberColors.neonAccent, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(currentUser.avatarEmoji, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ALPHALINK",
                                    color = cyberColors.textPrimary,
                                    fontSize = 18.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                NeonBadge(text = "MESH ONLINE", color = cyberColors.neonAccent)
                            }
                            Text(
                                text = "@${currentUser.username} • 14ms ping",
                                color = cyberColors.textSecondary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onSettingsClick,
                            modifier = Modifier.testTag("settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Open Settings",
                                tint = cyberColors.neonAccent
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar in cyber glass
                CyberGlassSurface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search chats",
                            tint = cyberColors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(
                                    "Search encrypted chats, nodes, tags...",
                                    color = cyberColors.textSecondary.copy(alpha = 0.6f),
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = cyberColors.textPrimary,
                                unfocusedTextColor = cyberColors.textPrimary
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("chat_search_field")
                        )
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search",
                                    tint = cyberColors.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = cyberColors.neonAccent,
                contentColor = Color.Black,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("create_chat_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "New chat or channel",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Stories Carousel
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = "ENCRYPTED STORIES",
                        color = cyberColors.textSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(stories) { story ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { onStoryClick(story) }
                                    .testTag("story_item_${story.id}")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .border(
                                            2.dp,
                                            Brush.sweepGradient(
                                                listOf(
                                                    cyberColors.neonAccent,
                                                    cyberColors.secondaryAccent,
                                                    cyberColors.neonAccent
                                                )
                                            ),
                                            CircleShape
                                        )
                                        .padding(4.dp)
                                        .clip(CircleShape)
                                        .background(cyberColors.cyberGraphite),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(story.userAvatarEmoji, fontSize = 24.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = story.userName,
                                    color = cyberColors.textPrimary,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // Category filter pills
            item {
                val filters = listOf(
                    "ALL" to "All",
                    "DIRECT" to "Private",
                    "GROUP" to "Groups",
                    "CHANNEL" to "Channels",
                    "UNREAD" to "Unread"
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filters) { (key, label) ->
                        val isSelected = selectedFilter == key
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected) cyberColors.neonAccent else cyberColors.cyberGraphite
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) Color.Transparent else cyberColors.glassBorder,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedFilter = key }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                .testTag("filter_pill_$key"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.Black else cyberColors.textPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Chat list items
            items(filteredChats) { chat ->
                ChatItemCard(
                    chat = chat,
                    onClick = { onChatClick(chat) },
                    onCallClick = { isVideo ->
                        onStartCallClick(chat.title, chat.avatarEmoji, isVideo)
                    }
                )
            }
        }
    }

    if (showCreateDialog) {
        CreateChatDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { title, type ->
                showCreateDialog = false
                // Create chat implementation
            }
        )
    }
}

@Composable
fun ChatItemCard(
    chat: Chat,
    onClick: () -> Unit,
    onCallClick: (Boolean) -> Unit
) {
    val cyberColors = LocalCyberColors.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(cyberColors.cyberSurface)
            .border(0.8.dp, cyberColors.glassBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
            .testTag("chat_item_${chat.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(cyberColors.cyberGraphite)
                    .border(
                        1.2.dp,
                        if (chat.unreadCount > 0) cyberColors.neonAccent else cyberColors.glassBorder,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(chat.avatarEmoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = chat.title,
                            color = cyberColors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (chat.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified node",
                                tint = cyberColors.secondaryAccent,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // Timestamp
                    Text(
                        text = "14:28",
                        color = cyberColors.textSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Snippet
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (chat.isPinned) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = "Pinned",
                                tint = cyberColors.neonAccent,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = chat.lastMessage?.text ?: chat.description,
                            color = cyberColors.textSecondary,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Unread badge or delivery status
                    if (chat.unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(cyberColors.neonAccent)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = chat.unreadCount.toString(),
                                color = Color.Black,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Read",
                            tint = cyberColors.neonAccent,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CreateChatDialog(
    onDismiss: () -> Unit,
    onCreate: (String, ChatType) -> Unit
) {
    val cyberColors = LocalCyberColors.current
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ChatType.GROUP) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = cyberColors.cyberGraphite,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = "CREATE COMMUNICATION CLUSTER",
                color = cyberColors.textPrimary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Configure a secure peer-to-peer group or a high-capacity broadcast channel.",
                    color = cyberColors.textSecondary,
                    fontSize = 12.sp
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Cluster / Channel Title") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = cyberColors.neonAccent,
                        unfocusedBorderColor = cyberColors.glassBorder,
                        focusedTextColor = cyberColors.textPrimary,
                        unfocusedTextColor = cyberColors.textPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { selectedType = ChatType.GROUP },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedType == ChatType.GROUP) cyberColors.neonAccent else cyberColors.cyberSurface
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "Group",
                            color = if (selectedType == ChatType.GROUP) Color.Black else cyberColors.textPrimary,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = { selectedType = ChatType.CHANNEL },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedType == ChatType.CHANNEL) cyberColors.neonAccent else cyberColors.cyberSurface
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "Channel",
                            color = if (selectedType == ChatType.CHANNEL) Color.Black else cyberColors.textPrimary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            CyberButton(
                text = "Create",
                onClick = { if (name.isNotBlank()) onCreate(name, selectedType) },
                isPrimary = true,
                modifier = Modifier.width(100.dp)
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = cyberColors.textSecondary)
            }
        }
    )
}

package com.example.ui.chat

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.data.repository.AlphaLinkRepository
import com.example.ui.components.*
import com.example.ui.theme.LocalCyberColors
import com.example.ui.theme.LocalReducedMotion
import com.example.ui.theme.MutedEmerald
import com.example.ui.theme.NeonGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    chat: Chat,
    repository: AlphaLinkRepository,
    onBack: () -> Unit,
    onStartCall: (String, String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val cyberColors = LocalCyberColors.current
    val reducedMotion = LocalReducedMotion.current
    val messagesMap by repository.messages.collectAsState()
    val chatMessages = messagesMap[chat.id].orEmpty()
    val currentUser by repository.currentUser.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var replyingTo by remember { mutableStateOf<Message?>(null) }
    var showAttachmentSheet by remember { mutableStateOf(false) }
    var showPollDialog by remember { mutableStateOf(false) }
    var isRecordingVoice by remember { mutableStateOf(false) }
    var recordingDuration by remember { mutableIntStateOf(0) }
    var selectedReactionMessage by remember { mutableStateOf<Message?>(null) }
    var showSelfDestructMenu by remember { mutableStateOf(false) }
    var activeSelfDestructSeconds by remember { mutableStateOf<Int?>(null) }

    val listState = rememberLazyListState()

    // Recording duration timer
    LaunchedEffect(isRecordingVoice) {
        if (isRecordingVoice) {
            recordingDuration = 0
            while (isRecordingVoice) {
                kotlinx.coroutines.delay(1000)
                recordingDuration++
            }
        }
    }

    // Is this a group chat? (Prompt requires: True night-black background, Neon green primary text, Muted emerald secondary text)
    val isGroupChat = chat.type == ChatType.GROUP
    val chatBackgroundColor = if (isGroupChat) Color.Black else cyberColors.cyberBackground
    val primaryMessageColor = if (isGroupChat) NeonGreenPrimary else cyberColors.textPrimary
    val secondaryMessageColor = if (isGroupChat) MutedEmerald else cyberColors.textSecondary

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(chatBackgroundColor),
        containerColor = chatBackgroundColor,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cyberColors.cyberGraphite.copy(alpha = 0.95f))
                    .statusBarsPadding()
                    .border(
                        0.8.dp,
                        cyberColors.glassBorder.copy(alpha = 0.4f),
                        RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("chat_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = cyberColors.neonAccent
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(cyberColors.cyberSurface)
                                .border(1.dp, cyberColors.glassBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(chat.avatarEmoji, fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = chat.title,
                                    color = cyberColors.textPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                if (isGroupChat) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    NeonBadge(text = "GROUP CLUSTER", color = cyberColors.neonAccent)
                                } else if (chat.type == ChatType.CHANNEL) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    NeonBadge(text = "BROADCAST", color = cyberColors.secondaryAccent)
                                }
                            }
                            Text(
                                text = if (isGroupChat) "${chat.members.size} members • Encrypted Mesh"
                                else if (chat.type == ChatType.CHANNEL) "${chat.subscriberCount} subscribers"
                                else "Online • End-to-end Ratchet",
                                color = secondaryMessageColor,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Call & Options Action
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (chat.type != ChatType.CHANNEL) {
                            IconButton(
                                onClick = { onStartCall(chat.title, chat.avatarEmoji, false) },
                                modifier = Modifier.testTag("audio_call_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Voice Call",
                                    tint = cyberColors.neonAccent
                                )
                            }
                            IconButton(
                                onClick = { onStartCall(chat.title, chat.avatarEmoji, true) },
                                modifier = Modifier.testTag("video_call_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = "Video Call",
                                    tint = cyberColors.secondaryAccent
                                )
                            }
                        }
                    }
                }

                // Pinned messages bar
                val pinnedMessage = chatMessages.findLast { it.isPinned }
                if (pinnedMessage != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(cyberColors.cyberSurface)
                            .border(0.5.dp, cyberColors.neonAccent.copy(alpha = 0.3f))
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned Announcement",
                            tint = cyberColors.neonAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "PINNED ANNOUNCEMENT (${pinnedMessage.senderName})",
                                color = cyberColors.neonAccent,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = pinnedMessage.text,
                                color = cyberColors.textPrimary,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cyberColors.cyberGraphite.copy(alpha = 0.95f))
                    .navigationBarsPadding()
                    .padding(8.dp)
            ) {
                // Reply banner if active
                if (replyingTo != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(cyberColors.cyberSurface)
                            .border(1.dp, cyberColors.neonAccent.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Reply,
                            contentDescription = "Replying",
                            tint = cyberColors.neonAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Replying to ${replyingTo?.senderName}",
                                color = cyberColors.neonAccent,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = replyingTo?.text.orEmpty(),
                                color = cyberColors.textSecondary,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                        IconButton(onClick = { replyingTo = null }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel reply",
                                tint = cyberColors.textSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Input Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Attachment button
                    IconButton(
                        onClick = { showAttachmentSheet = true },
                        modifier = Modifier.testTag("attachment_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "Attach file or poll",
                            tint = cyberColors.neonAccent
                        )
                    }

                    // Self-destruct timer pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (activeSelfDestructSeconds != null) cyberColors.alertRed.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable {
                                activeSelfDestructSeconds = when (activeSelfDestructSeconds) {
                                    null -> 10
                                    10 -> 60
                                    60 -> 3600
                                    else -> null
                                }
                            }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Self destruct timer",
                            tint = if (activeSelfDestructSeconds != null) cyberColors.alertRed else cyberColors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Text Field Container
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(cyberColors.cyberSurface)
                            .border(1.dp, cyberColors.glassBorder, RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        if (isRecordingVoice) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(cyberColors.alertRed)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Recording: ${recordingDuration}s",
                                        color = cyberColors.alertRed,
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Text(
                                    text = "Release to send",
                                    color = cyberColors.textSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        } else {
                            BasicTextField(
                                value = inputText,
                                onValueChange = { inputText = it },
                                textStyle = LocalTextStyle.current.copy(
                                    color = cyberColors.textPrimary,
                                    fontSize = 14.sp
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("chat_input_field"),
                                decorationBox = { innerTextField ->
                                    if (inputText.isEmpty()) {
                                        Text(
                                            text = if (isGroupChat) "Message AlphaLink cluster..." else "Encrypted message...",
                                            color = secondaryMessageColor.copy(alpha = 0.5f),
                                            fontSize = 14.sp
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Voice / Send Action
                    if (inputText.isNotBlank()) {
                        IconButton(
                            onClick = {
                                repository.sendMessage(
                                    chatId = chat.id,
                                    text = inputText.trim(),
                                    replyTo = replyingTo,
                                    selfDestructSeconds = activeSelfDestructSeconds
                                )
                                inputText = ""
                                replyingTo = null
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(cyberColors.neonAccent)
                                .testTag("send_message_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send message",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        // Voice message hold/click
                        IconButton(
                            onClick = {
                                if (isRecordingVoice) {
                                    // Send voice note with simulated waveform
                                    isRecordingVoice = false
                                    repository.sendMessage(
                                        chatId = chat.id,
                                        text = "Voice message (${recordingDuration}s)",
                                        voiceWaveform = listOf(0.2f, 0.6f, 0.9f, 0.4f, 0.8f, 0.3f, 0.7f, 0.5f)
                                    )
                                } else {
                                    isRecordingVoice = true
                                }
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isRecordingVoice) cyberColors.alertRed else cyberColors.cyberSurface)
                                .testTag("voice_message_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Record voice note",
                                tint = if (isRecordingVoice) Color.White else cyberColors.neonAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(chatMessages) { message ->
                    val isMine = message.senderId == currentUser.id
                    ChatMessageBubble(
                        message = message,
                        isMine = isMine,
                        isGroup = isGroupChat,
                        primaryColor = primaryMessageColor,
                        secondaryColor = secondaryMessageColor,
                        onReply = { replyingTo = message },
                        onReact = { emoji -> repository.toggleReaction(chat.id, message.id, emoji) },
                        onPin = { repository.pinMessage(chat.id, message.id) },
                        onDelete = { repository.deleteMessage(chat.id, message.id) },
                        onVote = { optIdx -> repository.votePoll(chat.id, message.id, optIdx) }
                    )
                }
            }
        }
    }

    // Attachment Modal Bottom Sheet
    if (showAttachmentSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAttachmentSheet = false },
            containerColor = cyberColors.cyberGraphite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "ATTACH ENCRYPTED PAYLOAD",
                    color = cyberColors.textPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                // 6GB Multipart file sharing showcase
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(cyberColors.cyberSurface)
                        .border(1.dp, cyberColors.neonAccent.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable {
                            showAttachmentSheet = false
                            // Send simulated 6GB large file with chunking
                            val attachment = MediaAttachment(
                                type = MediaType.LARGE_FILE_6GB,
                                fileName = "cyber_mesh_telemetry_dump.bin",
                                fileSizeFormatted = "5.82 GB",
                                fileSizeBytes = 6250000000L,
                                uploadProgress = 1.0f,
                                isUploading = false
                            )
                            repository.sendMessage(
                                chatId = chat.id,
                                text = "Transferred 5.82 GB raw audit image over parallel peer streams.",
                                mediaAttachment = attachment
                            )
                        }
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = "6GB Multipart File Upload",
                            tint = cyberColors.neonAccent,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Send Large File (Up to 6 GB)",
                                    color = cyberColors.textPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                NeonBadge(text = "UNRESTRICTED", color = cyberColors.neonAccent)
                            }
                            Text(
                                text = "Resumable multipart chunked transfer with SHA-256 verification.",
                                color = cyberColors.textSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Poll / Quiz Option
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(cyberColors.cyberSurface)
                        .border(1.dp, cyberColors.glassBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            showAttachmentSheet = false
                            showPollDialog = true
                        }
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Poll,
                            contentDescription = "Create Poll or Quiz",
                            tint = cyberColors.secondaryAccent,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Create Poll or Quiz",
                                color = cyberColors.textPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Anonymous or public multi-option consensus voting.",
                                color = cyberColors.textSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Document & Media
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CyberButton(
                        text = "Document",
                        onClick = {
                            showAttachmentSheet = false
                            repository.sendMessage(
                                chatId = chat.id,
                                text = "Attached: security_architecture_v2.pdf",
                                mediaAttachment = MediaAttachment(
                                    type = MediaType.DOCUMENT,
                                    fileName = "security_architecture_v2.pdf",
                                    fileSizeFormatted = "4.2 MB"
                                )
                            )
                        },
                        isPrimary = false,
                        modifier = Modifier.weight(1f)
                    )
                    CyberButton(
                        text = "Media / Photo",
                        onClick = {
                            showAttachmentSheet = false
                            repository.sendMessage(
                                chatId = chat.id,
                                text = "Attached encrypted photo payload (AES-256)",
                                mediaAttachment = MediaAttachment(
                                    type = MediaType.IMAGE,
                                    fileName = "node_cluster_diagram.png",
                                    fileSizeFormatted = "1.8 MB"
                                )
                            )
                        },
                        isPrimary = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    // Poll Creator Dialog
    if (showPollDialog) {
        CreatePollDialog(
            onDismiss = { showPollDialog = false },
            onCreatePoll = { question, options ->
                showPollDialog = false
                val poll = PollData(
                    question = question,
                    options = options.mapIndexed { idx, opt -> PollOption(idx + 1, opt, 0) }
                )
                repository.sendMessage(
                    chatId = chat.id,
                    text = "Poll: $question",
                    mediaAttachment = null
                )
            }
        )
    }
}

@Composable
fun ChatMessageBubble(
    message: Message,
    isMine: Boolean,
    isGroup: Boolean,
    primaryColor: Color,
    secondaryColor: Color,
    onReply: () -> Unit,
    onReact: (String) -> Unit,
    onPin: () -> Unit,
    onDelete: () -> Unit,
    onVote: (Int) -> Unit
) {
    val cyberColors = LocalCyberColors.current
    var showMenu by remember { mutableStateOf(false) }

    val alignment = if (isMine) Alignment.End else Alignment.Start
    val bubbleBg = if (isMine) {
        cyberColors.bubbleSent
    } else {
        cyberColors.bubbleReceived
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isMine) 16.dp else 2.dp,
                        bottomEnd = if (isMine) 2.dp else 16.dp
                    )
                )
                .background(bubbleBg)
                .border(
                    0.8.dp,
                    if (isMine) cyberColors.neonAccent.copy(alpha = 0.5f) else cyberColors.glassBorder,
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isMine) 16.dp else 2.dp,
                        bottomEnd = if (isMine) 2.dp else 16.dp
                    )
                )
                .clickable { showMenu = true }
                .padding(12.dp)
        ) {
            Column {
                // Sender Name in group
                if (!isMine && isGroup) {
                    Text(
                        text = message.senderName,
                        color = cyberColors.neonAccent,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                // Reply preview
                if (message.replyToText != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.3f))
                            .border(0.5.dp, cyberColors.neonAccent.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(6.dp)
                    ) {
                        Text(
                            text = "${message.replyToSenderName}: ${message.replyToText}",
                            color = secondaryColor,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Text body
                Text(
                    text = message.text,
                    color = primaryColor,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                // Voice Waveform if present
                if (message.voiceWaveform != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    AudioWaveformBar(
                        amplitudes = message.voiceWaveform,
                        isPlaying = true,
                        barColor = if (isMine) cyberColors.neonAccent else cyberColors.secondaryAccent
                    )
                }

                // Media Attachment if present (e.g. 6GB file)
                if (message.mediaAttachment != null) {
                    val att = message.mediaAttachment
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.4f))
                            .border(0.8.dp, cyberColors.neonAccent.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (att.type == MediaType.LARGE_FILE_6GB) Icons.Default.Inventory2 else Icons.Default.InsertDriveFile,
                                contentDescription = "Attachment",
                                tint = cyberColors.neonAccent,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = att.fileName,
                                    color = cyberColors.textPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${att.fileSizeFormatted} • Verified Checksum",
                                    color = cyberColors.neonAccent,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                // Poll if present
                if (message.pollData != null) {
                    val poll = message.pollData
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = poll.question,
                            color = primaryColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        poll.options.forEachIndexed { idx, opt ->
                            val isSelected = poll.userVotedIndex == idx
                            val totalVotes = maxOf(1, poll.totalVotes)
                            val pct = (opt.voteCount.toFloat() / totalVotes * 100).toInt()

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) cyberColors.neonAccent.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.3f))
                                    .border(0.8.dp, if (isSelected) cyberColors.neonAccent else cyberColors.glassBorder, RoundedCornerShape(6.dp))
                                    .clickable { onVote(idx) }
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(opt.text, color = primaryColor, fontSize = 12.sp)
                                    Text("$pct% (${opt.voteCount})", color = secondaryColor, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Footer: Timestamp, self-destruct indicator, status
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (message.selfDestructSeconds != null) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Self destructs in ${message.selfDestructSeconds}s",
                            tint = cyberColors.alertRed,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                    Text(
                        text = "14:32",
                        color = secondaryColor.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    if (isMine) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = when (message.status) {
                                MessageStatus.READ -> Icons.Default.DoneAll
                                MessageStatus.DELIVERED -> Icons.Default.Done
                                MessageStatus.SENT -> Icons.Default.Check
                                MessageStatus.SENDING -> Icons.Default.Schedule
                                MessageStatus.FAILED -> Icons.Default.ErrorOutline
                            },
                            contentDescription = message.status.name,
                            tint = if (message.status == MessageStatus.READ) cyberColors.neonAccent else secondaryColor,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            // Quick actions drop-down
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Reply") },
                    onClick = {
                        showMenu = false
                        onReply()
                    },
                    leadingIcon = { Icon(Icons.Default.Reply, null) }
                )
                DropdownMenuItem(
                    text = { Text(if (message.isPinned) "Unpin" else "Pin") },
                    onClick = {
                        showMenu = false
                        onPin()
                    },
                    leadingIcon = { Icon(Icons.Default.PushPin, null) }
                )
                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = {
                        showMenu = false
                        onDelete()
                    },
                    leadingIcon = { Icon(Icons.Default.Delete, null) }
                )
            }
        }

        // Reactions display row
        if (message.reactions.isNotEmpty()) {
            Row(
                modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                message.reactions.forEach { (emoji, count) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(cyberColors.cyberSurface)
                            .border(0.8.dp, cyberColors.glassBorder, RoundedCornerShape(12.dp))
                            .clickable { onReact(emoji) }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$emoji $count",
                            color = primaryColor,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CreatePollDialog(
    onDismiss: () -> Unit,
    onCreatePoll: (String, List<String>) -> Unit
) {
    val cyberColors = LocalCyberColors.current
    var question by remember { mutableStateOf("") }
    var opt1 by remember { mutableStateOf("") }
    var opt2 by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = cyberColors.cyberGraphite,
        title = { Text("NEW CONSENSUS POLL", color = cyberColors.textPrimary, fontFamily = FontFamily.Monospace) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text("Question") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = opt1,
                    onValueChange = { opt1 = it },
                    label = { Text("Option 1") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = opt2,
                    onValueChange = { opt2 = it },
                    label = { Text("Option 2") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (question.isNotBlank() && opt1.isNotBlank() && opt2.isNotBlank()) {
                        onCreatePoll(question, listOf(opt1, opt2))
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = cyberColors.neonAccent)
            ) {
                Text("Launch Poll", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = cyberColors.textSecondary)
            }
        }
    )
}

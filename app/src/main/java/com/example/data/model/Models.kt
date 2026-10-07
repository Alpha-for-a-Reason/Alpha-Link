package com.example.data.model

import java.util.UUID

enum class UserPresence(val label: String) {
    ONLINE("Online"),
    AWAY("Away"),
    GHOST("Ghost Mode (Encrypted)"),
    OFFLINE("Offline")
}

data class User(
    val id: String,
    val username: String,
    val displayName: String,
    val phoneNumber: String = "",
    val email: String = "",
    val bio: String = "Operating on AlphaLink Mesh v2.4",
    val presence: UserPresence = UserPresence.ONLINE,
    val lastSeenTimestamp: Long = System.currentTimeMillis(),
    val avatarEmoji: String = "🛡️",
    val publicKeyFingerprint: String = "SHA256:7B8F9A23E0C49D1A"
)

enum class MessageStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ,
    FAILED
}

enum class MediaType {
    NONE,
    IMAGE,
    VIDEO,
    VOICE_NOTE,
    DOCUMENT,
    LARGE_FILE_6GB
}

data class MediaAttachment(
    val id: String = UUID.randomUUID().toString(),
    val type: MediaType = MediaType.NONE,
    val fileName: String = "",
    val fileSizeFormatted: String = "",
    val fileSizeBytes: Long = 0L,
    val uploadProgress: Float = 1.0f,
    val isUploading: Boolean = false,
    val uriOrUrl: String = "",
    val durationSeconds: Int = 0
)

data class PollOption(
    val id: Int,
    val text: String,
    val voteCount: Int = 0
)

data class PollData(
    val question: String,
    val options: List<PollOption>,
    val isQuiz: Boolean = false,
    val correctOptionIndex: Int = -1,
    val userVotedIndex: Int? = null,
    val isAnonymous: Boolean = true
) {
    val totalVotes: Int get() = options.sumOf { it.voteCount }
}

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.READ,
    val replyToMessageId: String? = null,
    val replyToText: String? = null,
    val replyToSenderName: String? = null,
    val reactions: Map<String, Int> = emptyMap(), // e.g. "⚡" to 3, "🔥" to 1
    val userReaction: String? = null,
    val mediaAttachment: MediaAttachment? = null,
    val isEdited: Boolean = false,
    val isForwarded: Boolean = false,
    val forwardedFrom: String? = null,
    val isScheduled: Boolean = false,
    val scheduledTime: Long? = null,
    val isPinned: Boolean = false,
    val selfDestructSeconds: Int? = null, // e.g. 10s, 60s
    val voiceWaveform: List<Float>? = null, // normalized amplitudes 0.0f..1.0f
    val pollData: PollData? = null
)

enum class ChatType {
    DIRECT,
    GROUP,
    CHANNEL
}

enum class MemberRole {
    OWNER,
    ADMINISTRATOR,
    MODERATOR,
    MEMBER
}

data class ChatMember(
    val user: User,
    val role: MemberRole = MemberRole.MEMBER
)

data class Topic(
    val id: String,
    val title: String,
    val iconEmoji: String,
    val messageCount: Int
)

data class Chat(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val type: ChatType = ChatType.DIRECT,
    val avatarEmoji: String = "💬",
    val description: String = "",
    val isVerified: Boolean = false,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isMuted: Boolean = false,
    val unreadCount: Int = 0,
    val lastMessage: Message? = null,
    val draft: String = "",
    val members: List<ChatMember> = emptyList(),
    val subscriberCount: Int = 0,
    val slowModeSeconds: Int = 0, // 0 = off, 10, 30, 60
    val isForumMode: Boolean = false,
    val topics: List<Topic> = emptyList(),
    val channelViewCount: Int = 0,
    val channelShareCount: Int = 0,
    val pinnedMessage: Message? = null,
    val selfDestructTimerSeconds: Int = 0 // 0 = disabled
)

data class Story(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val userName: String,
    val userAvatarEmoji: String,
    val title: String,
    val mediaDescription: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isViewed: Boolean = false,
    val viewsCount: Int = 142
)

data class ActiveSession(
    val id: String = UUID.randomUUID().toString(),
    val deviceName: String,
    val clientType: String,
    val ipAddress: String,
    val location: String,
    val isCurrent: Boolean = false,
    val lastActive: String = "Just now"
)

data class ProxyConfig(
    val enabled: Boolean = false,
    val host: String = "127.0.0.1",
    val port: Int = 9050,
    val secret: String = "alphalink-quantum-psk-991"
)

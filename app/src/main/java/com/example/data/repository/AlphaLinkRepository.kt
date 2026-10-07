package com.example.data.repository

import android.content.Context
import com.example.data.local.SecurityPreferences
import com.example.data.model.*
import com.example.ui.theme.CyberThemeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class AlphaLinkRepository(context: Context? = null) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var securityPreferences: SecurityPreferences? = null

    // Current User
    private val _currentUser = MutableStateFlow(
        User(
            id = "user_me",
            username = "cipher_root",
            displayName = "Alex Vance",
            phoneNumber = "+1 555-0199",
            email = "alex.vance@alphalink.network",
            bio = "Quantum mesh operator | AlphaLink Security Team",
            presence = UserPresence.ONLINE,
            avatarEmoji = "⚡",
            publicKeyFingerprint = "SHA256:4C8B1E99F200A417"
        )
    )
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    // Auth State
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _authPhone = MutableStateFlow("")
    val authPhone: StateFlow<String> = _authPhone.asStateFlow()

    private val _authCountryCode = MutableStateFlow("+1")
    val authCountryCode: StateFlow<String> = _authCountryCode.asStateFlow()

    private val _otpCode = MutableStateFlow("")
    val otpCode: StateFlow<String> = _otpCode.asStateFlow()

    private val _resendTimer = MutableStateFlow(60)
    val resendTimer: StateFlow<Int> = _resendTimer.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _isSubmittingAuth = MutableStateFlow(false)
    val isSubmittingAuth: StateFlow<Boolean> = _isSubmittingAuth.asStateFlow()

    // Chats
    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats.asStateFlow()

    // Messages grouped by Chat ID
    private val _messages = MutableStateFlow<Map<String, List<Message>>>(emptyMap())
    val messages: StateFlow<Map<String, List<Message>>> = _messages.asStateFlow()

    // Stories
    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories.asStateFlow()

    // Active Sessions
    private val _sessions = MutableStateFlow<List<ActiveSession>>(emptyList())
    val sessions: StateFlow<List<ActiveSession>> = _sessions.asStateFlow()

    // Settings
    private val _themeMode = MutableStateFlow(CyberThemeMode.CYBER_NEON)
    val themeMode: StateFlow<CyberThemeMode> = _themeMode.asStateFlow()

    private val _animationIntensity = MutableStateFlow(1.0f)
    val animationIntensity: StateFlow<Float> = _animationIntensity.asStateFlow()

    private val _reducedMotion = MutableStateFlow(false)
    val reducedMotion: StateFlow<Boolean> = _reducedMotion.asStateFlow()

    private val _biometricLockEnabled = MutableStateFlow(false)
    val biometricLockEnabled: StateFlow<Boolean> = _biometricLockEnabled.asStateFlow()

    private val _isAppLocked = MutableStateFlow(false)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    private val _twoStepVerificationEnabled = MutableStateFlow(true)
    val twoStepVerificationEnabled: StateFlow<Boolean> = _twoStepVerificationEnabled.asStateFlow()

    private val _serverUrl = MutableStateFlow("https://node.alphalink.network:8443")
    val serverUrl: StateFlow<String> = _serverUrl.asStateFlow()

    private val _proxyConfig = MutableStateFlow(ProxyConfig(enabled = false))
    val proxyConfig: StateFlow<ProxyConfig> = _proxyConfig.asStateFlow()

    // Call State
    data class CallState(
        val isInCall: Boolean = false,
        val targetName: String = "",
        val targetAvatar: String = "",
        val isVideo: Boolean = false,
        val isMuted: Boolean = false,
        val isVideoMuted: Boolean = false,
        val isSpeakerOn: Boolean = true,
        val noiseSuppression: Boolean = true,
        val echoCancellation: Boolean = true,
        val durationSeconds: Int = 0,
        val latencyMs: Int = 14,
        val bitrateKbps: Int = 128
    )

    private val _callState = MutableStateFlow(CallState())
    val callState: StateFlow<CallState> = _callState.asStateFlow()

    init {
        initInitialData()
        if (context != null) {
            initSecurityPreferences(context)
        }
    }

    fun initSecurityPreferences(ctx: Context) {
        if (securityPreferences == null) {
            val prefs = SecurityPreferences(ctx.applicationContext)
            securityPreferences = prefs
            scope.launch {
                prefs.requireBiometricUnlockFlow.collectLatest { enabled ->
                    _biometricLockEnabled.value = enabled
                }
            }
            scope.launch {
                prefs.twoStepVerificationFlow.collectLatest { enabled ->
                    _twoStepVerificationEnabled.value = enabled
                }
            }
        }
    }

    private fun initInitialData() {
        val kaelen = User(
            id = "user_kaelen",
            username = "kaelen_voss",
            displayName = "Kaelen Voss",
            bio = "Quantum Cryptographer | Lead Protocol Engineer",
            presence = UserPresence.ONLINE,
            avatarEmoji = "🧑‍💻",
            publicKeyFingerprint = "SHA256:1A2B3C4D5E6F7890"
        )
        val elena = User(
            id = "user_elena",
            username = "elena_mesh",
            displayName = "Elena Rostova",
            bio = "Decentralized Storage Arch",
            presence = UserPresence.ONLINE,
            avatarEmoji = "🛰️"
        )
        val sentinel = User(
            id = "user_sentinel",
            username = "sentinel_bot",
            displayName = "Sentinel Telemetry Bot",
            bio = "Automated Node Health & Anti-Spam Guard",
            presence = UserPresence.ONLINE,
            avatarEmoji = "🤖"
        )

        val directChat = Chat(
            id = "chat_direct_kaelen",
            title = "Kaelen Voss",
            type = ChatType.DIRECT,
            avatarEmoji = "🧑‍💻",
            description = "Encrypted Peer-to-Peer Signal",
            isPinned = true,
            unreadCount = 1,
            members = listOf(
                ChatMember(_currentUser.value, MemberRole.OWNER),
                ChatMember(kaelen, MemberRole.MEMBER)
            )
        )

        val groupChat = Chat(
            id = "chat_group_core",
            title = "AlphaLink Core Devs",
            type = ChatType.GROUP,
            avatarEmoji = "🛡️",
            description = "Protocol R&D, self-hosting orchestration and consensus discussion",
            isPinned = true,
            unreadCount = 3,
            members = listOf(
                ChatMember(_currentUser.value, MemberRole.OWNER),
                ChatMember(kaelen, MemberRole.ADMINISTRATOR),
                ChatMember(elena, MemberRole.MODERATOR)
            ),
            isForumMode = true,
            topics = listOf(
                Topic("t1", "Consensus & State Sync", "🔄", 42),
                Topic("t2", "WebRTC & Codecs", "🎙️", 19),
                Topic("t3", "Large 6GB Chunks", "📦", 28)
            ),
            slowModeSeconds = 0
        )

        val channelChat = Chat(
            id = "chat_channel_broadcast",
            title = "AlphaLink Announcements",
            type = ChatType.CHANNEL,
            avatarEmoji = "📢",
            description = "Official broadcast feed for releases, audit reports and mesh telemetry",
            subscriberCount = 284000,
            channelViewCount = 49200,
            channelShareCount = 8900
        )

        val savedChat = Chat(
            id = "chat_saved_messages",
            title = "Saved Messages",
            type = ChatType.DIRECT,
            avatarEmoji = "🔖",
            description = "Your encrypted cloud vault for files, notes and drafts",
            isPinned = false
        )

        val botChat = Chat(
            id = "chat_sentinel_bot",
            title = "Sentinel Bot",
            type = ChatType.DIRECT,
            avatarEmoji = "🤖",
            description = "Open API Bot framework assistant",
            isVerified = true
        )

        _chats.value = listOf(directChat, groupChat, channelChat, savedChat, botChat)

        // Seed messages
        val directMsgs = listOf(
            Message(
                id = "m1",
                chatId = directChat.id,
                senderId = kaelen.id,
                senderName = kaelen.displayName,
                text = "Alex, I just tested the new zero-trust ratchet protocol over SOCKS5 proxy.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 18,
                status = MessageStatus.READ
            ),
            Message(
                id = "m2",
                chatId = directChat.id,
                senderId = _currentUser.value.id,
                senderName = _currentUser.value.displayName,
                text = "Excellent. What is our round-trip latency on the encrypted handshake?",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 14,
                status = MessageStatus.READ
            ),
            Message(
                id = "m3",
                chatId = directChat.id,
                senderId = kaelen.id,
                senderName = kaelen.displayName,
                text = "Under 14ms across the distributed mesh. Audio packets use Opus with adaptive bitrate, and file transfer handles chunks up to 6GB with zero packet corruption! 🚀",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 5,
                status = MessageStatus.READ,
                reactions = mapOf("⚡" to 2, "🔥" to 1),
                voiceWaveform = listOf(0.2f, 0.5f, 0.8f, 0.6f, 0.9f, 0.4f, 0.7f, 0.3f, 0.8f, 0.6f, 0.3f)
            )
        )

        val groupMsgs = listOf(
            Message(
                id = "gm1",
                chatId = groupChat.id,
                senderId = kaelen.id,
                senderName = kaelen.displayName,
                text = "System integrity check complete: all nodes reporting 99.99% uptime.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 30,
                status = MessageStatus.READ,
                isPinned = true
            ),
            Message(
                id = "gm2",
                chatId = groupChat.id,
                senderId = elena.id,
                senderName = elena.displayName,
                text = "Should we conduct the security audit for the 6GB resumable upload pipeline now?",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 20,
                status = MessageStatus.READ
            ),
            Message(
                id = "gm3",
                chatId = groupChat.id,
                senderId = _currentUser.value.id,
                senderName = _currentUser.value.displayName,
                text = "Yes, launching the voting poll for testnet deployment window:",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 10,
                status = MessageStatus.READ,
                pollData = PollData(
                    question = "Select target release window for AlphaLink Mesh v2.5:",
                    options = listOf(
                        PollOption(1, "Deploy Immediately (Epoch 48)", 14),
                        PollOption(2, "Wait for secondary cryptographic audit", 8),
                        PollOption(3, "Stage on Canary clusters first", 22)
                    ),
                    isQuiz = false,
                    userVotedIndex = 2
                )
            )
        )

        val channelMsgs = listOf(
            Message(
                id = "cm1",
                chatId = channelChat.id,
                senderId = "alphalink_system",
                senderName = "AlphaLink Core Team",
                text = "⚡ AlphaLink v2.4 Released!\n\n• True night-black 3D command center interface\n• 6GB file transfer with resumable chunking\n• Encrypted WebRTC voice & video with adaptive bitrate\n• Granular channel roles & forum topics\n• 100% Open-source and self-hostable.\n\nRead documentation: https://github.com/Alpha-for-a-Reason",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 4,
                status = MessageStatus.READ,
                reactions = mapOf("💚" to 1420, "🚀" to 950, "⚡" to 2100)
            )
        )

        val savedMsgs = listOf(
            Message(
                id = "sm1",
                chatId = savedChat.id,
                senderId = _currentUser.value.id,
                senderName = _currentUser.value.displayName,
                text = "Encrypted storage node backup checklist:\n1. Verify SOCKS5 proxy configuration\n2. Rotate Ed25519 root keys\n3. Sync local credentials via Credential Manager",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24,
                status = MessageStatus.READ
            )
        )

        val botMsgs = listOf(
            Message(
                id = "bm1",
                chatId = botChat.id,
                senderId = sentinel.id,
                senderName = sentinel.displayName,
                text = "Greetings Commander. AlphaLink Sentinel Bot v2.4 active. Type /status to inspect mesh health, /ping for latency analysis, or /keygen to generate ephemeral session pairs.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 2,
                status = MessageStatus.READ
            )
        )

        _messages.value = mapOf(
            directChat.id to directMsgs,
            groupChat.id to groupMsgs,
            channelChat.id to channelMsgs,
            savedChat.id to savedMsgs,
            botChat.id to botMsgs
        )

        // Seed stories
        _stories.value = listOf(
            Story(
                id = "s1",
                userId = "user_kaelen",
                userName = "Kaelen",
                userAvatarEmoji = "🧑‍💻",
                title = "Node Sync Online",
                mediaDescription = "Global latency map down to 11ms on APAC relay."
            ),
            Story(
                id = "s2",
                userId = "user_elena",
                userName = "Elena",
                userAvatarEmoji = "🛰️",
                title = "6GB Multipart Demo",
                mediaDescription = "Transferred 5.8 GB ISO image over parallel peer streams."
            ),
            Story(
                id = "s3",
                userId = "alphalink_system",
                userName = "AlphaLink",
                userAvatarEmoji = "⚡",
                title = "Night-Black UI",
                mediaDescription = "Futuristic 3D Cyber Command Center ready for production."
            )
        )

        // Seed active sessions
        _sessions.value = listOf(
            ActiveSession(
                deviceName = "Pixel 9 Pro (This Device)",
                clientType = "AlphaLink Android v2.4",
                ipAddress = "192.168.1.104 (Local Mesh)",
                location = "Encrypted Tunnel (WireGuard)",
                isCurrent = true,
                lastActive = "Active now"
            ),
            ActiveSession(
                deviceName = "CyberDeck Terminal - Workstation",
                clientType = "AlphaLink Desktop Qt/Rust",
                ipAddress = "10.0.0.12",
                location = "Zurich, Switzerland",
                isCurrent = false,
                lastActive = "12 minutes ago"
            ),
            ActiveSession(
                deviceName = "iPad Pro M4 - Security Console",
                clientType = "AlphaLink Web Terminal",
                ipAddress = "172.16.4.88",
                location = "Reykjavik, Iceland",
                isCurrent = false,
                lastActive = "Yesterday at 21:04"
            )
        )
    }

    // Auth actions
    fun setAuthPhone(phone: String) {
        _authPhone.value = phone
    }

    fun setAuthCountryCode(code: String) {
        _authCountryCode.value = code
    }

    fun setOtpCode(code: String) {
        _otpCode.value = code.filter { it.isDigit() }.take(8)
    }

    fun sendVerificationCode() {
        scope.launch {
            _isSubmittingAuth.value = true
            _authError.value = null
            delay(800)
            _isSubmittingAuth.value = false
            // Reset resend timer
            _resendTimer.value = 60
            startResendCountdown()
        }
    }

    private fun startResendCountdown() {
        scope.launch {
            while (_resendTimer.value > 0) {
                delay(1000)
                _resendTimer.update { if (it > 0) it - 1 else 0 }
            }
        }
    }

    fun verifyCodeAndLogin() {
        scope.launch {
            _isSubmittingAuth.value = true
            _authError.value = null
            delay(900)
            _isSubmittingAuth.value = false
            if (_otpCode.value.length == 8 || _otpCode.value == "12345678" || _otpCode.value.isEmpty()) {
                _isAuthenticated.value = true
            } else {
                _authError.value = "Invalid 8-digit verification code. Please check and retry."
            }
        }
    }

    fun continueWithGoogle() {
        scope.launch {
            _isSubmittingAuth.value = true
            delay(700)
            _isSubmittingAuth.value = false
            _isAuthenticated.value = true
        }
    }

    fun logout() {
        _isAuthenticated.value = false
    }

    // Message actions
    fun sendMessage(
        chatId: String,
        text: String,
        replyTo: Message? = null,
        mediaAttachment: MediaAttachment? = null,
        voiceWaveform: List<Float>? = null,
        selfDestructSeconds: Int? = null
    ) {
        val newMsg = Message(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            senderId = _currentUser.value.id,
            senderName = _currentUser.value.displayName,
            text = text,
            timestamp = System.currentTimeMillis(),
            status = MessageStatus.SENT,
            replyToMessageId = replyTo?.id,
            replyToText = replyTo?.text,
            replyToSenderName = replyTo?.senderName,
            mediaAttachment = mediaAttachment,
            voiceWaveform = voiceWaveform,
            selfDestructSeconds = selfDestructSeconds
        )

        _messages.update { current ->
            val list = current[chatId].orEmpty().toMutableList()
            list.add(newMsg)
            current + (chatId to list)
        }

        // Update chat's last message
        _chats.update { current ->
            current.map {
                if (it.id == chatId) it.copy(lastMessage = newMsg) else it
            }
        }

        // Simulate delivery & read
        scope.launch {
            delay(1200)
            updateMessageStatus(chatId, newMsg.id, MessageStatus.DELIVERED)
            delay(1500)
            updateMessageStatus(chatId, newMsg.id, MessageStatus.READ)

            // If bot chat or kaelen, simulate auto reply
            if (chatId == "chat_sentinel_bot") {
                delay(800)
                val botReply = Message(
                    id = UUID.randomUUID().toString(),
                    chatId = chatId,
                    senderId = "user_sentinel",
                    senderName = "Sentinel Bot",
                    text = "Telemetry node acknowledged: \"$text\". Encryption cipher: AES-256-GCM + Kyber-768 post-quantum key exchange active. Latency: 11ms.",
                    status = MessageStatus.READ
                )
                _messages.update { current ->
                    val list = current[chatId].orEmpty().toMutableList()
                    list.add(botReply)
                    current + (chatId to list)
                }
            }
        }
    }

    fun toggleReaction(chatId: String, messageId: String, emoji: String) {
        _messages.update { currentMap ->
            val list = currentMap[chatId]?.map { msg ->
                if (msg.id == messageId) {
                    val currentReactions = msg.reactions.toMutableMap()
                    val existingReaction = msg.userReaction
                    if (existingReaction == emoji) {
                        // Remove reaction
                        val count = (currentReactions[emoji] ?: 1) - 1
                        if (count <= 0) currentReactions.remove(emoji) else currentReactions[emoji] = count
                        msg.copy(reactions = currentReactions, userReaction = null)
                    } else {
                        // Add or replace
                        if (existingReaction != null) {
                            val oldCount = (currentReactions[existingReaction] ?: 1) - 1
                            if (oldCount <= 0) currentReactions.remove(existingReaction) else currentReactions[existingReaction] = oldCount
                        }
                        currentReactions[emoji] = (currentReactions[emoji] ?: 0) + 1
                        msg.copy(reactions = currentReactions, userReaction = emoji)
                    }
                } else msg
            } ?: emptyList()
            currentMap + (chatId to list)
        }
    }

    fun pinMessage(chatId: String, messageId: String) {
        _messages.update { currentMap ->
            val list = currentMap[chatId]?.map { msg ->
                if (msg.id == messageId) msg.copy(isPinned = !msg.isPinned) else msg
            } ?: emptyList()
            currentMap + (chatId to list)
        }
    }

    fun deleteMessage(chatId: String, messageId: String) {
        _messages.update { currentMap ->
            val list = currentMap[chatId]?.filterNot { it.id == messageId } ?: emptyList()
            currentMap + (chatId to list)
        }
    }

    fun editMessage(chatId: String, messageId: String, newText: String) {
        _messages.update { currentMap ->
            val list = currentMap[chatId]?.map { msg ->
                if (msg.id == messageId) msg.copy(text = newText, isEdited = true) else msg
            } ?: emptyList()
            currentMap + (chatId to list)
        }
    }

    fun votePoll(chatId: String, messageId: String, optionIndex: Int) {
        _messages.update { currentMap ->
            val list = currentMap[chatId]?.map { msg ->
                if (msg.id == messageId && msg.pollData != null) {
                    val poll = msg.pollData
                    val previousVote = poll.userVotedIndex
                    val updatedOptions = poll.options.mapIndexed { idx, opt ->
                        when (idx) {
                            optionIndex -> opt.copy(voteCount = opt.voteCount + 1)
                            previousVote -> opt.copy(voteCount = maxOf(0, opt.voteCount - 1))
                            else -> opt
                        }
                    }
                    msg.copy(pollData = poll.copy(options = updatedOptions, userVotedIndex = optionIndex))
                } else msg
            } ?: emptyList()
            currentMap + (chatId to list)
        }
    }

    private fun updateMessageStatus(chatId: String, messageId: String, status: MessageStatus) {
        _messages.update { currentMap ->
            val list = currentMap[chatId]?.map { msg ->
                if (msg.id == messageId) msg.copy(status = status) else msg
            } ?: emptyList()
            currentMap + (chatId to list)
        }
    }

    // Call controls
    fun startCall(targetName: String, targetAvatar: String, isVideo: Boolean) {
        _callState.value = CallState(
            isInCall = true,
            targetName = targetName,
            targetAvatar = targetAvatar,
            isVideo = isVideo,
            durationSeconds = 0
        )
        scope.launch {
            while (_callState.value.isInCall) {
                delay(1000)
                _callState.update { it.copy(durationSeconds = it.durationSeconds + 1) }
            }
        }
    }

    fun endCall() {
        _callState.value = CallState(isInCall = false)
    }

    fun toggleMute() {
        _callState.update { it.copy(isMuted = !it.isMuted) }
    }

    fun toggleVideo() {
        _callState.update { it.copy(isVideoMuted = !it.isVideoMuted) }
    }

    fun toggleSpeaker() {
        _callState.update { it.copy(isSpeakerOn = !it.isSpeakerOn) }
    }

    fun toggleNoiseSuppression() {
        _callState.update { it.copy(noiseSuppression = !it.noiseSuppression) }
    }

    // Session management
    fun terminateSession(sessionId: String) {
        _sessions.update { it.filterNot { s -> s.id == sessionId } }
    }

    fun terminateAllOtherSessions() {
        _sessions.update { it.filter { s -> s.isCurrent } }
    }

    // Settings
    fun setThemeMode(mode: CyberThemeMode) {
        _themeMode.value = mode
    }

    fun setAnimationIntensity(intensity: Float) {
        _animationIntensity.value = intensity
    }

    fun setReducedMotion(enabled: Boolean) {
        _reducedMotion.value = enabled
    }

    fun setBiometricLock(enabled: Boolean) {
        _biometricLockEnabled.value = enabled
        if (!enabled) {
            _isAppLocked.value = false
        }
        scope.launch {
            securityPreferences?.setRequireBiometricUnlock(enabled)
        }
    }

    fun lockApp() {
        if (_biometricLockEnabled.value) {
            _isAppLocked.value = true
        }
    }

    fun unlockApp() {
        _isAppLocked.value = false
    }

    fun setTwoStepVerification(enabled: Boolean) {
        _twoStepVerificationEnabled.value = enabled
        scope.launch {
            securityPreferences?.setTwoStepVerification(enabled)
        }
    }

    fun setServerUrl(url: String) {
        _serverUrl.value = url
    }

    fun setProxyConfig(config: ProxyConfig) {
        _proxyConfig.value = config
    }
}

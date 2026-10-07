package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.example.data.model.Chat
import com.example.data.model.Story
import com.example.data.repository.AlphaLinkRepository
import com.example.ui.auth.LoginScreen
import com.example.ui.calls.CallScreen
import com.example.ui.chat.ChatDetailScreen
import com.example.ui.chat.ChatListScreen
import com.example.ui.security.BiometricLockScreen
import com.example.ui.settings.DeveloperScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.stories.StoryViewerDialog
import com.example.ui.theme.MyApplicationTheme

enum class Screen {
    CHATS,
    CHAT_DETAIL,
    SETTINGS,
    DEVELOPER,
    CALL
}

class MainActivity : FragmentActivity() {
    private val repository = AlphaLinkRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        repository.initSecurityPreferences(this)

        setContent {
            val themeMode by repository.themeMode.collectAsState()
            val animationIntensity by repository.animationIntensity.collectAsState()
            val reducedMotion by repository.reducedMotion.collectAsState()
            val isAuthenticated by repository.isAuthenticated.collectAsState()
            val isAppLocked by repository.isAppLocked.collectAsState()
            val callState by repository.callState.collectAsState()

            var currentScreen by remember { mutableStateOf(Screen.CHATS) }
            var activeChat by remember { mutableStateOf<Chat?>(null) }
            var activeStory by remember { mutableStateOf<Story?>(null) }

            // Switch to Call screen automatically when call begins
            LaunchedEffect(callState.isInCall) {
                if (callState.isInCall) {
                    currentScreen = Screen.CALL
                } else if (currentScreen == Screen.CALL) {
                    currentScreen = Screen.CHATS
                }
            }

            MyApplicationTheme(
                themeMode = themeMode,
                animationIntensity = animationIntensity,
                reducedMotion = reducedMotion
            ) {
                if (!isAuthenticated) {
                    LoginScreen(
                        repository = repository,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (isAppLocked) {
                    BiometricLockScreen(
                        repository = repository,
                        activity = this@MainActivity,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    AnimatedContent(
                        targetState = currentScreen,
                        label = "ScreenTransition"
                    ) { screen ->
                        when (screen) {
                            Screen.CHATS -> {
                                ChatListScreen(
                                    repository = repository,
                                    onChatClick = { chat ->
                                        activeChat = chat
                                        currentScreen = Screen.CHAT_DETAIL
                                    },
                                    onStoryClick = { story ->
                                        activeStory = story
                                    },
                                    onSettingsClick = {
                                        currentScreen = Screen.SETTINGS
                                    },
                                    onStartCallClick = { name, avatar, isVideo ->
                                        repository.startCall(name, avatar, isVideo)
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Screen.CHAT_DETAIL -> {
                                val chat = activeChat
                                if (chat != null) {
                                    ChatDetailScreen(
                                        chat = chat,
                                        repository = repository,
                                        onBack = { currentScreen = Screen.CHATS },
                                        onStartCall = { name, avatar, isVideo ->
                                            repository.startCall(name, avatar, isVideo)
                                        },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    currentScreen = Screen.CHATS
                                }
                            }

                            Screen.SETTINGS -> {
                                SettingsScreen(
                                    repository = repository,
                                    onBack = { currentScreen = Screen.CHATS },
                                    onOpenDeveloper = { currentScreen = Screen.DEVELOPER },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Screen.DEVELOPER -> {
                                DeveloperScreen(
                                    onBack = { currentScreen = Screen.SETTINGS },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Screen.CALL -> {
                                CallScreen(
                                    repository = repository,
                                    onEndCall = { currentScreen = Screen.CHATS },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    // Story Dialog Overlay if active
                    activeStory?.let { story ->
                        StoryViewerDialog(
                            story = story,
                            onDismiss = { activeStory = null }
                        )
                    }
                }
            }
        }
    }
}

package com.example.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AlphaLinkRepository
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberGlassSurface
import com.example.ui.components.HolographicCard
import com.example.ui.components.NeonBadge
import com.example.ui.theme.CyberThemeMode
import com.example.ui.theme.LocalCyberColors
import com.example.ui.theme.LocalReducedMotion

@Composable
fun SettingsScreen(
    repository: AlphaLinkRepository,
    onBack: () -> Unit,
    onOpenDeveloper: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val cyberColors = LocalCyberColors.current
    val currentUser by repository.currentUser.collectAsState()
    val themeMode by repository.themeMode.collectAsState()
    val animationIntensity by repository.animationIntensity.collectAsState()
    val reducedMotion by repository.reducedMotion.collectAsState()
    val biometricLock by repository.biometricLockEnabled.collectAsState()
    val twoStep by repository.twoStepVerificationEnabled.collectAsState()
    val serverUrl by repository.serverUrl.collectAsState()
    val proxyConfig by repository.proxyConfig.collectAsState()
    val sessions by repository.sessions.collectAsState()

    var showServerConfigDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(cyberColors.cyberBackground),
        containerColor = cyberColors.cyberBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cyberColors.cyberGraphite.copy(alpha = 0.95f))
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = cyberColors.neonAccent
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "COMMAND SETTINGS",
                    color = cyberColors.textPrimary,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Profile Card
            HolographicCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(cyberColors.cyberSurface)
                            .border(1.5.dp, cyberColors.neonAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(currentUser.avatarEmoji, fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = currentUser.displayName,
                            color = cyberColors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "@${currentUser.username} • ${currentUser.phoneNumber}",
                            color = cyberColors.neonAccent,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = currentUser.publicKeyFingerprint,
                            color = cyberColors.textSecondary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // About > Developer Card
            Text(
                text = "ABOUT & SYSTEM ARCHITECTURE",
                color = cyberColors.neonAccent,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            CyberGlassSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenDeveloper)
                    .testTag("open_developer_section")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(cyberColors.neonAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Terminal,
                                contentDescription = "Developer",
                                tint = cyberColors.neonAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Developer",
                                color = cyberColors.textPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Instagram (@alpha_for_a_reason_) & GitHub links",
                                color = cyberColors.textSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Navigate",
                        tint = cyberColors.neonAccent
                    )
                }
            }

            // Theme & Customization Section
            Text(
                text = "CYBER THEME & VISUAL DISPLAY",
                color = cyberColors.neonAccent,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            CyberGlassSurface(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Select Interface Theme", color = cyberColors.textPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    CyberThemeMode.values().forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { repository.setThemeMode(mode) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = themeMode == mode,
                                onClick = { repository.setThemeMode(mode) },
                                colors = RadioButtonDefaults.colors(selectedColor = cyberColors.neonAccent)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(mode.displayName, color = cyberColors.textPrimary, fontSize = 13.sp)
                        }
                    }

                    HorizontalDivider(color = cyberColors.glassBorder.copy(alpha = 0.3f))

                    // Reduced Motion Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Reduced Motion", color = cyberColors.textPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Disable non-essential pulse animations (WCAG AA)", color = cyberColors.textSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = reducedMotion,
                            onCheckedChange = { repository.setReducedMotion(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = cyberColors.neonAccent)
                        )
                    }
                }
            }

            // Security & Active Sessions
            Text(
                text = "AUTHENTICATION & SESSIONS",
                color = cyberColors.neonAccent,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            CyberGlassSurface(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Require biometric unlock", color = cyberColors.textPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                NeonBadge("DATASTORE", color = cyberColors.secondaryAccent)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Enforce fingerprint or face recognition via Android BiometricPrompt on app launch. Persisted across reboots.", color = cyberColors.textSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = biometricLock,
                            onCheckedChange = { repository.setBiometricLock(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = cyberColors.neonAccent),
                            modifier = Modifier.testTag("require_biometric_unlock_switch")
                        )
                    }

                    if (biometricLock) {
                        CyberButton(
                            text = "🔒 Lock Terminal Now (Test Biometrics)",
                            onClick = { repository.lockApp() },
                            isPrimary = false,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "test_biometric_lock_button"
                        )
                    }

                    HorizontalDivider(color = cyberColors.glassBorder.copy(alpha = 0.3f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Two-Step Verification (2FA)", color = cyberColors.textPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Require password challenge when signing in on new device", color = cyberColors.textSecondary, fontSize = 11.sp)
                        }
                        Switch(
                            checked = twoStep,
                            onCheckedChange = { repository.setTwoStepVerification(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = cyberColors.neonAccent)
                        )
                    }
                }
            }

            // Active Multi-Device Sessions
            Text(
                text = "ACTIVE PEER SESSIONS (${sessions.size})",
                color = cyberColors.neonAccent,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            CyberGlassSurface(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    sessions.forEach { session ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(session.deviceName, color = cyberColors.textPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    if (session.isCurrent) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        NeonBadge("THIS DEVICE", color = cyberColors.neonAccent)
                                    }
                                }
                                Text("${session.clientType} • ${session.location}", color = cyberColors.textSecondary, fontSize = 11.sp)
                                Text("${session.ipAddress} • ${session.lastActive}", color = cyberColors.neonAccent, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            }
                            if (!session.isCurrent) {
                                TextButton(onClick = { repository.terminateSession(session.id) }) {
                                    Text("Terminate", color = cyberColors.alertRed, fontSize = 11.sp)
                                }
                            }
                        }
                        HorizontalDivider(color = cyberColors.glassBorder.copy(alpha = 0.2f))
                    }

                    CyberButton(
                        text = "Terminate All Other Sessions",
                        onClick = { repository.terminateAllOtherSessions() },
                        isPrimary = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Self-Hosted Server Configuration
            Text(
                text = "SELF-HOSTED NODE & PROXY",
                color = cyberColors.neonAccent,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            CyberGlassSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showServerConfigDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Active Mesh Node Gateway", color = cyberColors.textPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(serverUrl, color = cyberColors.secondaryAccent, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                    Icon(Icons.Default.Tune, contentDescription = "Configure Node", tint = cyberColors.neonAccent)
                }
            }

            // Logout Action
            CyberButton(
                text = "Lock Terminal & Sign Out",
                onClick = {
                    repository.logout()
                    onBack()
                },
                isPrimary = false,
                modifier = Modifier.fillMaxWidth(),
                testTag = "logout_button"
            )
        }
    }

    if (showServerConfigDialog) {
        var tempUrl by remember { mutableStateOf(serverUrl) }
        AlertDialog(
            onDismissRequest = { showServerConfigDialog = false },
            containerColor = cyberColors.cyberGraphite,
            title = { Text("SELF-HOSTED NODE URL", color = cyberColors.textPrimary, fontFamily = FontFamily.Monospace) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Point this client to your private self-hosted AlphaLink relay node:", color = cyberColors.textSecondary, fontSize = 12.sp)
                    OutlinedTextField(
                        value = tempUrl,
                        onValueChange = { tempUrl = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.setServerUrl(tempUrl)
                        showServerConfigDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = cyberColors.neonAccent)
                ) {
                    Text("Save", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showServerConfigDialog = false }) {
                    Text("Cancel", color = cyberColors.textSecondary)
                }
            }
        )
    }
}

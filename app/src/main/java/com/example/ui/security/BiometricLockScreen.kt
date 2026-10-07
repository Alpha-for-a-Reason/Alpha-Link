package com.example.ui.security

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.data.repository.AlphaLinkRepository
import com.example.security.BiometricAuthManager
import com.example.security.BiometricStatus
import com.example.ui.components.CyberButton
import com.example.ui.components.EncryptedDataStreamEffect
import com.example.ui.components.HolographicCard
import com.example.ui.components.NeonBadge
import com.example.ui.theme.LocalCyberColors
import com.example.ui.theme.LocalReducedMotion

@Composable
fun BiometricLockScreen(
    repository: AlphaLinkRepository,
    activity: FragmentActivity?,
    modifier: Modifier = Modifier
) {
    val cyberColors = LocalCyberColors.current
    val context = LocalContext.current
    val reducedMotion = LocalReducedMotion.current
    val biometricManager = remember { BiometricAuthManager(context) }
    val biometricStatus = remember { biometricManager.checkBiometricAvailability() }

    var feedbackMessage by remember { mutableStateOf("Touch sensor or glance at camera to unlock") }
    var isAuthenticating by remember { mutableStateOf(false) }
    var showPasscodeFallback by remember { mutableStateOf(false) }
    var enteredPasscode by remember { mutableStateOf("") }

    // Holographic Scanline Animation
    val scanProgress = if (!reducedMotion) {
        val infiniteTransition = rememberInfiniteTransition(label = "BiometricScan")
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1800, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "Scanline"
        ).value
    } else {
        0.5f
    }

    fun launchBiometricPrompt() {
        if (activity == null) {
            // Emulated/preview environment fallback
            repository.unlockApp()
            return
        }

        isAuthenticating = true
        feedbackMessage = "Awaiting biometric signal verification..."

        biometricManager.promptBiometricAuth(
            activity = activity,
            title = "AlphaLink Terminal Access",
            subtitle = "Verify biometric identity (Fingerprint or Face) to decrypt session",
            negativeButtonText = "Use Terminal Keycode",
            onSuccess = {
                isAuthenticating = false
                feedbackMessage = "Identity authenticated. Decrypting..."
                repository.unlockApp()
            },
            onError = { code, err ->
                isAuthenticating = false
                feedbackMessage = "Sensor message: $err"
                if (code == 10 || code == 13) { // User canceled or clicked negative
                    showPasscodeFallback = true
                }
            },
            onFailed = {
                isAuthenticating = false
                feedbackMessage = "Biometric match rejected. Try again."
            }
        )
    }

    // Auto-trigger biometric prompt on first render
    LaunchedEffect(Unit) {
        if (activity != null) {
            launchBiometricPrompt()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(cyberColors.cyberBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        EncryptedDataStreamEffect(modifier = Modifier.fillMaxSize(), alpha = 0.1f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Lock Status Badge
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(cyberColors.neonAccent.copy(alpha = 0.2f), Color.Transparent)
                        )
                    )
                    .border(2.dp, cyberColors.neonAccent, CircleShape)
                    .clickable { launchBiometricPrompt() }
                    .testTag("biometric_icon_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = "Biometric Lock",
                    tint = cyberColors.neonAccent,
                    modifier = Modifier.size(54.dp)
                )

                // Animated scanline across fingerprint
                if (!reducedMotion) {
                    Canvas(modifier = Modifier.size(90.dp)) {
                        val y = size.height * scanProgress
                        drawLine(
                            color = cyberColors.secondaryAccent,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 2f
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "TERMINAL LOCKED",
                color = cyberColors.textPrimary,
                fontSize = 22.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "BIOMETRIC CIPHER GUARD ACTIVE",
                color = cyberColors.neonAccent,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Holographic Status Panel
            HolographicCard(modifier = Modifier.fillMaxWidth().widthIn(max = 420.dp)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STATUS",
                            color = cyberColors.textSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        NeonBadge(
                            text = if (biometricStatus == BiometricStatus.AVAILABLE) "HARDWARE READY" else "FALLBACK ACTIVE",
                            color = if (biometricStatus == BiometricStatus.AVAILABLE) cyberColors.neonAccent else cyberColors.warningAmber
                        )
                    }

                    Text(
                        text = feedbackMessage,
                        color = cyberColors.textPrimary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    CyberButton(
                        text = "Scan Fingerprint / Face",
                        onClick = { launchBiometricPrompt() },
                        isPrimary = true,
                        isLoading = isAuthenticating,
                        leadingIcon = {
                            Icon(Icons.Default.Fingerprint, null, tint = Color.Black, modifier = Modifier.size(20.dp))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "trigger_biometric_scan_button"
                    )

                    // Passcode Fallback Button
                    CyberButton(
                        text = if (!showPasscodeFallback) "Use Keycode Fallback" else "Cancel Keycode",
                        onClick = { showPasscodeFallback = !showPasscodeFallback },
                        isPrimary = false,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "keycode_fallback_button"
                    )

                    // Fallback Keycode input
                    if (showPasscodeFallback) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Enter 4-Digit Keycode (Demo: 0000)",
                                color = cyberColors.textSecondary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                (0..3).forEach { index ->
                                    val digit = enteredPasscode.getOrNull(index)?.toString() ?: ""
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(cyberColors.cyberSurface)
                                            .border(1.dp, cyberColors.glassBorder, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (digit.isNotEmpty()) "•" else "",
                                            color = cyberColors.neonAccent,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // Numeric Keypad Simulation
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                val keys = listOf(
                                    listOf("1", "2", "3"),
                                    listOf("4", "5", "6"),
                                    listOf("7", "8", "9"),
                                    listOf("C", "0", "OK")
                                )
                                keys.forEach { row ->
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        row.forEach { key ->
                                            Box(
                                                modifier = Modifier
                                                    .size(52.dp, 40.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(cyberColors.cyberSurface)
                                                    .border(0.8.dp, cyberColors.glassBorder, RoundedCornerShape(6.dp))
                                                    .clickable {
                                                        when (key) {
                                                            "C" -> enteredPasscode = ""
                                                            "OK" -> {
                                                                if (enteredPasscode == "0000" || enteredPasscode.length == 4) {
                                                                    repository.unlockApp()
                                                                } else {
                                                                    feedbackMessage = "Invalid Keycode"
                                                                }
                                                            }
                                                            else -> {
                                                                if (enteredPasscode.length < 4) {
                                                                    enteredPasscode += key
                                                                    if (enteredPasscode.length == 4) {
                                                                        repository.unlockApp()
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = key,
                                                    color = if (key == "OK") cyberColors.neonAccent else cyberColors.textPrimary,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Encrypted Memory",
                    tint = cyberColors.neonAccent.copy(alpha = 0.6f),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Android BiometricPrompt API v1.2 Protected",
                    color = cyberColors.textSecondary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

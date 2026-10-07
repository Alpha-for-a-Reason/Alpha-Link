package com.example.ui.calls

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AlphaLinkRepository
import com.example.ui.components.NeonBadge
import com.example.ui.components.NetworkQualityBadge
import com.example.ui.theme.LocalCyberColors
import com.example.ui.theme.LocalReducedMotion

@Composable
fun CallScreen(
    repository: AlphaLinkRepository,
    onEndCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        repository.endCall()
        onEndCall()
    }

    val cyberColors = LocalCyberColors.current
    val callState by repository.callState.collectAsState()
    val reducedMotion = LocalReducedMotion.current

    val formatDuration = remember(callState.durationSeconds) {
        val mins = callState.durationSeconds / 60
        val secs = callState.durationSeconds % 60
        String.format("%02d:%02d", mins, secs)
    }

    // Audio visualizer wave animation
    val wavePhase = if (!reducedMotion) {
        val infiniteTransition = rememberInfiniteTransition(label = "WavePhase")
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 6.28f,
            animationSpec = infiniteRepeatable(
                animation = tween(2500, easing = LinearEasing)
            ),
            label = "WavePhaseValue"
        ).value
    } else {
        0f
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Holographic Audio Visualizer Canvas Background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height * 0.42f)
            val baseRadius = 120f

            for (i in 1..4) {
                val radius = baseRadius + (i * 35f)
                val alpha = (0.25f / i)
                drawCircle(
                    color = cyberColors.neonAccent.copy(alpha = alpha),
                    radius = radius + (kotlin.math.sin(wavePhase + i).toFloat() * 10f),
                    center = center,
                    style = Stroke(width = 1.5f)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Network stats & encryption
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NeonBadge(text = "E2E WEBRTC ENCRYPTED", color = cyberColors.neonAccent)
                    NetworkQualityBadge(
                        latencyMs = callState.latencyMs,
                        bitrateKbps = callState.bitrateKbps
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = callState.targetName,
                    color = cyberColors.textPrimary,
                    fontSize = 26.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (callState.isVideo) "Encrypted Video Stream • $formatDuration" else "Encrypted Voice Stream • $formatDuration",
                    color = cyberColors.neonAccent,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Central Avatar / Video Surface
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(cyberColors.cyberGraphite)
                    .border(
                        2.5.dp,
                        Brush.sweepGradient(
                            listOf(cyberColors.neonAccent, cyberColors.secondaryAccent, cyberColors.neonAccent)
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = callState.targetAvatar,
                    fontSize = 64.sp
                )
            }

            // Technical telemetry card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(cyberColors.cyberSurface)
                    .border(0.8.dp, cyberColors.glassBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("CODEC", color = cyberColors.textSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text("Opus / AV1", color = cyberColors.neonAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("NOISE SUPPRESSION", color = cyberColors.textSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text(if (callState.noiseSuppression) "Active (RNNoise)" else "Bypass", color = cyberColors.secondaryAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("JITTER", color = cyberColors.textSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text("0.8 ms", color = cyberColors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }

            // Call Controls Grid
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute
                    IconButton(
                        onClick = { repository.toggleMute() },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(if (callState.isMuted) cyberColors.alertRed.copy(alpha = 0.3f) else cyberColors.cyberSurface)
                            .border(1.dp, cyberColors.glassBorder, CircleShape)
                            .testTag("call_mute_toggle")
                    ) {
                        Icon(
                            imageVector = if (callState.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mute",
                            tint = if (callState.isMuted) cyberColors.alertRed else cyberColors.neonAccent
                        )
                    }

                    // Video Toggle
                    IconButton(
                        onClick = { repository.toggleVideo() },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(if (callState.isVideoMuted) cyberColors.alertRed.copy(alpha = 0.3f) else cyberColors.cyberSurface)
                            .border(1.dp, cyberColors.glassBorder, CircleShape)
                            .testTag("call_video_toggle")
                    ) {
                        Icon(
                            imageVector = if (callState.isVideoMuted) Icons.Default.VideocamOff else Icons.Default.Videocam,
                            contentDescription = "Video",
                            tint = if (callState.isVideoMuted) cyberColors.alertRed else cyberColors.secondaryAccent
                        )
                    }

                    // Speaker
                    IconButton(
                        onClick = { repository.toggleSpeaker() },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(cyberColors.cyberSurface)
                            .border(1.dp, cyberColors.glassBorder, CircleShape)
                            .testTag("call_speaker_toggle")
                    ) {
                        Icon(
                            imageVector = if (callState.isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                            contentDescription = "Speaker",
                            tint = cyberColors.neonAccent
                        )
                    }

                    // Noise Suppression Toggle
                    IconButton(
                        onClick = { repository.toggleNoiseSuppression() },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(cyberColors.cyberSurface)
                            .border(1.dp, cyberColors.glassBorder, CircleShape)
                            .testTag("call_noise_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Noise Suppression",
                            tint = if (callState.noiseSuppression) cyberColors.neonAccent else cyberColors.textSecondary
                        )
                    }
                }

                // End Call Red Button
                IconButton(
                    onClick = {
                        repository.endCall()
                        onEndCall()
                    },
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(cyberColors.alertRed)
                        .testTag("end_call_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End call",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

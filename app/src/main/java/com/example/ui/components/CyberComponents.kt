package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalCyberColors
import com.example.ui.theme.LocalReducedMotion
import com.example.ui.theme.NeonGreenPrimary
import kotlin.random.Random

@Composable
fun CyberGlassSurface(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(14.dp),
    borderColor: Color = LocalCyberColors.current.glassBorder,
    borderWidth: Dp = 1.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val cyberColors = LocalCyberColors.current
    Box(
        modifier = modifier
            .clip(shape)
            .background(cyberColors.cyberSurface)
            .border(borderWidth, borderColor, shape),
        content = content
    )
}

@Composable
fun HolographicCard(
    modifier: Modifier = Modifier,
    borderColor: Color = LocalCyberColors.current.neonAccent.copy(alpha = 0.4f),
    glowColor: Color = LocalCyberColors.current.neonAccent.copy(alpha = 0.12f),
    content: @Composable ColumnScope.() -> Unit
) {
    val cyberColors = LocalCyberColors.current
    val reducedMotion = LocalReducedMotion.current
    
    val infiniteTransition = rememberInfiniteTransition(label = "HoloPulse")
    val alphaAnim by if (!reducedMotion) {
        infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 0.7f,
            animationSpec = infiniteRepeatable(
                animation = tween(2400, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "HoloAlpha"
        )
    } else {
        remember { mutableFloatStateOf(0.4f) }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        cyberColors.cyberGraphite.copy(alpha = 0.95f),
                        cyberColors.cyberBackground.copy(alpha = 0.98f)
                    )
                )
            )
            .border(
                1.dp,
                borderColor.copy(alpha = alphaAnim),
                RoundedCornerShape(16.dp)
            )
    ) {
        // Subtle top neon highlight line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            cyberColors.neonAccent.copy(alpha = alphaAnim),
                            Color.Transparent
                        )
                    )
                )
        )
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun CyberButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    testTag: String = "cyber_button"
) {
    val cyberColors = LocalCyberColors.current
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .testTag(testTag)
            .height(50.dp)
            .clip(shape)
            .background(
                if (isPrimary) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            cyberColors.neonAccent,
                            cyberColors.secondaryAccent
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(
                            cyberColors.cyberGraphite,
                            cyberColors.cyberSurface
                        )
                    )
                }
            )
            .border(
                1.dp,
                if (isPrimary) Color.Transparent else cyberColors.neonAccent.copy(alpha = 0.5f),
                shape
            )
            .clickable(enabled = !isLoading, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = if (isPrimary) Color.Black else cyberColors.neonAccent,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    color = if (isPrimary) Color.Black else cyberColors.neonAccent,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun NeonBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = LocalCyberColors.current.neonAccent
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(0.8.dp, color.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 0.4.sp
        )
    }
}

@Composable
fun EncryptedDataStreamEffect(
    modifier: Modifier = Modifier,
    alpha: Float = 0.18f
) {
    val reducedMotion = LocalReducedMotion.current
    val cyberColors = LocalCyberColors.current

    val streamProgress = if (!reducedMotion) {
        val infiniteTransition = rememberInfiniteTransition(label = "StreamMatrix")
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(8000, easing = LinearEasing)
            ),
            label = "MatrixOffset"
        ).value
    } else {
        0.5f
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val colWidth = 28f
        val cols = (width / colWidth).toInt()

        for (i in 0 until cols) {
            val startY = ((i * 123 + streamProgress * height * 1.5f) % (height + 200)) - 100
            val x = i * colWidth + 12f
            
            // Draw faint cyber streams
            drawLine(
                color = if (i % 2 == 0) cyberColors.neonAccent.copy(alpha = alpha) else cyberColors.secondaryAccent.copy(alpha = alpha),
                start = Offset(x, startY),
                end = Offset(x, startY + 60f),
                strokeWidth = 1.2f
            )
            drawCircle(
                color = cyberColors.neonAccent.copy(alpha = alpha * 1.5f),
                radius = 1.5f,
                center = Offset(x, startY + 60f)
            )
        }
    }
}

@Composable
fun AudioWaveformBar(
    amplitudes: List<Float>,
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    barColor: Color = LocalCyberColors.current.neonAccent
) {
    val reducedMotion = LocalReducedMotion.current
    val pulse by if (isPlaying && !reducedMotion) {
        val transition = rememberInfiniteTransition(label = "VoicePulse")
        transition.animateFloat(
            initialValue = 0.8f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "WaveAmp"
        )
    } else {
        remember { mutableFloatStateOf(1.0f) }
    }

    Row(
        modifier = modifier.height(28.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        amplitudes.forEachIndexed { index, amp ->
            val heightFraction = (amp * (if (isPlaying) pulse else 1.0f)).coerceIn(0.15f, 1.0f)
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight(heightFraction)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(
                        if (index % 3 == 0) barColor else barColor.copy(alpha = 0.65f)
                    )
            )
        }
    }
}

@Composable
fun NetworkQualityBadge(
    latencyMs: Int,
    bitrateKbps: Int,
    modifier: Modifier = Modifier
) {
    val cyberColors = LocalCyberColors.current
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(cyberColors.cyberGraphite.copy(alpha = 0.8f))
            .border(0.8.dp, cyberColors.glassBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(cyberColors.neonAccent)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "WebRTC: ${latencyMs}ms | ${bitrateKbps}kbps",
            color = cyberColors.textPrimary,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

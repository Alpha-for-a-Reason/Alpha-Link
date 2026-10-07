package com.example.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberGlassSurface
import com.example.ui.components.HolographicCard
import com.example.ui.components.NeonBadge
import com.example.ui.theme.LocalCyberColors

@Composable
fun DeveloperScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val cyberColors = LocalCyberColors.current
    val context = LocalContext.current
    var showLicensesDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    fun openInstagram(username: String, url: String) {
        val appUri = Uri.parse("http://instagram.com/_u/$username")
        val appIntent = Intent(Intent.ACTION_VIEW, appUri).apply {
            setPackage("com.instagram.android")
        }
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            if (appIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(appIntent)
            } else {
                context.startActivity(webIntent)
            }
        } catch (e: Exception) {
            context.startActivity(webIntent)
        }
    }

    fun openBrowser(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(cyberColors.cyberBackground),
        containerColor = cyberColors.cyberBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cyberColors.cyberGraphite.copy(alpha = 0.9f))
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("developer_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = cyberColors.neonAccent
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "DEVELOPER INTEL",
                        color = cyberColors.textPrimary,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Settings > About > Developer",
                        color = cyberColors.textSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
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
            // Hero Card
            HolographicCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(cyberColors.neonAccent.copy(alpha = 0.15f))
                            .border(1.5.dp, cyberColors.neonAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = "Developer",
                            tint = cyberColors.neonAccent,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "AlphaLink Core Architect",
                            color = cyberColors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Lead Systems & Cryptography Engineering",
                            color = cyberColors.secondaryAccent,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        NeonBadge(text = "VERIFIED CREATOR", color = cyberColors.neonAccent)
                    }
                }
            }

            // Developer Social Links
            Text(
                text = "COMMUNICATION CHANNELS",
                color = cyberColors.neonAccent,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            // Instagram Entry (alpha_for_a_reason_)
            CyberGlassSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        openInstagram(
                            username = "alpha_for_a_reason_",
                            url = "https://www.instagram.com/alpha_for_a_reason_/"
                        )
                    }
                    .testTag("developer_instagram_link")
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
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE1306C).copy(alpha = 0.2f))
                                .border(1.dp, Color(0xFFE1306C).copy(alpha = 0.6f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Instagram",
                                tint = Color(0xFFE1306C),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Instagram",
                                color = cyberColors.textPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "@alpha_for_a_reason_",
                                color = cyberColors.neonAccent,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Open Instagram Profile",
                        tint = cyberColors.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // GitHub Entry (Alpha-for-a-Reason)
            CyberGlassSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        openBrowser("https://github.com/Alpha-for-a-Reason")
                    }
                    .testTag("developer_github_link")
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
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(cyberColors.secondaryAccent.copy(alpha = 0.2f))
                                .border(1.dp, cyberColors.secondaryAccent, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = "GitHub",
                                tint = cyberColors.secondaryAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "GitHub",
                                color = cyberColors.textPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "github.com/Alpha-for-a-Reason",
                                color = cyberColors.secondaryAccent,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Open GitHub Profile",
                        tint = cyberColors.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Additional Items: Version, Build, Licenses, Policy, Terms, Security Contact
            Text(
                text = "RELEASE SPECIFICATIONS",
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
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Application Version", color = cyberColors.textSecondary, fontSize = 13.sp)
                        Text("v2.4.0-quantum", color = cyberColors.neonAccent, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    HorizontalDivider(color = cyberColors.glassBorder.copy(alpha = 0.4f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Build Number", color = cyberColors.textSecondary, fontSize = 13.sp)
                        Text("#8492-PROD", color = cyberColors.textPrimary, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                    }
                    HorizontalDivider(color = cyberColors.glassBorder.copy(alpha = 0.4f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLicensesDialog = true },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Open-Source Licenses", color = cyberColors.textPrimary, fontSize = 13.sp)
                        Icon(Icons.Default.ChevronRight, null, tint = cyberColors.neonAccent, modifier = Modifier.size(18.dp))
                    }
                    HorizontalDivider(color = cyberColors.glassBorder.copy(alpha = 0.4f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { openBrowser("https://github.com/Alpha-for-a-Reason") },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Source-Code Repository", color = cyberColors.textPrimary, fontSize = 13.sp)
                        Icon(Icons.Default.ChevronRight, null, tint = cyberColors.neonAccent, modifier = Modifier.size(18.dp))
                    }
                    HorizontalDivider(color = cyberColors.glassBorder.copy(alpha = 0.4f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showPrivacyDialog = true },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Privacy Policy & Zero-Log Terms", color = cyberColors.textPrimary, fontSize = 13.sp)
                        Icon(Icons.Default.ChevronRight, null, tint = cyberColors.neonAccent, modifier = Modifier.size(18.dp))
                    }
                    HorizontalDivider(color = cyberColors.glassBorder.copy(alpha = 0.4f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Security Contact", color = cyberColors.textSecondary, fontSize = 13.sp)
                        Text("security@alphalink.network", color = cyberColors.secondaryAccent, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                    }
                }
            }
        }
    }

    if (showLicensesDialog) {
        AlertDialog(
            onDismissRequest = { showLicensesDialog = false },
            containerColor = cyberColors.cyberGraphite,
            title = { Text("OPEN-SOURCE LICENSES", color = cyberColors.textPrimary, fontFamily = FontFamily.Monospace) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• AlphaLink Core Engine: GPL-3.0-or-later", color = cyberColors.neonAccent, fontSize = 12.sp)
                    Text("• Jetpack Compose & Material 3: Apache-2.0", color = cyberColors.textSecondary, fontSize = 12.sp)
                    Text("• Kyber-768 & Dilithium Cryptography: CC0 / Public Domain", color = cyberColors.textSecondary, fontSize = 12.sp)
                    Text("• WebRTC Media Engine: BSD-3-Clause", color = cyberColors.textSecondary, fontSize = 12.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showLicensesDialog = false }) {
                    Text("Dismiss", color = cyberColors.neonAccent)
                }
            }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            containerColor = cyberColors.cyberGraphite,
            title = { Text("ZERO-KNOWLEDGE PRIVACY POLICY", color = cyberColors.textPrimary, fontFamily = FontFamily.Monospace) },
            text = {
                Text(
                    text = "AlphaLink stores no plaintext messages, contact rosters, or IP session correlation logs on servers. All cryptographic keys reside on client hardware. Multi-device sync is handled using ephemeral authenticated ratchets.",
                    color = cyberColors.textSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Understood", color = cyberColors.neonAccent)
                }
            }
        )
    }
}

package com.example.ui.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AlphaLinkRepository
import com.example.ui.components.*
import com.example.ui.theme.LocalCyberColors
import com.example.ui.theme.LocalReducedMotion

enum class AuthStep {
    INPUT_METHOD,
    PHONE_INPUT,
    OTP_VERIFICATION
}

@Composable
fun LoginScreen(
    repository: AlphaLinkRepository,
    modifier: Modifier = Modifier
) {
    val cyberColors = LocalCyberColors.current
    val reducedMotion = LocalReducedMotion.current

    val authPhone by repository.authPhone.collectAsState()
    val countryCode by repository.authCountryCode.collectAsState()
    val otpCode by repository.otpCode.collectAsState()
    val resendTimer by repository.resendTimer.collectAsState()
    val authError by repository.authError.collectAsState()
    val isSubmitting by repository.isSubmittingAuth.collectAsState()

    var currentStep by remember { mutableStateOf(AuthStep.INPUT_METHOD) }
    var showCountryPicker by remember { mutableStateOf(false) }

    val countryCodes = listOf(
        "+1" to "United States / Canada",
        "+44" to "United Kingdom",
        "+49" to "Germany",
        "+33" to "France",
        "+81" to "Japan",
        "+91" to "India",
        "+61" to "Australia",
        "+41" to "Switzerland"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(cyberColors.cyberBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Animated encrypted background data stream
        EncryptedDataStreamEffect(
            modifier = Modifier.fillMaxSize(),
            alpha = 0.12f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Quantum Shield Logo Emblem
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                cyberColors.neonAccent.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                    )
                    .border(1.5.dp, cyberColors.neonAccent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "AlphaLink Security Core",
                    tint = cyberColors.neonAccent,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ALPHALINK",
                color = cyberColors.textPrimary,
                fontSize = 28.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 3.sp
            )

            Text(
                text = "ZERO-TRUST DECENTRALIZED MESH",
                color = cyberColors.neonAccent,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 3D Command Panel Holographic Card
            HolographicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
            ) {
                AnimatedContent(
                    targetState = currentStep,
                    label = "AuthStepTransition"
                ) { step ->
                    when (step) {
                        AuthStep.INPUT_METHOD -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "SECURE ACCESS GATEWAY",
                                    color = cyberColors.textPrimary,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = "Connect with cryptographic peer authorization or OpenID Connect identity provider.",
                                    color = cyberColors.textSecondary,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Continue with Google
                                CyberButton(
                                    text = "Continue with Google",
                                    onClick = { repository.continueWithGoogle() },
                                    isPrimary = false,
                                    isLoading = isSubmitting,
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.AccountCircle,
                                            contentDescription = "Google Auth",
                                            tint = cyberColors.neonAccent,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    testTag = "google_login_button"
                                )

                                // Continue with Mobile Number
                                CyberButton(
                                    text = "Continue with Mobile Number",
                                    onClick = { currentStep = AuthStep.PHONE_INPUT },
                                    isPrimary = true,
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.PhoneAndroid,
                                            contentDescription = "Phone Auth",
                                            tint = Color.Black,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    testTag = "phone_login_button"
                                )

                                // Direct Quick Demo Access
                                TextButton(
                                    onClick = { repository.continueWithGoogle() },
                                    modifier = Modifier.testTag("quick_access_button")
                                ) {
                                    Text(
                                        text = "⚡ Bypass with Operator Key (Demo)",
                                        color = cyberColors.secondaryAccent,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        AuthStep.PHONE_INPUT -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "TELECOMMUNICATIONS AUTH",
                                    color = cyberColors.textPrimary,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Enter an international phone number. An eight-digit cryptographic challenge will be dispatched.",
                                    color = cyberColors.textSecondary,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )

                                // Country selector & Phone input row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Country code pill
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(cyberColors.cyberSurface)
                                            .border(1.dp, cyberColors.glassBorder, RoundedCornerShape(10.dp))
                                            .clickable { showCountryPicker = !showCountryPicker }
                                            .padding(horizontal = 12.dp, vertical = 14.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = countryCode,
                                                color = cyberColors.neonAccent,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = "Select country code",
                                                tint = cyberColors.neonAccent,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Phone Number TextField
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(cyberColors.cyberSurface)
                                            .border(1.dp, cyberColors.glassBorder, RoundedCornerShape(10.dp))
                                            .padding(horizontal = 14.dp, vertical = 14.dp)
                                    ) {
                                        BasicTextField(
                                            value = authPhone,
                                            onValueChange = { repository.setAuthPhone(it) },
                                            textStyle = LocalTextStyle.current.copy(
                                                color = cyberColors.textPrimary,
                                                fontSize = 16.sp,
                                                fontFamily = FontFamily.Monospace
                                            ),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                            singleLine = true,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("phone_input_field"),
                                            decorationBox = { innerTextField ->
                                                if (authPhone.isEmpty()) {
                                                    Text(
                                                        text = "555 019 924",
                                                        color = cyberColors.textSecondary.copy(alpha = 0.5f),
                                                        fontSize = 14.sp,
                                                        fontFamily = FontFamily.Monospace
                                                    )
                                                }
                                                innerTextField()
                                            }
                                        )
                                    }
                                }

                                if (showCountryPicker) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(cyberColors.cyberGraphite)
                                            .border(1.dp, cyberColors.glassBorder, RoundedCornerShape(10.dp))
                                            .padding(8.dp)
                                    ) {
                                        countryCodes.forEach { (code, country) ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .clickable {
                                                        repository.setAuthCountryCode(code)
                                                        showCountryPicker = false
                                                    }
                                                    .padding(8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(country, color = cyberColors.textPrimary, fontSize = 12.sp)
                                                Text(code, color = cyberColors.neonAccent, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }

                                CyberButton(
                                    text = "Send 8-Digit OTP Code",
                                    onClick = {
                                        repository.sendVerificationCode()
                                        currentStep = AuthStep.OTP_VERIFICATION
                                    },
                                    isLoading = isSubmitting,
                                    modifier = Modifier.fillMaxWidth(),
                                    testTag = "send_otp_button"
                                )

                                TextButton(
                                    onClick = { currentStep = AuthStep.INPUT_METHOD }
                                ) {
                                    Text(
                                        text = "← Back to Options",
                                        color = cyberColors.textSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        AuthStep.OTP_VERIFICATION -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "CHALLENGE VERIFICATION",
                                    color = cyberColors.textPrimary,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Dispatched 8-digit OTP code to $countryCode $authPhone",
                                    color = cyberColors.neonAccent,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    textAlign = TextAlign.Center
                                )

                                // Eight Separate Glowing Input Cells
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    for (i in 0 until 8) {
                                        val char = otpCode.getOrNull(i)?.toString() ?: ""
                                        val isCurrentActive = otpCode.length == i
                                        Box(
                                            modifier = Modifier
                                                .size(width = 34.dp, height = 44.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (char.isNotEmpty()) cyberColors.cyberSurface else cyberColors.cyberGraphite
                                                )
                                                .border(
                                                    1.5.dp,
                                                    if (isCurrentActive) cyberColors.neonAccent
                                                    else if (char.isNotEmpty()) cyberColors.secondaryAccent.copy(alpha = 0.8f)
                                                    else cyberColors.glassBorder,
                                                    RoundedCornerShape(8.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = char,
                                                color = cyberColors.neonAccent,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp
                                            )
                                        }
                                    }
                                }

                                // Hidden input for capturing 8 digits
                                BasicTextField(
                                    value = otpCode,
                                    onValueChange = { repository.setOtpCode(it) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .testTag("otp_code_input")
                                )

                                // Auto SMS Detection Simulation Chip
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(cyberColors.cyberGraphite)
                                        .border(0.8.dp, cyberColors.secondaryAccent.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                        .clickable { repository.setOtpCode("72940183") }
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sms,
                                        contentDescription = "Simulate SMS Autofill",
                                        tint = cyberColors.secondaryAccent,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Auto-detect SMS: 72940183",
                                        color = cyberColors.secondaryAccent,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                if (authError != null) {
                                    Text(
                                        text = authError ?: "",
                                        color = cyberColors.alertRed,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                CyberButton(
                                    text = "Verify Code & Access",
                                    onClick = { repository.verifyCodeAndLogin() },
                                    isLoading = isSubmitting,
                                    modifier = Modifier.fillMaxWidth(),
                                    testTag = "verify_otp_button"
                                )

                                // Resend timer & Change number
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        onClick = {
                                            if (resendTimer == 0) repository.sendVerificationCode()
                                        },
                                        enabled = resendTimer == 0
                                    ) {
                                        Text(
                                            text = if (resendTimer > 0) "Resend code in ${resendTimer}s" else "Resend code",
                                            color = if (resendTimer > 0) cyberColors.textSecondary else cyberColors.neonAccent,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    TextButton(
                                        onClick = {
                                            currentStep = AuthStep.PHONE_INPUT
                                            repository.setOtpCode("")
                                        }
                                    ) {
                                        Text(
                                            text = "Change Number",
                                            color = cyberColors.secondaryAccent,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Security compliance indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Encrypted session",
                    tint = cyberColors.neonAccent.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Kyber-768 Post-Quantum Key Transport",
                    color = cyberColors.textSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

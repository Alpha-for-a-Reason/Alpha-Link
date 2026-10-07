package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.SecurityPreferences
import com.example.data.repository.AlphaLinkRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("AlphaLink", appName)
  }

  @Test
  fun `repository initialization and messaging`() {
    val repo = AlphaLinkRepository()
    val initialChats = repo.chats.value
    assertTrue("Chats list should not be empty", initialChats.isNotEmpty())

    val firstChat = initialChats.first()
    repo.sendMessage(firstChat.id, "Test encrypted ping")

    val messages = repo.messages.value[firstChat.id]
    assertNotNull(messages)
    assertTrue(messages!!.any { it.text == "Test encrypted ping" })
  }

  @Test
  fun `biometric lock and unlock lifecycle`() {
    val repo = AlphaLinkRepository()
    assertFalse(repo.biometricLockEnabled.value)
    assertFalse(repo.isAppLocked.value)

    repo.setBiometricLock(true)
    assertTrue(repo.biometricLockEnabled.value)

    repo.lockApp()
    assertTrue(repo.isAppLocked.value)

    repo.unlockApp()
    assertFalse(repo.isAppLocked.value)

    // Disabling biometric lock should also unlock
    repo.lockApp()
    assertTrue(repo.isAppLocked.value)
    repo.setBiometricLock(false)
    assertFalse(repo.biometricLockEnabled.value)
    assertFalse(repo.isAppLocked.value)
  }

  @Test
  fun `datastore preferences persistence for biometric unlock`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefs = SecurityPreferences(context)

    prefs.setRequireBiometricUnlock(true)
    val enabled = prefs.requireBiometricUnlockFlow.first()
    assertTrue("Require biometric unlock should persist true in DataStore", enabled)

    prefs.setRequireBiometricUnlock(false)
    val disabled = prefs.requireBiometricUnlockFlow.first()
    assertFalse("Require biometric unlock should persist false in DataStore", disabled)
  }
}

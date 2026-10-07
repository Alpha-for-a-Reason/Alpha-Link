package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.securityDataStore: DataStore<Preferences> by preferencesDataStore(name = "security_settings")

class SecurityPreferences(private val context: Context) {

    companion object {
        val KEY_REQUIRE_BIOMETRIC_UNLOCK = booleanPreferencesKey("require_biometric_unlock")
        val KEY_TWO_STEP_VERIFICATION = booleanPreferencesKey("require_two_step_verification")
    }

    val requireBiometricUnlockFlow: Flow<Boolean> = context.securityDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[KEY_REQUIRE_BIOMETRIC_UNLOCK] ?: false
        }

    val twoStepVerificationFlow: Flow<Boolean> = context.securityDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[KEY_TWO_STEP_VERIFICATION] ?: true
        }

    suspend fun setRequireBiometricUnlock(enabled: Boolean) {
        context.securityDataStore.edit { preferences ->
            preferences[KEY_REQUIRE_BIOMETRIC_UNLOCK] = enabled
        }
    }

    suspend fun setTwoStepVerification(enabled: Boolean) {
        context.securityDataStore.edit { preferences ->
            preferences[KEY_TWO_STEP_VERIFICATION] = enabled
        }
    }
}

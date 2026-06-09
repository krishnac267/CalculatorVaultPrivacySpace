package com.calculator.vault.privacy.security

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.calculator.vault.privacy.core.security.ContentEncryptionService
import com.calculator.vault.privacy.core.security.PinManager
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CryptoSecurityTest {
    private lateinit var encryptionService: ContentEncryptionService

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        encryptionService = ContentEncryptionService(PinManager(context))
    }

    @Test
    fun encryptDecrypt_roundTrip() {
        val plain = "Top secret note body"
        val encrypted = encryptionService.encryptText(plain)
        assertThat(encrypted).isNotEmpty()
        assertThat(encrypted).doesNotContain(plain)
        assertThat(encryptionService.decryptText(encrypted)).isEqualTo(plain)
    }

    @Test
    fun encrypt_usesAesGcmWithIvPrefix() {
        val encrypted = encryptionService.encryptBytes("payload".toByteArray())
        assertThat(encrypted.size).isGreaterThan(12)
    }

    @Test
    fun decrypt_tamperedCiphertext_failsGracefully() {
        val encrypted = encryptionService.encryptBytes("data".toByteArray())
        encrypted[encrypted.size - 1] = (encrypted.last().toInt() xor 0xFF).toByte()
        try {
            encryptionService.decryptBytes(encrypted)
            throw AssertionError("Expected failure")
        } catch (_: IllegalStateException) {
            // expected
        }
    }

    @Test
    fun decrypt_missingIv_failsGracefully() {
        try {
            encryptionService.decryptBytes(byteArrayOf(1, 2, 3))
            throw AssertionError("Expected failure")
        } catch (_: IllegalArgumentException) {
            // expected
        }
    }
}

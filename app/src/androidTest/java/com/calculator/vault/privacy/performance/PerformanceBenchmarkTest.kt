package com.calculator.vault.privacy.performance

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.calculator.vault.privacy.core.security.ContentEncryptionService
import com.calculator.vault.privacy.core.security.PinManager
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PerformanceBenchmarkTest {
    @Test
    fun noteEncryption_under50ms() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val service = ContentEncryptionService(PinManager(context))
        val payload = "Benchmark note ".repeat(20)
        val start = System.nanoTime()
        repeat(10) {
            service.encryptText(payload)
        }
        val avgMs = (System.nanoTime() - start) / 10 / 1_000_000
        assertThat(avgMs).isLessThan(50L)
    }

    @Test
    fun noteDecryption_under50ms() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val service = ContentEncryptionService(PinManager(context))
        val encrypted = service.encryptText("Benchmark decrypt payload")
        val start = System.nanoTime()
        repeat(10) {
            service.decryptText(encrypted)
        }
        val avgMs = (System.nanoTime() - start) / 10 / 1_000_000
        assertThat(avgMs).isLessThan(50L)
    }
}

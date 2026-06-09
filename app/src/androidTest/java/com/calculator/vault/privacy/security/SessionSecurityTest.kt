package com.calculator.vault.privacy.security

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.calculator.vault.privacy.core.session.SessionManager
import com.calculator.vault.privacy.data.storage.FileStorageManager
import com.calculator.vault.privacy.domain.model.SessionState
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SessionSecurityTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var fileStorageManager: FileStorageManager

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun lock_clearsPreviewCache() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val previewDir = File(context.cacheDir, "vault_preview")
        previewDir.mkdirs()
        File(previewDir, "stale.bin").writeText("decrypted-preview")

        sessionManager.unlock(SessionState.REAL_VAULT)
        sessionManager.lock()

        assertThat(File(previewDir, "stale.bin").exists()).isFalse()
    }
}

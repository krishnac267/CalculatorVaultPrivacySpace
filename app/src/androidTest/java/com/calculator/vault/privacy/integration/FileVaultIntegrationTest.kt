package com.calculator.vault.privacy.integration

import android.net.Uri
import androidx.core.content.FileProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.calculator.vault.privacy.core.session.SessionManager
import com.calculator.vault.privacy.data.database.dao.VaultFileDao
import com.calculator.vault.privacy.domain.interfaces.FileRepository
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
class FileVaultIntegrationTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject lateinit var fileRepository: FileRepository
    @Inject lateinit var vaultFileDao: VaultFileDao
    @Inject lateinit var sessionManager: SessionManager

    @Before
    fun setUp() {
        hiltRule.inject()
        sessionManager.unlock(SessionState.REAL_VAULT)
    }

    @Test
    fun importFile_storesEncryptedCopyWithoutSourcePath() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val source = File(context.cacheDir, "source.txt")
        source.writeText("integration test payload")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            source,
        )

        val imported = fileRepository.importFile(uri, "test-doc.txt", "text/plain")
        val entity = vaultFileDao.findById(imported.id, 0)

        assertThat(entity).isNotNull()
        assertThat(entity!!.vaultPath).isEmpty()
        assertThat(entity.internalFileName).isNotEmpty()
        assertThat(entity.internalFileName.endsWith(".enc")).isTrue()
        assertThat(File(context.filesDir, "vault/${entity.internalFileName}").exists()).isTrue()
    }
}

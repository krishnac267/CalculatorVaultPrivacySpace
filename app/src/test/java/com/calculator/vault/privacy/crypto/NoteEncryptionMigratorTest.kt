package com.calculator.vault.privacy.crypto

import com.calculator.vault.privacy.data.migration.NoteEncryptionMigrator
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class NoteEncryptionMigratorTest {
    @Test
    fun buildSearchText_includesTitleAndContentLowercase() {
        val text = NoteEncryptionMigrator.buildSearchText("My Title", "Secret Words")
        assertThat(text).isEqualTo("my title secret words")
    }

    @Test
    fun buildSearchText_handlesNullContent() {
        val text = NoteEncryptionMigrator.buildSearchText("Only Title", null)
        assertThat(text).isEqualTo("only title")
    }
}

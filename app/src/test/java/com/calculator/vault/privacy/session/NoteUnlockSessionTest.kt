package com.calculator.vault.privacy.session

import com.calculator.vault.privacy.core.session.NoteUnlockSession
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class NoteUnlockSessionTest {
    @Test
    fun unlock_tracksNoteUntilLockAll() {
        val session = NoteUnlockSession()
        assertThat(session.isUnlocked(42L)).isFalse()
        session.unlock(42L)
        assertThat(session.isUnlocked(42L)).isTrue()
        session.lockAll()
        assertThat(session.isUnlocked(42L)).isFalse()
    }
}

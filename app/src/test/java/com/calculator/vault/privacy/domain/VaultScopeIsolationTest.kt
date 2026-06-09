package com.calculator.vault.privacy.domain

import com.calculator.vault.privacy.domain.model.SessionState
import com.calculator.vault.privacy.domain.model.VaultScope
import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class VaultScopeIsolationTest {
    @Test
    fun realSession_mapsToRealScope() {
        assertThat(VaultScope.fromSession(SessionState.REAL_VAULT)).isEqualTo(VaultScope.REAL)
    }

    @Test
    fun fakeSession_mapsToFakeScope() {
        assertThat(VaultScope.fromSession(SessionState.FAKE_VAULT)).isEqualTo(VaultScope.FAKE)
    }

    @Test
    fun lockedSession_mapsToRealScope() {
        assertThat(VaultScope.fromSession(SessionState.LOCKED)).isEqualTo(VaultScope.REAL)
    }
}

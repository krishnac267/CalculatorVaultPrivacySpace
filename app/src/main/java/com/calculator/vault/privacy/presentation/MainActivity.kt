package com.calculator.vault.privacy.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.calculator.vault.privacy.core.security.IntruderCaptureCoordinator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.calculator.vault.privacy.data.migration.NoteEncryptionMigrator
import com.calculator.vault.privacy.domain.interfaces.SecurityRepository
import com.calculator.vault.privacy.domain.model.SessionState
import com.calculator.vault.privacy.domain.usecases.CheckSessionExpiredUseCase
import com.calculator.vault.privacy.domain.usecases.LockSessionUseCase
import com.calculator.vault.privacy.domain.usecases.RefreshSessionUseCase
import com.calculator.vault.privacy.presentation.compose.theme.PrivacySpaceTheme
import com.calculator.vault.privacy.presentation.navigation.PrivacyNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject lateinit var lockSessionUseCase: LockSessionUseCase
    @Inject lateinit var checkSessionExpiredUseCase: CheckSessionExpiredUseCase
    @Inject lateinit var refreshSessionUseCase: RefreshSessionUseCase
    @Inject lateinit var securityRepository: SecurityRepository
    @Inject lateinit var intruderCaptureCoordinator: IntruderCaptureCoordinator
    @Inject lateinit var noteEncryptionMigrator: NoteEncryptionMigrator

    private var sessionLocked by mutableStateOf(false)

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        intruderCaptureCoordinator.hasCameraPermission = { granted }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch(Dispatchers.IO) {
            noteEncryptionMigrator.migrateIfNeeded()
        }
        intruderCaptureCoordinator.lifecycleOwner = this
        intruderCaptureCoordinator.hasCameraPermission = {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        }
        if (securityRepository.isIntruderCaptureEnabled()) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        setContent {
            PrivacySpaceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    PrivacyNavHost(
                        sessionLocked = sessionLocked,
                        onSessionLockAcknowledged = { sessionLocked = false },
                        onUserInteraction = { refreshSessionUseCase.execute() },
                    )
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (securityRepository.getSessionState() != SessionState.LOCKED) {
            lockSessionUseCase.execute()
            sessionLocked = true
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkSessionExpiredUseCase.execute()) {
            lockSessionUseCase.execute()
            sessionLocked = true
        }
    }
}

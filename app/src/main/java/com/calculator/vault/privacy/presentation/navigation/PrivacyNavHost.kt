package com.calculator.vault.privacy.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.calculator.vault.privacy.presentation.components.BiometricAuthHelper
import com.calculator.vault.privacy.presentation.components.SecureScreenEffect
import com.calculator.vault.privacy.presentation.components.rememberHapticClick
import com.calculator.vault.privacy.presentation.screens.AppsScreen
import com.calculator.vault.privacy.presentation.screens.CalculatorScreen
import com.calculator.vault.privacy.presentation.screens.DashboardScreen
import com.calculator.vault.privacy.presentation.screens.FilePreviewDialog
import com.calculator.vault.privacy.presentation.screens.FilesScreen
import com.calculator.vault.privacy.presentation.screens.NoteEditorScreen
import com.calculator.vault.privacy.presentation.screens.NotesScreen
import com.calculator.vault.privacy.presentation.screens.NotificationsScreen
import com.calculator.vault.privacy.presentation.screens.SecurityCenterScreen
import com.calculator.vault.privacy.presentation.screens.SettingsScreen
import com.calculator.vault.privacy.presentation.screens.SetupScreen
import com.calculator.vault.privacy.presentation.testing.TestTags
import com.calculator.vault.privacy.presentation.viewmodels.AppsEvent
import com.calculator.vault.privacy.presentation.viewmodels.AppsViewModel
import com.calculator.vault.privacy.presentation.viewmodels.CalculatorEvent
import com.calculator.vault.privacy.presentation.viewmodels.CalculatorViewModel
import com.calculator.vault.privacy.presentation.viewmodels.DashboardViewModel
import com.calculator.vault.privacy.presentation.viewmodels.FilePreviewState
import com.calculator.vault.privacy.presentation.viewmodels.FilesViewModel
import com.calculator.vault.privacy.presentation.viewmodels.NoteEditorEvent
import com.calculator.vault.privacy.presentation.viewmodels.NoteEditorViewModel
import com.calculator.vault.privacy.presentation.viewmodels.NotificationsViewModel
import com.calculator.vault.privacy.presentation.viewmodels.NotesViewModel
import com.calculator.vault.privacy.presentation.viewmodels.SecurityCenterEvent
import com.calculator.vault.privacy.presentation.viewmodels.SecurityCenterViewModel
import com.calculator.vault.privacy.presentation.viewmodels.SettingsEvent
import com.calculator.vault.privacy.presentation.viewmodels.SettingsViewModel
import com.calculator.vault.privacy.presentation.viewmodels.SetupViewModel

@Composable
fun PrivacyNavHost(
    sessionLocked: Boolean,
    onSessionLockAcknowledged: () -> Unit,
    onUserInteraction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val activity = LocalContext.current as FragmentActivity

    LaunchedEffect(sessionLocked) {
        if (sessionLocked) {
            navController.navigate(Routes.CALCULATOR) {
                popUpTo(0) { inclusive = true }
            }
            onSessionLockAcknowledged()
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.CALCULATOR,
        modifier = modifier,
    ) {
        composable(Routes.CALCULATOR) {
            val viewModel: CalculatorViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            if (uiState.setupComplete) {
                SecureScreenEffect()
            }
            val biometricStatus = BiometricAuthHelper.canAuthenticate(activity)

            LaunchedEffect(biometricStatus, uiState.biometricEnabled) {
                viewModel.onBiometricAvailabilityChecked(biometricStatus)
            }
            LaunchedEffect(Unit) {
                viewModel.refreshSetupState()
            }
            LaunchedEffect(uiState.setupComplete) {
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                if (!uiState.setupComplete) {
                    if (currentRoute != Routes.SETUP) {
                        navController.navigate(Routes.SETUP) {
                            launchSingleTop = true
                        }
                    }
                } else if (currentRoute == Routes.SETUP) {
                    navController.navigate(Routes.CALCULATOR) {
                        popUpTo(Routes.CALCULATOR) { inclusive = true }
                    }
                }
            }
            LaunchedEffect(viewModel) {
                viewModel.events.collect { event ->
                    when (event) {
                        CalculatorEvent.NavigateSetup -> navController.navigate(Routes.SETUP)
                        CalculatorEvent.NavigateVault -> {
                            navController.navigate(Routes.VAULT) {
                                popUpTo(Routes.CALCULATOR) { inclusive = false }
                            }
                        }
                    }
                }
            }
            CalculatorScreen(
                uiState = uiState,
                onDigit = viewModel::onDigit,
                onDecimal = viewModel::onDecimal,
                onOperator = viewModel::onOperator,
                onPercent = viewModel::onPercent,
                onSquareRoot = viewModel::onSquareRoot,
                onPower = viewModel::onPower,
                onClear = viewModel::onClear,
                onBackspace = viewModel::onBackspace,
                onEquals = viewModel::onEquals,
                onToggleScientific = viewModel::onToggleScientific,
                onBiometricClick = { viewModel.requestBiometricUnlock(activity) },
            )
        }
        composable(Routes.SETUP) {
            val viewModel: SetupViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(viewModel) {
                viewModel.events.collect {
                    navController.navigate(Routes.CALCULATOR) {
                        popUpTo(Routes.CALCULATOR) { inclusive = true }
                    }
                }
            }
            SetupScreen(
                uiState = uiState,
                onPinChange = viewModel::updatePin,
                onConfirmPinChange = viewModel::updateConfirmPin,
                onFakePinChange = viewModel::updateFakePin,
                onBiometricChange = viewModel::setBiometricEnabled,
                onNext = viewModel::nextStep,
            )
        }
        composable(Routes.VAULT) {
            SecureScreenEffect()
            VaultShell(
                onUserInteraction = onUserInteraction,
                onLocked = {
                    navController.navigate(Routes.CALCULATOR) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
    }
}

@Composable
private fun VaultShell(
    onUserInteraction: () -> Unit,
    onLocked: () -> Unit,
) {
    val navController = rememberNavController()
    val haptic = rememberHapticClick()
    val items = listOf(
        BottomTab(Routes.HOME, "Home", Icons.Outlined.Home, TestTags.VAULT_TAB_HOME),
        BottomTab(Routes.APPS, "Apps", Icons.Outlined.Apps, TestTags.VAULT_TAB_APPS),
        BottomTab(Routes.NOTES, "Notes", Icons.Outlined.Description, TestTags.VAULT_TAB_NOTES),
        BottomTab(Routes.FILES, "Files", Icons.Outlined.Folder, TestTags.VAULT_TAB_FILES),
        BottomTab(Routes.SETTINGS, "Settings", Icons.Outlined.Settings, TestTags.VAULT_TAB_SETTINGS),
    )
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route?.substringBefore("/") ?: Routes.HOME
    val hideBottomBar = currentRoute == Routes.NOTE_EDITOR.substringBefore("/")
            || currentRoute == Routes.SECURITY_CENTER

    Scaffold(
        bottomBar = {
            if (!hideBottomBar) {
                NavigationBar {
                    items.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = {
                                haptic()
                                onUserInteraction()
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    tab.icon,
                                    contentDescription = tab.label,
                                    modifier = Modifier.testTag(tab.testTag),
                                )
                            },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding),
        ) {
            composable(Routes.HOME) {
                val viewModel: DashboardViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                DashboardScreen(
                    uiState = uiState,
                    onRefresh = viewModel::refresh,
                    onNavigateApps = { navController.navigate(Routes.APPS) },
                    onNavigateNotes = { navController.navigate(Routes.NOTES) },
                    onNavigateFiles = { navController.navigate(Routes.FILES) },
                    onNavigateNotifications = { navController.navigate(Routes.NOTIFICATIONS) },
                    onNavigateSecurityCenter = { navController.navigate(Routes.SECURITY_CENTER) },
                )
            }
            composable(Routes.APPS) {
                val viewModel: AppsViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val activity = LocalContext.current as FragmentActivity
                val lifecycleOwner = LocalLifecycleOwner.current
                LaunchedEffect(viewModel) {
                    viewModel.events.collect { event ->
                        when (event) {
                            is AppsEvent.StartCloneSpaceSetup -> activity.startActivity(event.intent)
                        }
                    }
                }
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            viewModel.checkPendingCloneInstall()
                            viewModel.refresh()
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
                }
                AppsScreen(
                    uiState = uiState,
                    onQueryChange = viewModel::updateQuery,
                    onLaunchApp = viewModel::launchVaultApp,
                    onToggleFavorite = viewModel::toggleFavorite,
                    onShowPicker = viewModel::showPicker,
                    onLaunchInstalled = viewModel::launchInstalledApp,
                    onCloneInstalled = { app -> viewModel.cloneInstalledApp(activity, app) },
                    onEnableCloneSpace = viewModel::enableCloneSpace,
                    onDismissMessage = viewModel::clearUserMessage,
                    onPickerQueryChange = viewModel::updatePickerQuery,
                )
            }
            composable(Routes.NOTES) {
                val viewModel: NotesViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            viewModel.refresh()
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
                }
                NotesScreen(
                    uiState = uiState,
                    onQueryChange = viewModel::updateQuery,
                    onCreateNote = { navController.navigate(Routes.noteEditor(0L)) },
                    onOpenNote = { id -> navController.navigate(Routes.noteEditor(id)) },
                )
            }
            composable(
                route = Routes.NOTE_EDITOR,
                arguments = listOf(navArgument("noteId") { type = NavType.StringType }),
            ) {
                val viewModel: NoteEditorViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                LaunchedEffect(viewModel) {
                    viewModel.events.collect { event ->
                        when (event) {
                            NoteEditorEvent.Saved, NoteEditorEvent.Deleted -> navController.popBackStack()
                        }
                    }
                }
                NoteEditorScreen(
                    uiState = uiState,
                    onTitleChange = viewModel::updateTitle,
                    onContentChange = viewModel::updateContent,
                    onSave = viewModel::save,
                    onDelete = viewModel::delete,
                    onToggleFavorite = viewModel::toggleFavorite,
                    onToggleLocked = viewModel::toggleLocked,
                    onUnlockPinChange = viewModel::updatePinInput,
                    onUnlockWithPin = viewModel::unlockWithPin,
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Routes.FILES) {
                val viewModel: FilesViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val preview by viewModel.preview.collectAsStateWithLifecycle()
                FilesScreen(
                    uiState = uiState,
                    onTabSelected = viewModel::selectTab,
                    onQueryChange = viewModel::updateQuery,
                    onImport = viewModel::importFile,
                    onDelete = viewModel::deleteFile,
                    onRestore = viewModel::restoreFile,
                    onPermanentDelete = viewModel::permanentlyDeleteFile,
                    onToggleFavorite = viewModel::toggleFavorite,
                    onToggleDeletedView = viewModel::toggleDeletedView,
                    onPreview = viewModel::requestPreview,
                )
                preview?.let { state ->
                    FilePreviewDialog(
                        fileName = state.name,
                        previewFile = state.file,
                        mimeType = state.mimeType,
                        onDismiss = viewModel::dismissPreview,
                    )
                }
            }
            composable(Routes.NOTIFICATIONS) {
                val viewModel: NotificationsViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                NotificationsScreen(uiState = uiState, onQueryChange = viewModel::updateQuery)
            }
            composable(Routes.SECURITY_CENTER) {
                val viewModel: SecurityCenterViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                LaunchedEffect(viewModel) {
                    viewModel.events.collect { event ->
                        if (event == SecurityCenterEvent.PanicCompleted) onLocked()
                    }
                }
                SecurityCenterScreen(
                    uiState = uiState,
                    onBiometricChange = viewModel::setBiometricEnabled,
                    onIntruderCaptureChange = viewModel::setIntruderCaptureEnabled,
                    onTimeoutChange = viewModel::setSessionTimeout,
                    onDeleteLog = viewModel::deleteIntruderLog,
                    onPanicLogout = viewModel::panicLogout,
                )
            }
            composable(Routes.SETTINGS) {
                val viewModel: SettingsViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                LaunchedEffect(viewModel) {
                    viewModel.events.collect { event ->
                        if (event == SettingsEvent.PanicCompleted) onLocked()
                    }
                }
                SettingsScreen(
                    uiState = uiState,
                    onNavigateSecurityCenter = { navController.navigate(Routes.SECURITY_CENTER) },
                    onBiometricChange = viewModel::setBiometricEnabled,
                    onTimeoutChange = viewModel::setSessionTimeout,
                    onPanicLogout = viewModel::panicLogout,
                )
            }
        }
    }
}

private data class BottomTab(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val testTag: String,
)

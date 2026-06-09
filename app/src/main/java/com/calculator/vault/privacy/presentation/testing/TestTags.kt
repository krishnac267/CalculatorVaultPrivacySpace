package com.calculator.vault.privacy.presentation.testing

/** Stable Compose test tags for E2E and UI automation. */
object TestTags {
    const val CALC_DISPLAY = "calc_display"
    const val SETUP_TITLE = "setup_title"
    const val SETUP_PIN = "setup_pin"
    const val SETUP_CONFIRM_PIN = "setup_confirm_pin"
    const val SETUP_FAKE_PIN = "setup_fake_pin"
    const val SETUP_NEXT = "setup_next"
    const val SETUP_BIOMETRIC = "setup_biometric"
    const val VAULT_HOME = "vault_home"
    const val VAULT_TAB_HOME = "vault_tab_home"
    const val VAULT_TAB_APPS = "vault_tab_apps"
    const val VAULT_TAB_NOTES = "vault_tab_notes"
    const val VAULT_TAB_FILES = "vault_tab_files"
    const val VAULT_TAB_SETTINGS = "vault_tab_settings"
    const val NOTES_CREATE = "notes_create"
    const val NOTES_SEARCH = "notes_search"
    const val NOTE_TITLE = "note_title"
    const val NOTE_CONTENT = "note_content"
    const val NOTE_SAVE = "note_save"
    const val NOTE_LOCK = "note_lock"
    const val FILES_IMPORT = "files_import"
    const val FILES_SEARCH = "files_search"
    const val PIN_UNLOCK_DIALOG = "pin_unlock_dialog"
    const val PIN_UNLOCK_INPUT = "pin_unlock_input"

    fun calcKey(label: String): String = "calc_key_$label"
}

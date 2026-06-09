package com.calculator.vault.privacy.presentation.navigation

object Routes {
    const val CALCULATOR = "calculator"
    const val SETUP = "setup"
    const val VAULT = "vault"
    const val HOME = "home"
    const val APPS = "apps"
    const val NOTES = "notes"
    const val NOTE_EDITOR = "note_editor/{noteId}"
    const val FILES = "files"
    const val NOTIFICATIONS = "notifications"
    const val SECURITY_CENTER = "security_center"
    const val SETTINGS = "settings"

    fun noteEditor(noteId: Long = 0L) = "note_editor/$noteId"
}

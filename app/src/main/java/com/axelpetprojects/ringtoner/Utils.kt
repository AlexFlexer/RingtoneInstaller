package com.axelpetprojects.ringtoner

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes

val ViewGroup.layoutInflater: LayoutInflater
    get() = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

fun Context.openAppSettings(shouldOpenAsNewTask: Boolean = true) =
    startActivityOrFallback(prepareIntentForDetailSettings(packageName, shouldOpenAsNewTask)) {
        notifyAboutNoSettings()
    }

@RequiresApi(Build.VERSION_CODES.M)
fun Context.openChangeSettingsActivity() =
    startActivityOrFallback(Intent().also {
        it.action = Settings.ACTION_MANAGE_WRITE_SETTINGS
        it.data = Uri.parse("package:" + packageName.orEmpty())
    }) { openAppSettings() }

fun Context.startActivityOrFallback(intent: Intent, fallback: () -> Unit = { notifyAboutNoApp() }) =
    tryOrGiveUp({ startActivity(intent) }, { fallback() })

/**
 * Tries to perform some [actionToTry], and if it throws an exception, returns [defaultVal].
 */
inline fun <T> tryOrGiveUp(actionToTry: () -> T, defaultVal: () -> T): T =
    try {
        actionToTry()
    } catch (t: Exception) {
        defaultVal()
    }

private fun prepareIntentForDetailSettings(
    packageName: String,
    shouldOpenAsNewTask: Boolean
): Intent {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    if (shouldOpenAsNewTask) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    return intent
}

private fun Context.notifyAboutNoApp() = showNoAppDialog(R.string.no_app_for_action)

private fun Context.notifyAboutNoSettings() = showNoAppDialog(R.string.no_app_for_settings)

private fun Context.showNoAppDialog(@StringRes message: Int) {
    createAlertDialogWithMessage(
        this,
        R.string.error,
        message,
        R.string.ok
    ).show()
}
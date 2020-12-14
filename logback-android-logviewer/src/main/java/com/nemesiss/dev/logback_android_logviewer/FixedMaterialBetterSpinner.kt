package com.nemesiss.dev.logback_android_logviewer

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner

fun MaterialBetterSpinner.setPopup(popup: Boolean) {
    val isPopupField = MaterialBetterSpinner::class.java.getDeclaredField("isPopup")
    isPopupField.isAccessible = true
    isPopupField.set(this, popup)
}
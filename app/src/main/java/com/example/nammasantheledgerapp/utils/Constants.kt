package com.example.nammasantheledgerapp.utils

object Constants {
    const val DATABASE_NAME = "namma_santhe_database"
    const val PREF_NAME = "namma_santhe_prefs"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    const val KEY_VENDOR_NAME = "vendor_name"
    const val KEY_SHOP_NAME = "shop_name"
    const val KEY_USER_PIN = "user_pin"
    const val KEY_DARK_MODE = "dark_mode"
    const val KEY_LARGE_TEXT = "large_text"

    val QUICK_AMOUNTS = listOf(100, 200, 500, 1000, 2000, 5000)
    const val DEFAULT_TRUST_SCORE = 0.7f
    const val HIGH_RISK_THRESHOLD = 0.3f
    const val TRUSTED_THRESHOLD = 0.7f

    const val WHATSAPP_PACKAGE_NAME = "com.whatsapp"
    const val WHATSAPP_BUSINESS_PACKAGE = "com.whatsapp.w4b"

    const val REQUEST_CAMERA_PERMISSION = 1001
    const val REQUEST_STORAGE_PERMISSION = 1002
}
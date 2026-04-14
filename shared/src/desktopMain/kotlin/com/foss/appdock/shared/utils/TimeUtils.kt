package com.foss.appdock.shared.utils

import java.util.Calendar

actual fun getSystemTimeMillis(): Long = System.currentTimeMillis()

actual fun getCurrentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)

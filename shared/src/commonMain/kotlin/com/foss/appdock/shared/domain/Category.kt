package com.foss.appdock.shared.domain

import kotlinx.serialization.Serializable

@Serializable data class Category(val id: Long = 0, val name: String, val sortOrder: Int = 0)

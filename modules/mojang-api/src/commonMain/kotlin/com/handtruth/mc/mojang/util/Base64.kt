package com.handtruth.mc.mojang.util

import io.ktor.util.*
import io.ktor.utils.io.bits.*
import io.ktor.utils.io.core.*

@OptIn(InternalAPI::class)
fun decodeBase64AsString(value: String): String = value.decodeBase64String()

package com.handtruth.mc.minecraft.util

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

internal val json = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true, encodeDefaults = false))

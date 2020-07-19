package com.handtruth.mc.mojang.util

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

internal val json = Json(JsonConfiguration.Stable.copy(ignoreUnknownKeys = true, encodeDefaults = false))

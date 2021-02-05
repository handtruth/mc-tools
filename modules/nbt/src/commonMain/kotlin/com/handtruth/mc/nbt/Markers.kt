package com.handtruth.mc.nbt

import kotlinx.serialization.SerialInfo

@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class NBTListType(val id: Int)

@DslMarker
annotation class NBTDsl

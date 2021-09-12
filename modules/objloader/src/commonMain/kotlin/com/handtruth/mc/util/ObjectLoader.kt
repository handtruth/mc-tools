package com.handtruth.mc.util

import kotlin.reflect.KClass

/**
 * Loads Kotlin objects and returns them as [List].
 *
 * This function has different implementation that depends on execution platform.
 *
 * On Android and JVM this function loads objects in [java.util.ServiceLoader] style,
 * but it does not instantiate loaded classes. It tries to get object instance instead.
 * This function uses classloader that loaded class [klass].
 *
 * @param klass object's superclass
 * @param T object's supertype
 * @return list of loaded objects
 */
public expect fun <T : Any> loadObjects(klass: KClass<out T>): List<T>

/**
 * Loads Kotlin objects and returns them as [List].
 *
 * This function has different implementation that depends on execution platform.
 *
 * On Android and JVM this function loads objects in [java.util.ServiceLoader] style,
 * but it does not instantiate loaded classes. It tries to get object instance instead.
 * This function uses classloader that loaded class of type [T].
 *
 * @param T object's supertype
 * @return list of loaded objects
 */
public inline fun <reified T : Any> loadObjects(): List<T> = loadObjects(T::class)

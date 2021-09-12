package com.handtruth.mc.util

import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

public actual fun <T : Any> loadObjects(klass: KClass<out T>): List<T> = loadObjects(klass, klass.java.classLoader)

/**
 * Loads Kotlin objects and returns them as [List].
 *
 * This function has different implementation that depends on execution platform.
 *
 * On Android and JVM this function loads objects in [java.util.ServiceLoader] style,
 * but it does not instantiate loaded classes. It tries to get object instance instead.
 *
 * @param klass object's superclass
 * @param classLoader class loader that is used to load object's classes
 * @param T object's supertype
 * @return list of loaded objects
 */
public fun <T : Any> loadObjects(klass: KClass<out T>, classLoader: ClassLoader): List<T> {
    val resources = classLoader.getResources("META-INF/services/${klass.java.canonicalName}")!!
    return resources.asSequence().flatMap { resource ->
        resource.openStream().bufferedReader().readLines().map {
            val objectClass = classLoader.loadClass(it).kotlin
            check(objectClass.isSubclassOf(klass)) { "$objectClass is not a subclass of $klass" }
            @Suppress("UNCHECKED_CAST")
            objectClass.objectInstance as T
        }
    }.toList()
}

/**
 * Loads Kotlin objects and returns them as [List].
 *
 * This function has different implementation that depends on execution platform.
 *
 * On Android and JVM this function loads objects in [java.util.ServiceLoader] style,
 * but it does not instantiate loaded classes. It tries to get object instance instead.
 *
 * @param classLoader class loader that is used to load object's classes
 * @param T object's supertype
 * @return list of loaded objects
 */
public inline fun <reified T : Any> loadObjects(classLoader: ClassLoader): List<T> = loadObjects(T::class, classLoader)

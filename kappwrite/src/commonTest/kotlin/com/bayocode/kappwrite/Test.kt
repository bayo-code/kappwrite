package com.bayocode.kappwrite

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertTrue

@Serializable
data class CarWrapper<T>(
    val t: T
)

@Serializable
data class Car(
    val name: String
)

class CommonGreetingTest {

    private inline fun<reified T> serializeStuff(t: T) {
        val result = json.encodeToString(CarWrapper(t))
        println(result)

        println(json.decodeFromString<CarWrapper<T>>(result))
    }

    @Test
    fun testExample() {
        serializeStuff(Car(name = "Hello"))
    }
}
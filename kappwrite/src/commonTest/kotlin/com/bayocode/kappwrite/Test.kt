package com.bayocode.kappwrite

import kotlin.test.Test
import kotlin.test.assertTrue

class CommonGreetingTest {

    @Test
    fun testExample() {
        assertTrue(
            Greeting()
                .greet()
                .contains("Hello"),
            message = "Check 'Hello' is mentioned"
        )
    }
}
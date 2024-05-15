package com.bayocode.kappwrite

import kotlinx.datetime.Clock
import kotlin.random.Random

class ID {
    companion object {
        // Generate an hex ID based on timestamp
        // Recreated from https://www.php.net/manual/en/function.uniqid.php
        private fun hexTimestamp(): String {
            val now = Clock.System.now()
            val sec = now.epochSeconds
            val usec = (Clock.System.now().nanosecondsOfSecond / 1000) % 1000

            return sec.toString(16) + usec.toString(16).padStart(5, '0')
        }

        fun custom(id: String): String = id

        // Generate a unique ID with padding to have a longer ID
        fun unique(padding: Int = 7): String {
            val baseId = hexTimestamp()
            val randomPadding = (1..padding)
                .map { Random.nextInt(0, 16).toString(16) }
                .joinToString("")

            return baseId + randomPadding
        }
    }
}
package com.bayocode.kappwrite

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
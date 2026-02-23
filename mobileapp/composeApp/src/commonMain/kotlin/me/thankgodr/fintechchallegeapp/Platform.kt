package me.thankgodr.fintechchallegeapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
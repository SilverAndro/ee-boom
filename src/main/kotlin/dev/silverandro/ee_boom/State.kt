package dev.silverandro.ee_boom

enum class State(val message: String) {
    // Startup
    ACQUIRE_LOG("Getting log file"),
    // Loop
    WAITING("Waiting for update"),
    PARSING("Parsing lines"),
    PLAYING("Playing audio clip")
}
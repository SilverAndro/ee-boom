package dev.silverandro.ee_boom.log

import dev.silverandro.ee_boom.State
import dev.silverandro.ee_boom.audio.ClipPlayer
import kotlinx.coroutines.*
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.time.Instant
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object LogWatcher {
    val logFile = File("${System.getenv("LOCALAPPDATA")}/Warframe/EE.log")
    val scope = CoroutineScope(Dispatchers.IO)
    var watch = Instant.now().toEpochMilli()
    val fileBuffer = logFile.bufferedReader()
    var fragment: String?  = null
    var shouldPlay = false

    fun start(update: (State) -> Unit, ping: () -> Unit) {

        // Disruption: Boss Killed for artifact 2
        scope.launch {
            while (isActive) {
                delay(25.milliseconds)
                val new = logFile.lastModified()
                if (new > watch) {
                    watch = new
                    update.invoke(State.PARSING)
                    ping.invoke()

                    fragment = fileBuffer.readLine()
                    while (true) {
                        val next = fileBuffer.readLine()
                        if (next != null) {
                            processLine(fragment!!)
                            fragment = next
                        } else {
                            break
                        }
                    }
                }

                if (shouldPlay) {
                    update.invoke(State.PLAYING)
                    ClipPlayer.playClip()
                    shouldPlay = false
                }
                update.invoke(State.WAITING)
            }
        }
    }

    fun processLine(line: String) {
        if (
            line.contains("Disruption: Boss Killed for artifact") ||
            line.contains("Disruption: Completed defense for artifact")
        ) {
            shouldPlay = true
            println("FOUND LINE!")
        }
    }

    fun shutdown() {
        scope.cancel("Shutting down application")
        fileBuffer.close()
    }
}
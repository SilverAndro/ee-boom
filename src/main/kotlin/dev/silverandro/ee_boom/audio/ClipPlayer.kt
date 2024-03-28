package dev.silverandro.ee_boom.audio

import kotlinx.coroutines.*
import javax.sound.sampled.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds


object ClipPlayer : LineListener {
    val scope = CoroutineScope(Dispatchers.IO)
    private var donePlaying = false

    suspend fun playClip() {
        scope.launch {
            val inputStream = javaClass.classLoader.getResourceAsStream("vine_boom.wav")?.buffered()
            val audioStream = AudioSystem.getAudioInputStream(inputStream)
            val clip = AudioSystem.getClip()
            clip.open(audioStream)
            clip.addLineListener(this@ClipPlayer)
            clip.start()

            launch {
                delay(2.seconds)
                while (isActive && !donePlaying) {
                    delay(100.milliseconds)
                }
            }.join()

            donePlaying = false
            clip.close()
            audioStream.close()
        }.join()
    }

    override fun update(event: LineEvent) {
        if (LineEvent.Type.START == event.type) {
            println("playback start")
        } else if (LineEvent.Type.STOP == event.type) {
            println("playback stop")
            donePlaying = true
        }
    }
}
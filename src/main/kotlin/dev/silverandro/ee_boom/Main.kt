package dev.silverandro.ee_boom

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import dev.silverandro.ee_boom.log.LogWatcher
import java.time.Instant

@Composable
@Preview
fun App() {
    var applicationState by remember { mutableStateOf(State.ACQUIRE_LOG) }
    var averageUpdateRate by remember { mutableStateOf(0.0) }
    val updates = mutableListOf<Instant>()

    val updater: (State)->Unit = {
        if (it != applicationState) { println(it) }
        applicationState = it
    }

    val ping: ()->Unit = {
        updates.add(Instant.now())
        if (updates.size > 20) {
            updates.removeFirst()
        }
        val result = updates.mapIndexedNotNull { i, inst ->
            if (i+1 > updates.lastIndex) return@mapIndexedNotNull null
            return@mapIndexedNotNull updates[i + 1].toEpochMilli() - inst.toEpochMilli()
        }.average()
        averageUpdateRate = result
    }

    MaterialTheme {
        Column {
            Text(applicationState.message)
            Text("Mills between updates: $averageUpdateRate")
        }
    }

    LogWatcher.start(updater, ping)
}

fun main() = application {
    Window(
        title = "ee-boom",
        resizable = false,
        state = WindowState(width = 400.dp, height = 200.dp),
        onCloseRequest = { LogWatcher.shutdown(); exitApplication(); }
    ) {
        App()
    }
}

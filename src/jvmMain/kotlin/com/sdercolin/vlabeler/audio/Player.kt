package com.sdercolin.vlabeler.audio

import com.sdercolin.vlabeler.env.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import javax.sound.sampled.AudioSystem

class Player(
    private val coroutineScope: CoroutineScope,
    private val state: PlayerState
) {
    private var file: File? = null
    private val clip = AudioSystem.getClip()

    private var countingJob: Job? = null

    fun load(file: File) {
        Log.info("Player.load(${file.absolutePath}")
        if (this.file != null) {
            clip.flush()
            clip.close()
        }
        this.file = file
        AudioSystem.getAudioInputStream(file).use { clip.open(it) }
    }

    fun toggle() {
        file ?: return
        if (state.isPlaying) stop() else {
            reset()
            play()
        }
    }

    private fun play(untilPosition: Int? = null) {
        Log.info("Player.play()")
        countingJob = coroutineScope.launch {
            while (true) {
                delay(PlayingTimeInterval)
                state.changeFramePosition(clip.framePosition)
                if (!clip.isRunning) {
                    state.stopPlaying()
                    return@launch
                }
                if (untilPosition != null && state.framePosition >= untilPosition) {
                    stop()
                    return@launch
                }
            }
        }
        state.startPlaying()
        clip.start()
    }

    fun playSection(startPosition: Float, endPosition: Float) {
        file ?: return
        Log.info("Player.playSection($startPosition, $endPosition)")
        reset()
        clip.framePosition = startPosition.toInt()
        play(untilPosition = endPosition.toInt())
    }

    private fun stop() {
        Log.info("Player.stop()")
        clip.stop()
        countingJob?.cancel()
        countingJob = null
        state.stopPlaying()
    }

    private fun reset() {
        Log.info("Player.reset()")
        clip.close()
        AudioSystem.getAudioInputStream(file).use { clip.open(it) }
        clip.framePosition = 0
        state.changeFramePosition(0)
    }

    companion object {
        private const val PlayingTimeInterval = 50L
    }
}

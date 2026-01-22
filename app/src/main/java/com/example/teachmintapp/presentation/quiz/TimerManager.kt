package com.example.teachmintapp.presentation.quiz

import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerManager(
    private val scope: CoroutineScope,
    private val onTimerExpired: () -> Unit
) {
    private var timerJob: Job? = null
    private var startTime: Long = 0
    private var totalPausedDuration: Long = 0
    private var pauseStartTime: Long = 0
    private var isPaused: Boolean = false
    private var initialDuration: Long = 10_000L

    private val _remainingTime = MutableStateFlow(10_000L)
    val remainingTime: StateFlow<Long> = _remainingTime.asStateFlow()

    fun start(durationMillis: Long = 10_000L) {
        stop()
        initialDuration = durationMillis
        _remainingTime.value = durationMillis
        startTime = SystemClock.elapsedRealtime()
        totalPausedDuration = 0
        isPaused = false

        timerJob = scope.launch {
            while (true) {
                if (!isPaused) {
                    val currentTime = SystemClock.elapsedRealtime()
                    val elapsed = currentTime - startTime - totalPausedDuration
                    val remaining = (initialDuration - elapsed).coerceAtLeast(0)

                    if (remaining <= 0) {
                        _remainingTime.value = 0
                        onTimerExpired()
                        break
                    } else {
                        _remainingTime.value = remaining
                    }
                }
                delay(100)
            }
        }
    }

    fun pause() {
        if (!isPaused && timerJob?.isActive == true) {
            isPaused = true
            pauseStartTime = SystemClock.elapsedRealtime()
            // Calculate and save remaining time before stopping
            val currentTime = SystemClock.elapsedRealtime()
            val elapsed = currentTime - startTime - totalPausedDuration
            val remaining = (initialDuration - elapsed).coerceAtLeast(0)
            _remainingTime.value = remaining
            // Stop the timer job to save resources
            timerJob?.cancel()
            timerJob = null
        }
    }

    fun resume() {
        if (isPaused) {
            val remainingTime = _remainingTime.value
            if (remainingTime > 0) {
                // Restart timer with remaining time
                start(remainingTime)
            }
        }
    }

    fun stop() {
        timerJob?.cancel()
        timerJob = null
        isPaused = false
    }

    fun getRemainingTime(): Long = _remainingTime.value

    fun setRemainingTime(time: Long) {
        _remainingTime.value = time
        initialDuration = time
        startTime = SystemClock.elapsedRealtime()
        totalPausedDuration = 0
        isPaused = false
    }
}

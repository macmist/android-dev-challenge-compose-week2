/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.TimeUnit

class TimerViewModel : ViewModel() {
    var timer: MyTimer = MyTimer(
        100,
        100, {}, {}
    )
    private var timeString by mutableStateOf("")
    var time by mutableStateOf(0.toLong())
    private var _started = MutableLiveData(false)
    private var _paused = MutableLiveData(false)
    private var _h = MutableLiveData("00")
    private var _m = MutableLiveData("00")
    private var _s = MutableLiveData("00")
    private var _rgb = MutableLiveData(Color.Green)
    var started: LiveData<Boolean> = _started
    var paused: LiveData<Boolean> = _paused
    var hours: LiveData<String> = _h
    var minutes: LiveData<String> = _m
    var seconds: LiveData<String> = _s
    var color: LiveData<Color> = _rgb

    private fun newTimer(
        timeInMs: Long
    ) {
        timer.cancel()
        _h.value = MyTimer.getFullTimeString(TimeUnit.MILLISECONDS.toHours(timeInMs))
        _m.value = MyTimer.getFullTimeString(TimeUnit.MILLISECONDS.toMinutes(timeInMs), 60)
        _s.value = MyTimer.getFullTimeString(TimeUnit.MILLISECONDS.toSeconds(timeInMs), 60)
        _rgb.value = Color.Green
        timer = MyTimer(
            timeInMs,
            1000,
            onUpdate = { remaining ->
                _h.value = MyTimer.getFullTimeString(TimeUnit.MILLISECONDS.toHours(remaining))
                _m.value = MyTimer.getFullTimeString(TimeUnit.MILLISECONDS.toMinutes(remaining), 60)
                _s.value = MyTimer.getFullTimeString(TimeUnit.MILLISECONDS.toSeconds(remaining), 60)
                getRGB()
            },
            onFinished = {
                _started.value = false
                _paused.value = false
                timeString = ""
                time = 0
                _rgb.value = Color.Green
            }
        )
    }

    fun addDigitToTime(digit: Int) {
        val s = 1
        val m = 60 * s
        val h = 60 * m
        val currentTime = timeString + digit
        timeString = currentTime
        var tmp = currentTime.toLong()
        var secs = tmp % 100
        tmp = (tmp - secs) / 100
        var mins = tmp % 100
        tmp = (tmp - mins) / 100
        if (tmp > 99) { // max to 99h59m59s
            tmp = 99
            mins = 59
            secs = 59
        }
        val finalTime = (tmp * h + mins * m + secs * s) * 1000
        time = finalTime
        newTimer(finalTime)
    }

    fun startTimer() {
        if (_started.value == false) {
            timer.start()
            _started.value = true
        }
    }

    fun restartTimer() {
        if (_paused.value == true) {
            timer.start()
            _paused.value = false
            _rgb.value = Color.Green
        }
    }

    fun pauseTimer() {
        timer.cancel()
        _paused.value = true
    }

    fun resumeTimer() {
        if (_paused.value == true) {
            newTimer(timer.timeLeft)
            timer.start()
            _paused.value = false
        }
    }

    fun getRGB() {
        val percent = timer.timeLeft * 100.0f / time
        val g = 255 * percent / 100
        val r = 255 - g
        _rgb.value = Color(r.toInt(), g.toInt(), 0, 255)
    }
}

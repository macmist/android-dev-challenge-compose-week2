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
package com.example.androiddevchallenge.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.TimerViewModel

@ExperimentalAnimationApi
@Composable
fun TimerScreen(timerViewModel: TimerViewModel) {
    val hours: String by timerViewModel.hours.observeAsState("00")
    val minutes: String by timerViewModel.minutes.observeAsState("00")
    val seconds: String by timerViewModel.seconds.observeAsState("00")
    val started: Boolean by timerViewModel.started.observeAsState(false)
    val paused: Boolean by timerViewModel.paused.observeAsState(false)

    TimerComponent(
        hours = hours,
        minutes = minutes,
        seconds = seconds,
        showInputBox = !started,
        showStart = !started && timerViewModel.time > 0,
        paused = paused,
        timerViewModel = timerViewModel
    )
}

@ExperimentalAnimationApi
@Preview
@Composable
fun TimerScreenPreview() {
    TimerScreen(TimerViewModel())
}

@ExperimentalAnimationApi
@Composable
fun TimerComponent(
    hours: String,
    minutes: String,
    seconds: String,
    showInputBox: Boolean,
    showStart: Boolean,
    paused: Boolean,
    timerViewModel: TimerViewModel
) {
    val color: Color by timerViewModel.color.observeAsState(Color.Green)
    val colAnim: Color by animateColorAsState(targetValue = color)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(color = colAnim)

    ) {
        TimeLeft(hours = hours, minutes = minutes, seconds = seconds)
        InputBox(
            itemClicked = { item -> timerViewModel.addDigitToTime(item.toInt()) },
            visible = showInputBox
        )

        ControlButtons(
            visible = !showInputBox,
            paused = paused,
            onPause = { timerViewModel.pauseTimer() },
            onResume = { timerViewModel.resumeTimer() },
            onRestart = { timerViewModel.restartTimer() }
        )
        if (showStart)
            ActionButton(text = "Start", onClick = { timerViewModel.startTimer() })
    }
}

@ExperimentalAnimationApi
@Composable
fun ControlButtons(
    visible: Boolean,
    paused: Boolean,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onRestart: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -10 }
        ) + expandVertically(
            expandFrom = Alignment.Top
        ) + fadeIn(initialAlpha = 0.3f),
        exit = slideOutVertically() + shrinkVertically() + fadeOut()
    ) {
        AnimatedVisibility(
            visible = paused,
            enter = slideInVertically(
                initialOffsetY = { -10 }
            ) + expandVertically(
                expandFrom = Alignment.Top
            ) + fadeIn(initialAlpha = 0.3f),
            exit = slideOutVertically() + shrinkVertically() + fadeOut()
        ) {
            Column {
                ActionButton(text = "Resume", onClick = onResume)
                ActionButton(text = "Restart", onClick = onRestart)
            }
        }
        AnimatedVisibility(
            visible = !paused,
            enter = slideInVertically(
                initialOffsetY = { -10 }
            ) + expandVertically(
                expandFrom = Alignment.Top
            ) + fadeIn(initialAlpha = 0.3f),
            exit = slideOutVertically() + shrinkVertically() + fadeOut()
        ) {
            ActionButton(text = "Pause", onClick = onPause)
        }
    }
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(text = text)
    }
}

@Composable
fun InputBoxRow(
    items: List<String>,
    itemClicked: (String) -> Unit
) {
    Row(
        modifier = Modifier.padding(10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEach { item ->
            TextButton(onClick = { itemClicked(item) }) {
                Text(item)
            }
        }
    }
}

@Preview
@Composable
fun NumberBoxRowPreview() {
    InputBoxRow(listOf("1", "2", "3")) { {} }
}

@ExperimentalAnimationApi
@Composable
fun InputBox(
    itemClicked: (String) -> Unit,
    visible: Boolean
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { 40 }
        ) + expandVertically(
            expandFrom = Alignment.Bottom
        ) + fadeIn(initialAlpha = 0.3f),
        exit = slideOutVertically() + shrinkVertically() + fadeOut()
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InputBoxRow(items = listOf("1", "2", "3"), itemClicked = itemClicked)
            InputBoxRow(items = listOf("4", "5", "6"), itemClicked = itemClicked)
            InputBoxRow(items = listOf("7", "8", "9"), itemClicked = itemClicked)
            InputBoxRow(items = listOf("0"), itemClicked = itemClicked)
        }
    }
}
@ExperimentalAnimationApi
@Preview
@Composable
fun InputBoxPreview() {
    InputBox(itemClicked = { /*TODO*/ }, true)
}

@Composable
fun TimePart(
    modifier: Modifier = Modifier,
    time: String,
    unit: String
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.h3,
            color = MaterialTheme.colors.primary
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.primaryVariant,
            modifier = Modifier.padding(bottom = 10.dp)
        )
    }
}
@Composable
@Preview
fun TimePartPreview() {
    TimePart(time = "20", unit = "s")
}

@Composable
fun TimeLeft(
    hours: String,
    minutes: String,
    seconds: String,
) {
    Row(
        modifier = Modifier
            .padding(10.dp)
            .animateContentSize(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TimePart(time = hours, unit = "h")
        TimePart(
            time = minutes, unit = "min",
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        TimePart(time = seconds, unit = "s")
    }
}

@Preview
@Composable
fun TimeLeftPreview() {
    TimeLeft(seconds = "22", minutes = "30", hours = "10")
}

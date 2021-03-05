package com.example.androiddevchallenge

import android.os.CountDownTimer
import java.util.concurrent.TimeUnit

class MyTimer(millisInFuture: Long,
              countDownInterval: Long,

              val onUpdate: (Long) -> Unit,
              val onFinished: () -> Unit
) : CountDownTimer(millisInFuture,
    countDownInterval
)
{
    var timeLeft : Long = millisInFuture
    override fun onTick(p0: Long) {
        onUpdate(p0)
        timeLeft = p0
    }
    override fun onFinish() {
        onFinished()
    }

    companion object {
        fun getFullTimeString(unit: Long, max: Long = Long.MAX_VALUE): String {
            val finalUnit = unit % max
            if (finalUnit < 10)
                return "0$finalUnit"
            return finalUnit.toString()
        }
    }
}
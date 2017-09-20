package com.amuyu.samplecoroutine.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.amuyu.samplecoroutine.R
import kotlinx.android.synthetic.main.activity_timeout_job.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI

class TimeoutActivity : AppCompatActivity() {

    var count: Int = 0
    var mainJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeout_job)

        bt_run.setOnClickListener { run() }
    }

    fun run() = async(CommonPool) {
        enableRunBtn(false)
        var time = 1000L
        et_wait_time?.let {
            if (et_wait_time.text.length > 0) {
                time = et_wait_time.text.toString().toLong()
            }
        }

        try {
            withTimeout(time) {
                repeat(1000) { i ->
                    count = i
                    showCount()
                    delay(500L)
                }
            }
        } catch (ex: CancellationException) {
            enableRunBtn(true)
        }
    }

    fun showCount() = async(UI) {
        tv_count.setText(count.toString())
    }

    fun enableRunBtn(enabled: Boolean) = async(UI) {
        bt_run.isEnabled = enabled
    }
}
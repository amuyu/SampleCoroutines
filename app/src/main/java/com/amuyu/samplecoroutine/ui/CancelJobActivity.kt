package com.amuyu.samplecoroutine.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.amuyu.samplecoroutine.R
import kotlinx.android.synthetic.main.activity_wait_job.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI

class CancelJobActivity : AppCompatActivity() {

    var count: Int = 0
    var mainJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wait_job)

        bt_run.setOnClickListener {
            bt_run.isEnabled = false
            run()
        }

        bt_stop.setOnClickListener {
            bt_run.isEnabled = true
            stop()
        }
    }

    fun run() = runBlocking {
        if( !(mainJob?.isActive ?: false) ) {
            mainJob = launch(CommonPool) {
                repeat(1000) { i ->
                    count = i
                    launch(UI) {
                        showCount()
                    }
                    delay(500L)
                }
            }
        }

    }

    fun stop() = runBlocking {
        mainJob?.cancel()
    }

    fun showCount() {
        tv_count.setText(count.toString())
    }
}


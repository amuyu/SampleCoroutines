package com.amuyu.samplecoroutine.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.amuyu.logger.Logger
import com.amuyu.samplecoroutine.R
import kotlinx.android.synthetic.main.activity_cancel_job.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI

class WaitCancelJobActivity : AppCompatActivity() {

    var count: Int = 0
    var mainJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cancel_job)

        bt_run.setOnClickListener {
            run()
        }

        bt_stop.setOnClickListener {
            stop()
        }
    }

    fun run() = async(CommonPool) {
        if( !(mainJob?.isActive ?: false) ) {
            enableRunBtn(false)
            mainJob = launch(CommonPool) {
                repeat(10) { i ->
                    count = i
                    showCount()
                    delay(500L)
                }
            }
            mainJob?.join()
            Logger.d("")
            enableRunBtn(true)
        }

    }

    fun stop() = runBlocking {
        mainJob?.cancel()
    }

    fun showCount() = async(UI) {
        tv_count.setText(count.toString())
    }

    fun enableRunBtn(enabled: Boolean) = async(UI) {
        bt_run.isEnabled = enabled
    }
}


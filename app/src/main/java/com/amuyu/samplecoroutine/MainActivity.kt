package com.amuyu.samplecoroutine

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.amuyu.logger.Logger
import com.amuyu.samplecoroutine.ui.LaunchActivity
import com.amuyu.samplecoroutine.ui.LightweightActivity
import com.amuyu.samplecoroutine.ui.TimeoutActivity
import com.amuyu.samplecoroutine.ui.WaitCancelJobActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.*
import org.jetbrains.anko.startActivity
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis


class MainActivity : AppCompatActivity() {

    var count: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bt_launch.setOnClickListener(View.OnClickListener {
            startActivity<LaunchActivity>()
        })

        bt_lightweight.setOnClickListener(View.OnClickListener {
            startActivity<LightweightActivity>()
        })

        bt_cancel_job.setOnClickListener {
            startActivity<WaitCancelJobActivity>()
        }

        bt_timeout.setOnClickListener {
            startActivity<TimeoutActivity>()
        }

    }

}

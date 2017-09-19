package com.amuyu.samplecoroutine.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.amuyu.logger.Logger
import com.amuyu.samplecoroutine.R
import kotlinx.android.synthetic.main.activity_launch.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking


class LaunchActivity : AppCompatActivity() {

    var isChange: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        bt_run.setOnClickListener({
            et_time.text?.let {
                if(et_time.text.length > 0)
                    run(et_time.text.toString().toLong())
            }
        })
    }

    fun run(time: Long) = runBlocking {
        Logger.d("run start")
        launch(UI) { // create new coroutine and keep a reference to its Job
            Logger.d("delay time:$time")
            delay(time)
            changeColor()
        }
        Logger.d("run end")
    }

    fun changeColor() {
        if(isChange)
            tv_text.setTextColor(getColor(R.color.colorAccent))
        else
            tv_text.setTextColor(getColor(R.color.colorPrimary))
        isChange = !isChange
    }


}

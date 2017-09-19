package com.amuyu.samplecoroutine

import android.app.Application

import com.amuyu.logger.DefaultLogPrinter
import com.amuyu.logger.Logger


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Logger.addLogPrinter(DefaultLogPrinter(this))
    }
}

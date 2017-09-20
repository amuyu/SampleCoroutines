package com.amuyu.samplecoroutine

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.amuyu.logger.Logger
import com.amuyu.samplecoroutine.ui.LaunchActivity
import com.amuyu.samplecoroutine.ui.LightweightActivity
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


//        cooperative()
//        composeSequential()
        composeAsync()
//        composeAsyncLazy()

//        launch(CommonPool) {
//            Logger.d("")
//            delay()
//        }
    }

    fun delay() = runBlocking {
        launch(CommonPool) { // create new coroutine and keep a reference to its Job
            Logger.d("blocking!")
            delay(1000L)
            Logger.d("World!")
        }
        Logger.d("Hello,")
        Logger.d("end,")
    }

    fun job() = runBlocking {
        val job = launch(CommonPool) { // create new coroutine and keep a reference to its Job
            delay(5000L)
            Logger.d("World!")
        }
        Logger.d("Hello,"+job)
        job.join() // wait until child coroutine completes
        Logger.d("end,")
        refactoring()
    }

    fun refactoring() = runBlocking {
        val job = launch(CommonPool) {
            doWorld()
            Logger.d("call doWorld")
        }
        Logger.d("Hello,")
        job.join()
        Logger.d("end,")
    }

    // this is your first suspending function
    suspend fun doWorld() {
        Logger.d("doworld")
        delay(1000L)
        Logger.d("World!")
    }

    fun runList() = runBlocking {
        val jobs = List(100_000) { // create a lot of coroutines and list their jobs
            launch(CommonPool) {
                delay(1000L)
                Logger.d(". count:$count")
            }
        }

        Logger.d("jobs size:"+jobs.size)
        jobs.forEach {
            it.join()
            countup()
        } // wait for all jobs to complete

        jobs.last().join()
        Logger.d("last end count:$count")
    }

    fun runListThread() {

        List(100_000) {
            thread {
                Thread.sleep(1000L)
                count++
                Logger.d(". count:$count")
            }.start()
        }


    }

    suspend fun countup() {
        count++
    }

    fun repeat() = runBlocking {
        launch(CommonPool) {
            repeat(100) { i ->
                Logger.d("I'm sleeping $i ...")
                delay(500L)
            }
        }
        delay(1300L) // just quit after delay
        Logger.d("end")
    }

    fun repeatThread() = runBlocking {
//        Observable.interval(1000, 5000, TimeUnit.MILLISECONDS)
//                .subscribeOn(Schedulers.computation())
//                .subscribe { l -> Logger.d("sleeping " +l) }

        thread {
            while(true) {
                Thread.sleep(500L)
                count++
                Logger.d("sleeping " +count)
            }

        }

        delay(1300L) // just quit after delay
        Logger.d("end")
    }

    fun cooperative() = runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(CommonPool) {
            var nextPrintTime = startTime
            var i = 0
            while (isActive) { // computation loop, just wastes CPU
                // print a message twice a second
                if (System.currentTimeMillis() >= nextPrintTime) {
                    Logger.d("I'm sleeping ${i++} ...")
                    nextPrintTime += 500L
                }
            }
        }
        delay(1300L) // delay a bit
        Logger.d("main: I'm tired of waiting!")
        job.cancel() // cancels the job
        delay(1300L) // delay a bit to see if it was cancelled....
        Logger.d("main: Now I can quit.")
    }

    fun composeSequential() = runBlocking {
        val time = measureTimeMillis {
            val one = doSomethingUsefulOne()
            val two = doSomethingUsefulTwo()
            Logger.d("The answer is ${one + two}")
        }
        Logger.d("Completed in $time ms")
    }

    fun composeAsync() = runBlocking {
        val time = measureTimeMillis {
            val one = doSomethingUsefulOneAsync()
            val two = async(CommonPool) { doSomethingUsefulTwo() }
            Logger.d("The answer is ${one.await() + two.await()}")
        }
        Logger.d("Completed in $time ms")
    }

    fun composeAsyncLazy() = runBlocking {
        val time = measureTimeMillis {
            val one = async(CommonPool, CoroutineStart.LAZY) { doSomethingUsefulOne() }
            val two = async(CommonPool, CoroutineStart.LAZY) { doSomethingUsefulTwo() }
            Logger.d("The answer is ${one.await() + two.await()}")
        }
        Logger.d("Completed in $time ms")
    }

    suspend fun doSomethingUsefulOne(): Int {
        Logger.d("");
        delay(1000L) // pretend we are doing something useful here
        return 13
    }

    suspend fun doSomethingUsefulTwo(): Int {
        Logger.d("");
        delay(1000L) // pretend we are doing something useful here, too
        return 29
    }

    fun doSomethingUsefulOneAsync() = async(CommonPool){
        doSomethingUsefulOne()
    }

}

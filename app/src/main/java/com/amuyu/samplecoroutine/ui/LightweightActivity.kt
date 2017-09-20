package com.amuyu.samplecoroutine.ui




import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import com.amuyu.samplecoroutine.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_lightweight.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.util.concurrent.TimeUnit


class LightweightActivity : AppCompatActivity() {

    var index: Int = 0
    lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lightweight)

        listView.let {
            adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ArrayList())
            it.setAdapter(adapter)
            it.setOnItemClickListener { adapterView, view, pos, id ->
                if (pos == 0) {
                    var index = adapter.count - 1
                    if (index > 0) it.setSelection(index)
                }
            }
        }

        bt_coroutine.setOnClickListener(View.OnClickListener {
            clickCoroutine()
        })

        bt_thread.setOnClickListener(View.OnClickListener {
            clickThread()
        })
    }

    fun clickCoroutine() {
        bt_coroutine.isEnabled = false
        progreeBar.visibility = View.VISIBLE
        adapter.clear()
        Observable.timer(1000L, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { needless ->
                    runCoroutine {
                        bt_coroutine.isEnabled = true
                        progreeBar.visibility = View.GONE }
                }
    }


    fun runCoroutine(callback: () ->Unit) = runBlocking {
        val jobs = List(100_000) { // create a lot of coroutines and list their jobs
            launch(CommonPool) {
                delay(1000L)
            }
        }

        jobs.forEach {
            it.join()
            addList()
        } // wait for all jobs to complete

        jobs.last().join()
        callback()
    }

    fun clickThread() {
        bt_thread.isEnabled = false
        progreeBar.visibility = View.VISIBLE
        adapter.clear()
        Observable.timer(1000L, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { needless ->
                    runThread()
                }
    }


    fun runThread() {
        List(100_000) {
            Thread {
                Thread.sleep(1000L)
                runOnUiThread(Runnable {
                    addList()
                })
            }.start()
        }
    }

    fun addList() {
        index++
        adapter.add("text index:$index")
    }


}
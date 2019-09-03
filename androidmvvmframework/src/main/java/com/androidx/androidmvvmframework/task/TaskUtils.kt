package com.androidx.androidmvvmframework.task

import android.content.Context
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.work.*
import java.util.concurrent.TimeUnit

class CountDownTask(
    millisInFuture: Long,
    countDownInterval: Long
) :
    CountDownTimer(millisInFuture, countDownInterval) {
    private var mDone: () -> Unit = {}
    private var mCountDown: (time: Long) -> Unit = {}
    fun onDone(done: () -> Unit) {
        mDone = done
    }

    fun onCount(count: (time: Long) -> Unit) {
        mCountDown = count
    }

    override fun onFinish() {
        mDone()
    }

    override fun onTick(millisUntilFinished: Long) {
        mCountDown(millisUntilFinished)
    }

}

inline fun <reified T> T.countDownTask(
    taskTime: Long,
    interval: Long,
    task: CountDownTask.() -> Unit
) {
    task(CountDownTask(taskTime, interval))
}


fun LifecycleOwner.getContext(): Context? {
    return when (this) {
        is Fragment -> context
        is AppCompatActivity -> this
        else -> throw Exception("")
    }
}

inline fun <reified T : Worker> LifecycleOwner.SingleWorker(
    startTime: Long,
    timeUnit: TimeUnit,
    crossinline workRequest: OneTimeWorkRequest.Builder.() -> Unit,
    crossinline observer: WorkInfo.() -> Unit
) {
    val mBuilder = OneTimeWorkRequestBuilder<T>().setConstraints(Constraints.NONE)
        .setInitialDelay(startTime, timeUnit)
    workRequest(mBuilder)
    val mWorkRequest = mBuilder.build()
    val context = getContext()
    WorkManager.getInstance(context!!).getWorkInfoByIdLiveData(mWorkRequest.id)
        .observe(this, Observer {
            observer(it)
        })
    WorkManager.getInstance(context).enqueue(mWorkRequest)

}

inline fun <reified T : Worker> LifecycleOwner.RepeatingTask(
    repeatTime: Long,
    repeatTimeUnit: TimeUnit,
    startTime: Long,
    startTimeUnit: TimeUnit,
    crossinline periodicWorkRequestBuilder: PeriodicWorkRequest.Builder.() -> Unit,
    crossinline observer: WorkInfo.() -> Unit
) {
    val mBuilder = PeriodicWorkRequestBuilder<T>(repeatTime, repeatTimeUnit)
        .setInitialDelay(startTime,startTimeUnit)
    periodicWorkRequestBuilder(mBuilder)
    val periodicWorkRequest = mBuilder.build()
    val context = getContext()
    WorkManager.getInstance(context!!).enqueueUniquePeriodicWork(
        T::class.java.simpleName,
        ExistingPeriodicWorkPolicy.KEEP,
        periodicWorkRequest
    )
    WorkManager.getInstance(context).getWorkInfoByIdLiveData(periodicWorkRequest.id)
        .observe(this, Observer {
            observer(it)
        })
}

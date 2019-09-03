package com.androidx.androidmvvmframework.event

import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.ConcurrentHashMap


class EventLiveData<T> : MutableLiveData<T>() {
    internal  var mContent: T?=null
    private var mHandle:Boolean = false
    override fun onActive() {
        super.onActive()
        if (mContent!=null && !mHandle){
            mHandle = true
            setValue(mContent!!)

        }
    }

    override fun setValue(value: T) {
        super.setValue(value)
        mHandle= true
    }

}

internal object EventManager {
    private val mEventMap: ConcurrentHashMap<String, EventLiveData<*>> by lazy {
        ConcurrentHashMap<String, EventLiveData<*>>()
    }

    private fun <T> validateEventLiveData(
        eventId: String,
        onNull: () -> Unit,
        onInit: EventLiveData<T>.()->Unit) {
        val eventLiveData = mEventMap[eventId]
         if (eventLiveData == null) {
            onNull()
        } else {
             onInit(eventLiveData as EventLiveData<T>)

        }
    }

    fun <T> registerEvent(
        eventId: String,
        lifecycleOwner: LifecycleOwner,
        onReceive: (content: T) -> Unit
    ) {
        validateEventLiveData<T>(eventId,{
            val eventLiveData = EventLiveData<T>()
            eventLiveData.observe(lifecycleOwner, Observer {
                onReceive(it)
            })

            mEventMap[eventId] = eventLiveData

        },{
            if (!hasObservers()){
                observe(lifecycleOwner, Observer {
                    onReceive(it)
                })
            }else{
                setContent(this,mContent!!)
            }
        })

    }

    fun <T> sendEvent(eventId: String,content: T) {
        validateEventLiveData<T>(eventId, {
            val eventLiveData = EventLiveData<T>()
            eventLiveData.mContent = content
            mEventMap[eventId] = eventLiveData
        },{

            setContent(this,content)
        })


    }
    private fun <T>setContent(liveData: EventLiveData<T>, content:T){

        if (Looper.myLooper() == Looper.getMainLooper()) {
            liveData.setValue(content)
        } else {
            liveData.postValue(content)
        }
    }
}

 fun < T> dispatchEvent(eventId: String,content:T){
     EventManager.sendEvent(eventId, content)
}

 fun <T> LifecycleOwner.bindEvent(eventId: String, onReceive: (content: T) -> Unit){
     EventManager.registerEvent<T>(eventId, this) {
         onReceive(it)
     }
}
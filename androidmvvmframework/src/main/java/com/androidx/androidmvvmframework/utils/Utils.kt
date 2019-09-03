package com.androidx.androidmvvmframework.utils

import android.app.KeyguardManager
import android.content.Context
import android.os.PowerManager
import android.os.PowerManager.ACQUIRE_CAUSES_WAKEUP
import android.os.PowerManager.PARTIAL_WAKE_LOCK
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import java.text.SimpleDateFormat
import java.util.*

 fun dateFormat(pattern:String):SimpleDateFormat{
    return SimpleDateFormat(pattern, Locale.CHINA)
}


inline val <reified T:Context> T.wakeLock:PowerManager.WakeLock get() =
    (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
        newWakeLock(ACQUIRE_CAUSES_WAKEUP or PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag").apply {
            acquire(5000)
            release()
        }
    }

inline fun <reified T:Context> T.wakeLock(tag:String):PowerManager.WakeLock{
    return (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
        newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::$tag").apply {
            acquire()
        }
    }
}
inline fun <reified T:AppCompatActivity> T.unlockScreen(crossinline unlock:()->Unit){
    getSystemService(KeyguardManager::class.java).run {
        if (isKeyguardLocked){
            newKeyguardLock("unlock").disableKeyguard()
            requestDismissKeyguard(this@unlockScreen,object :
                KeyguardManager.KeyguardDismissCallback() {
                override fun onDismissCancelled() {
                    super.onDismissCancelled()
                    Log.e("===","===onDismissCancelled>>>>")
                }
                override fun onDismissSucceeded() {
                    super.onDismissSucceeded()
                    Log.e("===","===onDismissSucceeded>>>>")
                    unlock()
                }

                override fun onDismissError() {
                    super.onDismissError()
                    Log.e("===","===onDismissError>>>>")


                }
            })
        }
    }

}
inline fun <reified T:AppCompatActivity> T.keepScreenOn(){
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}

inline fun <reified T:Fragment> T.alertMessage(message:String, action:Snackbar.()->Unit){
    val contentView = (this.activity?.window?.decorView as ViewGroup).getChildAt(0)
    Snackbar.make(contentView,message,Snackbar.LENGTH_LONG).run {
        action(this)
        show()
    }
}
inline fun <reified T:AppCompatActivity> T.alertMessage(message:String, action:Snackbar.()->Unit){
    val contentView = (window?.decorView as ViewGroup).getChildAt(0)
    Snackbar.make(contentView,message,Snackbar.LENGTH_LONG).run {
        action(this)
        animationMode = Snackbar.ANIMATION_MODE_SLIDE
        show()
    }
}

inline fun <reified T> T.eLog(message: String){
    Logger.e(message,T::class.simpleName)

}
inline fun <reified T> T.wLog(message: String){
    Logger.w(message,T::class.simpleName)

}
inline fun <reified T> T.dLog(message: String){
    Logger.d(message,T::class.simpleName)

}

 val mGson: Gson by lazy {
    Gson()
}
inline fun <reified T> T.json():String{
   return mGson.toJson(this)
}

package com.androidx.androidmvvmframework.utils

import android.view.View

class SingleClick(private val interval:Long,private val onClick:()->Unit):View.OnClickListener {
    private var mPreClickTIme:Long = 0
    override fun onClick(v: View?) {
        val clickTime = System.currentTimeMillis()
            if (clickTime - mPreClickTIme>interval){
                onClick()
                mPreClickTIme = System.currentTimeMillis()
            }
    }
}

inline fun <reified T> T.singleOnClick(interval:Long,noinline onClick: () -> Unit): SingleClick {
    return SingleClick(interval,onClick)
}
package com.androidx.myapplication

import android.os.Bundle
import com.androidx.androidmvvmframework.ui.fragment.AbstractFragment
import com.androidx.androidmvvmframework.utils.eLog

class MyFragment:AbstractFragment() {
    override val layoutRes: Int = R.layout.f_my
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTitleData.set("myFragment")
    }

    override fun onFragmentFinish(): Boolean {
        eLog("===>>>>>")
        return false
    }
}
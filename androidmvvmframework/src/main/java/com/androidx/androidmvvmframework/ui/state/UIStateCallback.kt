package com.androidx.androidmvvmframework.ui.state

import com.androidx.androidmvvmframework.databinding.UiStateBinding
import com.androidx.androidmvvmframework.ui.model.UIStateData

interface UIStateCallback {
    val mUIStateData:UIStateData
    fun onUIStateInit(mUiStateBinding: UiStateBinding){}
}
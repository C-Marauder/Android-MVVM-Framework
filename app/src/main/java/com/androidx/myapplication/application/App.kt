package com.androidx.myapplication.application

import android.app.Application
import android.os.Build
import com.androidx.androidmvvmframework.ui.UIFramework
import com.androidx.androidmvvmframework.ui.colorRes
import com.androidx.androidmvvmframework.ui.model.TemplateData
import com.androidx.androidmvvmframework.vm.NetworkObserver
import com.androidx.myapplication.R

class App:Application(),NetworkObserver {

    override fun onCreate() {
        super.onCreate()
        UIFramework.initUITemplate {
            templateData {
                TemplateData().apply {
                        themeColor = R.color.colorPrimary
                }
            }
        }
        initNetworkObserver(this)
    }
}
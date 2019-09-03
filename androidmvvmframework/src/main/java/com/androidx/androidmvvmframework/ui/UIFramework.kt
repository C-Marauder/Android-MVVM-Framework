package com.androidx.androidmvvmframework.ui

import android.app.Application
import com.androidx.androidmvvmframework.ui.model.TemplateData

class UIFramework private constructor(){
    companion object{
        internal val mHelper:UIFramework by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            UIFramework()
        }
        fun initUITemplate(init:UIFramework.()->Unit){
            init(mHelper)
        }
    }

    internal lateinit var mTemplateData: TemplateData

    fun templateData(templateDataCreate:()->TemplateData){
        mTemplateData = templateDataCreate()
    }


}
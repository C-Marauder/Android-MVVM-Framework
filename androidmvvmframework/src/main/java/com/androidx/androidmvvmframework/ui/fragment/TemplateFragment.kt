package com.androidx.androidmvvmframework.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.androidx.androidmvvmframework.R
import com.androidx.androidmvvmframework.databinding.TemplateCoordinatorBinding
import com.androidx.androidmvvmframework.databinding.UiStateBinding
import com.androidx.androidmvvmframework.ui.UIFramework
import com.androidx.androidmvvmframework.ui.model.TemplateData
import com.androidx.androidmvvmframework.ui.state.UIStateCallback
import com.google.android.material.appbar.MaterialToolbar

abstract class TemplateFragment:AbstractFragment() {
    val mTemplateData: TemplateData?=null
    open fun onTemplateInit(templateCoordinatorBinding: TemplateCoordinatorBinding){

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mTemplateCoordinatorBinding = DataBindingUtil.inflate<TemplateCoordinatorBinding>(inflater,
            R.layout.template_coordinator,container,false).apply {
            titleData = mTitleData
            template = mTemplateData ?: UIFramework.mHelper.mTemplateData
        }
        setNavListener(mTemplateCoordinatorBinding.toolbar)
        onTemplateInit(mTemplateCoordinatorBinding)
        inflateContentView(inflater,mTemplateCoordinatorBinding)
        inflateUIStateView(inflater,mTemplateCoordinatorBinding)
        return mTemplateCoordinatorBinding.root
    }

    /**
     * 加载内容视图
     */
    private fun inflateContentView(inflater: LayoutInflater,mTemplateCoordinatorBinding: TemplateCoordinatorBinding){
        val mContentDataBinding = DataBindingUtil.inflate<ViewDataBinding>(inflater,layoutRes,mTemplateCoordinatorBinding.coordinator,false)
        onContentViewInit(mContentDataBinding)
        mTemplateCoordinatorBinding.coordinator.addView(mContentDataBinding.root,1)
    }

    /**
     * 监听toolbar导航事件
     */
    private fun setNavListener(materialToolbar: MaterialToolbar){
        materialToolbar.setNavigationOnClickListener {
            shouldInterruptedOnBackPressed()
        }
    }
    /**
     * 加载多状态UI视图
     */
    private fun inflateUIStateView(inflater: LayoutInflater,mTemplateCoordinatorBinding: TemplateCoordinatorBinding){
        if (this is UIStateCallback){
            val mUiStateBinding = DataBindingUtil.inflate<UiStateBinding>(inflater,R.layout.ui_state,mTemplateCoordinatorBinding.coordinator,false)
            mTemplateCoordinatorBinding.coordinator.addView(mUiStateBinding.root,2)
            mUiStateBinding.uiState = mUIStateData
            onUIStateInit(mUiStateBinding)
        }
    }
}
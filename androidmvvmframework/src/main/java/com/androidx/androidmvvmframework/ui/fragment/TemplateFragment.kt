package com.androidx.androidmvvmframework.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.androidx.androidmvvmframework.R
import com.androidx.androidmvvmframework.databinding.TemplateCoordinatorBinding
import com.androidx.androidmvvmframework.databinding.UiStateBinding
import com.androidx.androidmvvmframework.ui.UIFramework
import com.androidx.androidmvvmframework.ui.dp
import com.androidx.androidmvvmframework.ui.model.TemplateData
import com.androidx.androidmvvmframework.ui.state.UIStateCallback
import com.androidx.androidmvvmframework.utils.eLog
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.circularreveal.coordinatorlayout.CircularRevealCoordinatorLayout

abstract class TemplateFragment : AbstractFragment() {
    val mTemplateData: TemplateData? = null
    open fun onTemplateInit(templateCoordinatorBinding: TemplateCoordinatorBinding) {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mTemplateCoordinatorBinding = DataBindingUtil.inflate<TemplateCoordinatorBinding>(
            inflater,
            R.layout.template_coordinator, container, false
        ).apply {
            titleData = mTitleData
            template = mTemplateData ?: UIFramework.mHelper.mTemplateData
        }
        setNavListener(mTemplateCoordinatorBinding.toolbar)
        onTemplateInit(mTemplateCoordinatorBinding)
        inflateContentView(inflater, mTemplateCoordinatorBinding)
        inflateUIStateView(inflater, mTemplateCoordinatorBinding)
        return mTemplateCoordinatorBinding.root
    }

    private fun appBarScrollAnim(mContentDataBinding: ViewDataBinding,templateCoordinatorBinding: TemplateCoordinatorBinding) {
        if (mContentDataBinding.root is RecyclerView) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mContentDataBinding.root.setOnScrollChangeListener { view, left, top, right, bottom ->
                    if ( view.scaleY == 0f){
                        templateCoordinatorBinding.appBar.elevation = 0f
                    }else{
                        templateCoordinatorBinding.appBar.elevation = 4f

                    }

                }
            }
        }
}

private fun setContentViewBehavior(mContentDataBinding: ViewDataBinding) {
    val lp = mContentDataBinding.root.layoutParams as CoordinatorLayout.LayoutParams
    lp.behavior = AppBarLayout.ScrollingViewBehavior()
}

/**
 * 加载内容视图
 */
private fun inflateContentView(
    inflater: LayoutInflater,
    mTemplateCoordinatorBinding: TemplateCoordinatorBinding
) {
    val mContentDataBinding = DataBindingUtil.inflate<ViewDataBinding>(
        inflater,
        layoutRes,
        mTemplateCoordinatorBinding.coordinator,
        false
    )
    onContentViewInit(mContentDataBinding)
    mTemplateCoordinatorBinding.coordinator.addView(mContentDataBinding.root, 1)
    setContentViewBehavior(mContentDataBinding)
}


/**
 * 监听toolbar导航事件
 */
private fun setNavListener(materialToolbar: MaterialToolbar) {

    materialToolbar.setNavigationOnClickListener {
        shouldInterruptedOnBackPressed()
    }
}

/**
 * 加载多状态UI视图
 */
private fun inflateUIStateView(
    inflater: LayoutInflater,
    mTemplateCoordinatorBinding: TemplateCoordinatorBinding
) {
    if (this is UIStateCallback) {
        val mUiStateBinding = DataBindingUtil.inflate<UiStateBinding>(
            inflater,
            R.layout.ui_state,
            mTemplateCoordinatorBinding.coordinator,
            false
        )
        mTemplateCoordinatorBinding.coordinator.addView(mUiStateBinding.root, 2)
        mUiStateBinding.uiState = mUIStateData
        onUIStateInit(mUiStateBinding)
    }
}
}
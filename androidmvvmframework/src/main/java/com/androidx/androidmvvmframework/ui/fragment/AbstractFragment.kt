package com.androidx.androidmvvmframework.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment


abstract class AbstractFragment:Fragment() {
    abstract val layoutRes:Int
    open fun onFragmentFinish():Boolean{
        return false
    }
    val mTitleData:ObservableField<String> by lazy {
        ObservableField<String>()
    }
    private lateinit var mOnBackPressedCallback: OnBackPressedCallback
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mOnBackPressedCallback= object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                shouldInterruptedOnBackPressed()
            }

        }
        observeBackPressedListener()
    }


    open fun onContentViewInit(contentViewDataBinding: ViewDataBinding){

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<ViewDataBinding>(inflater,layoutRes,container,false)
        onContentViewInit(dataBinding)
        return dataBinding.root
    }

    /**
     * 监听返回键事件
     */
    private fun observeBackPressedListener(){
        this.activity?.onBackPressedDispatcher?.addCallback(this,mOnBackPressedCallback)
    }
    internal fun shouldInterruptedOnBackPressed(){
        val interrupted = onFragmentFinish()
        if (!interrupted){
            mOnBackPressedCallback.isEnabled = false
            this.activity?.onBackPressed()
        }
    }

}

package com.androidx.androidmvvmframework.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import com.androidx.androidmvvmframework.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomDialog: BottomSheetDialogFragment() {
    @LayoutRes
    private  var mLayoutResId:Int =0
    companion object{
        private lateinit var mInstance: BottomDialog
        fun show(fragmentManager: FragmentManager,init: BottomDialog.()->Unit){
            if (!Companion::mInstance.isInitialized){
                mInstance = BottomDialog()
                init(mInstance)
            }

            mInstance.show(fragmentManager,"")
        }
    }

    fun dialogLayout(layout: () -> Int) {
        mLayoutResId = layout()
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            it.window?.setWindowAnimations(R.style.BottomDialogStyle)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<ViewDataBinding>(inflater,mLayoutResId,container,false)
        return dataBinding.root
    }
}
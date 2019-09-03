package com.androidx.androidmvvmframework.dialog

import android.content.DialogInterface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

import androidx.fragment.app.FragmentManager
import com.androidx.androidmvvmframework.R

class AppDialog : AppCompatDialogFragment() {
    private var mWidth: Int = -2
    private var mHeight: Int = -2
    private var mRadius: Float = 8f
    private var mTag: String? = null
    private var mLogic: DialogLogic<Any>? = null
    @LayoutRes
    private var mLayoutResId: Int = 0
    @ColorRes
    private var mBackgroundColor:Int = android.R.color.background_light
    private var mDim:Float = 0f
    @StyleRes
    private var mAnim:Int = R.style.DialogAnimStyle
    private var mOnDialogDismiss: OnDialogDismiss?=null
    private var mCancel:Boolean = true
    companion object {
        private lateinit var mInstance: AppDialog
        private lateinit var mFragmentManager: FragmentManager
        fun show(
            fragmentManager: FragmentManager,
            now: Boolean = false,
            dialog: AppDialog.() -> Unit
        ) {
            if (!Companion::mInstance.isInitialized) {
                mFragmentManager = fragmentManager
                mInstance = AppDialog()
                dialog(mInstance)
            }
            if (now) {
                mInstance.showNow(fragmentManager, mInstance.mTag)
            } else {
                mInstance.show(fragmentManager, mInstance.mTag)
            }
        }

        fun dismiss(tag: String) {
            if (!Companion::mInstance.isInitialized) {
                throw NullPointerException("the fragment whose tag is $tag not create ")
            }
            mFragmentManager.findFragmentByTag(mInstance.mTag)
        }
    }

    fun dialogTag(tag: () -> String) {
        mTag = tag()
    }

    fun dialogWidth(width: () -> Int) {
        mWidth = width()
    }

    fun dialogHeight(height: () -> Int) {
        mHeight = height()
    }

    fun dialogRadius(radius: () -> Float) {
        mRadius = radius()
    }

    fun dialogLayout(layout: () -> Int) {
        mLayoutResId = layout()
    }
    fun dialogBackground(background:()->Int){
        mBackgroundColor = background()
    }

    fun dialogDim(dim:()->Float){
        mDim = dim()
    }
    fun dialogAnim(anim:()->Int){
        mAnim = anim()
    }

    fun dialogCancelOnTouchOutside(cancel:()->Boolean){
        mCancel = cancel()
    }

    fun <T : ViewDataBinding> dialogLogic(logic: DialogLogic<T>) {
        mLogic = logic as DialogLogic<Any>
    }

    fun onDialogDismiss(dismiss: OnDialogDismiss){
        mOnDialogDismiss = dismiss
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            it.setCanceledOnTouchOutside(mCancel)
            val gradientDrawable = GradientDrawable()
            gradientDrawable.cornerRadius = mRadius
            gradientDrawable.setColor(ContextCompat.getColor(context!!,mBackgroundColor))
            it.window?.let {wd->
                    wd.setWindowAnimations(mAnim)


                wd.setBackgroundDrawable(gradientDrawable)
                if (mDim != 0f){
                    wd.setDimAmount(mDim)
                }
                wd.setLayout(mWidth,mHeight)
                //wd.setElevation(24f)

            }

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val dataBinding =
            DataBindingUtil.inflate<ViewDataBinding>(inflater, mLayoutResId, container, false)
        mLogic?.invoke(dataBinding)

        return dataBinding.root

    }


    override fun onDismiss(dialog: DialogInterface) {
        mOnDialogDismiss?.invoke()
        super.onDismiss(dialog)
    }

}
typealias OnDialogDismiss = ()->Unit
typealias DialogLogic <T> = (dataBinding: T) -> Unit
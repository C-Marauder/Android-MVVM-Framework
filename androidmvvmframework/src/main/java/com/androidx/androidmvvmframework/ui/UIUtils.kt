package com.androidx.androidmvvmframework.ui

import android.app.Application
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.TypedArrayUtils
import androidx.fragment.app.Fragment

inline fun <reified T : Application> T.colorRes(@ColorRes resId: Int): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        getColor(resId)
    } else {
        ContextCompat.getColor(this, resId)
    }

}

inline fun <reified T : Fragment> T.colorRes(@ColorRes resId: Int): Int {
    return this.activity?.application?.colorRes(resId)!!
}
inline fun <reified T : AppCompatActivity> T.colorRes(@ColorRes resId: Int): Int {
    return this.application?.colorRes(resId)!!
}

inline fun <reified T : Application> T.drawableRes(@DrawableRes resId: Int): Drawable? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        getDrawable(resId)
    } else {
        ContextCompat.getDrawable(this, resId)
    }
}

inline fun <reified T : Application> T.dimenRes(@DimenRes resId: Int): Float {
    return this.resources.getDimension(resId)
}


val Int.dp: Float
    get() {
       return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,this*1f,Resources.getSystem().displayMetrics)
    }
val Int.sp: Float
    get() {
       return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,this*1f,Resources.getSystem().displayMetrics)
    }

inline fun <reified T:AppCompatActivity> T.translucentStatusBar(){
    val decorView = window.decorView
    var newUiOptions =decorView.systemUiVisibility
    newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    decorView.systemUiVisibility = newUiOptions
    window.statusBarColor =    colorRes(android.R.color.transparent)
}
inline fun <reified T:AppCompatActivity> T.statusBarDarkMode(){
    val decorView = window.decorView
    var newUiOptions =decorView.systemUiVisibility
    newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    decorView.systemUiVisibility = newUiOptions
}
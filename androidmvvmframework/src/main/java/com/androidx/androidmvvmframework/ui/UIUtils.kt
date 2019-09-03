package com.androidx.androidmvvmframework.ui

import android.app.Application
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

inline fun <reified T:Application> T.colorRes(@ColorRes resId:Int):Int{
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        getColor(resId)
    }else{
        ContextCompat.getColor(this,resId)
    }

}
inline fun <reified T:Application> T.drawableRes(@DrawableRes resId:Int):Drawable?{
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        getDrawable(resId)
    }else{
        ContextCompat.getDrawable(this,resId)
    }
}

inline fun<reified T:Application>   T.dimenRes(@DimenRes resId:Int):Float{
    return this.resources.getDimension(resId)
}



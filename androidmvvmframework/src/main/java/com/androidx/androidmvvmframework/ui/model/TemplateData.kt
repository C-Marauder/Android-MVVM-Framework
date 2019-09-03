package com.androidx.androidmvvmframework.ui.model

import android.content.res.Resources
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import com.androidx.androidmvvmframework.BR
import com.androidx.androidmvvmframework.R
import com.androidx.androidmvvmframework.ui.UIFramework
import com.androidx.androidmvvmframework.ui.colorRes
import com.androidx.androidmvvmframework.ui.dimenRes
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView

open class TemplateData : BaseObservable() {
    @DimenRes
    @get:Bindable
    var appBarElevation:Int = R.dimen.appBarElevation
    set(value) {
        field = value
        notifyPropertyChanged(BR.appBarElevation)
    }
    @DimenRes
    @get:Bindable
    var centerTitleSize: Int =R.dimen.toolbarCenterTitleSize
        set(value) {
            field = value
            notifyPropertyChanged(BR.centerTitleSize)
        }
    @ColorRes
    @get:Bindable
    var themeColor: Int = R.color.colorPrimary
        set(value) {
            field = value
            notifyPropertyChanged(BR.themeColor)
        }
    @ColorRes
    @get:Bindable
    var centerTitleColor: Int = android.R.color.background_light
        set(value) {
            field = value
            notifyPropertyChanged(BR.centerTitleColor)
        }
    @DimenRes
    @get:Bindable
    var toolbarSize: Int = R.dimen.toolbarSize
        set(value) {
            field = value
            notifyPropertyChanged(BR.toolbarSize)

        }
    @DrawableRes
    @get:Bindable
    var navigationRes: Int = R.drawable.navigation
        set(value) {
            field = value
            notifyPropertyChanged(BR.navigationRes)

        }
}
@BindingAdapter("dimenTextSize")
fun MaterialTextView.convertDimenResToFloat(@DimenRes resId:Int){
    textSize = resources.getDimension(resId)
}
@BindingAdapter("colorTextColor")
fun MaterialTextView.convertColorResToInt(@ColorRes resId: Int){
    setTextColor(ContextCompat.getColor(context,resId))
}

@BindingAdapter("toolbarSize")
fun MaterialToolbar.setToolbarSize(@DimenRes sizeRes: Int) {
    val lp = this.layoutParams as AppBarLayout.LayoutParams
    lp.height = this.resources.getDimension(sizeRes).toInt()
    this.layoutParams = lp
}
@BindingAdapter("themeBackground")
fun MaterialToolbar.setThemeBackground(@ColorRes resId: Int) {
  setBackgroundResource(resId)
}

open class UIStateData:BaseObservable() {
    @DrawableRes
    @get:Bindable
    var uiStateRes: Int =0
    set(value) {
        field = value
        notifyPropertyChanged(BR.uiStateRes)
    }
    @ColorRes
    @get:Bindable
    var uiStateColorRes: Int = android.R.color.background_light
    set(value) {
        field = value
        notifyPropertyChanged(BR.uiStateColorRes)
    }
}
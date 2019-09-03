package com.androidx.androidmvvmframework.anim

import android.graphics.drawable.AnimatedVectorDrawable
import androidx.appcompat.widget.AppCompatImageView


inline fun <reified T:AppCompatImageView> T.start(){
    (this.drawable as AnimatedVectorDrawable).start()
}
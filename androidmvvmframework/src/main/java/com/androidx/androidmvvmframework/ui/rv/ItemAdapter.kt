package com.androidx.androidmvvmframework.ui.rv

import android.view.View
import androidx.databinding.ViewDataBinding

abstract class ItemAdapter<T,VDB:ViewDataBinding,VH:MVVMViewHolder<T,VDB>>( val data:MutableList<T>){

     fun getItemCount():Int = data.size
    abstract fun getItemLayoutResId():Int
    abstract fun createViewHolder(dataBinding: VDB,itemView:View):VH

}
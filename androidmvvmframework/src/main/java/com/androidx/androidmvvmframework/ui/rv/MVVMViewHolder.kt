package com.androidx.androidmvvmframework.ui.rv

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class MVVMViewHolder<T,VDB:ViewDataBinding>(val dataBinding: VDB,itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract fun bindData(item:T)


}
package com.androidx.myapplication.adapter

import android.view.View
import com.androidx.androidmvvmframework.ui.rv.ItemAdapter
import com.androidx.androidmvvmframework.ui.rv.MVVMViewHolder
import com.androidx.myapplication.R
import com.androidx.myapplication.databinding.Item1Binding
import com.androidx.myapplication.databinding.Item2Binding

class Item1Adapter(data: MutableList<String>,private val removeItem: (position: Int) -> Unit) :
    ItemAdapter<String, Item1Binding, Item1ViewHolder>(data) {
    override fun getItemLayoutResId(): Int = R.layout.item_1

    override fun createViewHolder(dataBinding: Item1Binding, itemView: View): Item1ViewHolder {
        return Item1ViewHolder(dataBinding,itemView){
            removeItem(it)
        }
    }

}

class Item1ViewHolder(dataBinding: Item1Binding, itemView: View,private val removeItem:(position:Int)->Unit) :
    MVVMViewHolder<String, Item1Binding>(dataBinding, itemView) {
    override fun bindData(item: String) {
        dataBinding.data = item

    }
    init {
        dataBinding.holder = this

    }
    fun remove(){
        removeItem(adapterPosition)
    }
}


class Item2Adapter(data: MutableList<String>) :
    ItemAdapter<String, Item2Binding, Item2ViewHolder>(data) {
    override fun getItemLayoutResId(): Int = R.layout.item_2

    override fun createViewHolder(dataBinding: Item2Binding, itemView: View): Item2ViewHolder {
        return Item2ViewHolder(dataBinding,itemView)
    }

}

class Item2ViewHolder(dataBinding: Item2Binding, itemView: View) :
    MVVMViewHolder<String, Item2Binding>(dataBinding, itemView) {
    override fun bindData(item: String) {
        dataBinding.data = item
    }
}
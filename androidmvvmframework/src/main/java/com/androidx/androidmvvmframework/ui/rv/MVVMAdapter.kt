package com.androidx.androidmvvmframework.ui.rv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView


class RvAdapterUtils private constructor() :
    RecyclerView.Adapter<MVVMViewHolder<Any, ViewDataBinding>>() {
    private var mItemCount: Int = 0
    private val mItemAdapters: MutableList<ItemAdapter<Any, ViewDataBinding, MVVMViewHolder<Any, ViewDataBinding>>> by lazy {
        mutableListOf<ItemAdapter<Any, ViewDataBinding, MVVMViewHolder<Any, ViewDataBinding>>>()
    }
    private val mItemPosition: MutableList<Int> by lazy {
        mutableListOf<Int>()
    }
    companion object {

        fun rvAdapter(init: RvAdapterUtils.() -> Unit): RecyclerView.Adapter<*> {
            val mRvAdapterUtils = RvAdapterUtils()
            init(mRvAdapterUtils)
            return mRvAdapterUtils
        }

    }

    private lateinit var mItemViewHolder: ItemViewHolder
    private lateinit var itemLayoutRes: (position: Int) -> Int
    private lateinit var mItemData: (position: Int) -> Any
    fun itemData(data: (position: Int) -> Any) {
        mItemData = data
    }

    fun itemLayoutRes(layoutRes: ItemLayoutRes) {
        itemLayoutRes = layoutRes
    }

    fun itemViewHolder(itemViewHolder: ItemViewHolder) {
        mItemViewHolder = itemViewHolder
    }

    fun itemCount(itemCount: () -> Int) {
        mItemCount = itemCount()
    }

    fun itemAdapters(itemAdapters: () -> MutableList<ItemAdapter<Any, ViewDataBinding, MVVMViewHolder<Any, ViewDataBinding>>>) {
        val adapters = itemAdapters()
        adapters.forEachIndexed { index, itemAdapter ->
            val itemCount = itemAdapter.getItemCount()
            mItemCount += itemCount
            for (i in 0 until itemCount) {
                mItemPosition.add(index)
            }
        }
        mItemAdapters.addAll(adapters)
    }
    fun removeItem(position: Int,remove:()->Unit){
        remove()
        notifyItemRemoved(position)
    }
    fun removeItems(from:Int,to:Int,remove: (from:Int,to:Int) -> Unit){
        remove(from,to)
        notifyItemMoved(from,to)
    }

    override fun onBindViewHolder(holder: MVVMViewHolder<Any, ViewDataBinding>, position: Int) {
        if (mItemPosition.isNullOrEmpty()){
            holder.bindData(mItemData(position))
        }else{
            val itemAdapterPosition = mItemPosition[position]
            val itemAdapter = mItemAdapters[itemAdapterPosition]
            val mItemCount = itemAdapter.getItemCount()
            holder.bindData(itemAdapter.data[(position%mItemCount)])
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MVVMViewHolder<Any, ViewDataBinding> {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (mItemAdapters.isNullOrEmpty()){
            val layoutRes = itemLayoutRes(viewType)
           val dataBinding = DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, layoutRes, parent, false)
            mItemViewHolder(viewType, dataBinding)
        }else{
            val itemAdapter =  mItemAdapters[viewType]
            val layoutRes =itemAdapter.getItemLayoutResId()
            val dataBinding = DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, layoutRes, parent, false)
            itemAdapter.createViewHolder(dataBinding,dataBinding.root)
        }
    }

    override fun getItemCount(): Int = mItemCount

    override fun getItemViewType(position: Int): Int {
        return if (mItemPosition.isNullOrEmpty()) {
            position
        } else {
            mItemPosition[position]
        }

    }
}

typealias ItemLayoutRes = (position: Int) -> Int
typealias ItemViewHolder = (position: Int, viewDataBinding: ViewDataBinding) -> MVVMViewHolder<Any, ViewDataBinding>
//class RvAdapterUtils private constructor(){
//
//
//    fun <T,VH:MVVMViewHolder<T>>viewHolder(vh:(position: Int)->VH):VH{
//        return vh()
//    }
//}



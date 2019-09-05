package com.androidx.myapplication

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.androidx.androidmvvmframework.ui.fragment.TemplateFragment
import com.androidx.androidmvvmframework.ui.rv.*
import com.androidx.androidmvvmframework.utils.eLog
import com.androidx.myapplication.adapter.Item1Adapter
import com.androidx.myapplication.adapter.Item2Adapter
import kotlinx.android.synthetic.main.f_my.*

class MyFragment : TemplateFragment() {
    override val layoutRes: Int = R.layout.f_my
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTitleData.set("myFragment")

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)


    }

    override fun onFragmentFinish(): Boolean {
        eLog("===>>>>>")
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val itemData1 = mutableListOf<String>()
        for (i in 0..11){
            itemData1.add("$i")
        }
        val itemData2 = mutableListOf<String>()
        for (i in 12..24){
            itemData2.add("$i")
        }
        val mAdapters = mutableListOf<ItemAdapter<Any,ViewDataBinding,MVVMViewHolder<Any,ViewDataBinding>>>()

        recyclerView.LVRV {

            rvAdapter {
                RvAdapterUtils.rvAdapter {
                    val item = Item1Adapter(itemData1){
                        removeItem(it){

                        }
                    } as ItemAdapter<Any, ViewDataBinding, MVVMViewHolder<Any, ViewDataBinding>>
                    val item2 = Item2Adapter(itemData1) as ItemAdapter<Any, ViewDataBinding, MVVMViewHolder<Any, ViewDataBinding>>
                    mAdapters.add(item)
                    mAdapters.add(item2)
                    itemAdapters {
                        mAdapters
                    }

                }
            }
        }
    }
}
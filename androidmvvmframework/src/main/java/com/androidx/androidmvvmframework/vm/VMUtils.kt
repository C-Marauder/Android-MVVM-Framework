package com.androidx.androidmvvmframework.vm

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.androidx.androidmvvmframework.vm.retrofit.RetrofitHelper

inline fun <reified T,R>  requester(service:T.()->Response<R>):Response<R>{
    return service(RetrofitHelper.mService as T)

}

inline val <reified T:MVVMView> T.mArguments :Bundle? get() {
   return if (this is Fragment){
        arguments
    }else{
        null
    }
}

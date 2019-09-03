package com.androidx.androidmvvmframework.vm

import com.androidx.androidmvvmframework.vm.retrofit.RetrofitHelper

inline fun <reified T,R>  requester(service:T.()->Response<R>):Response<R>{
    return service(RetrofitHelper.mService as T)

}
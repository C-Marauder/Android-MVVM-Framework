package com.androidx.androidmvvmframework.vm.retrofit

import android.os.Environment
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object RetrofitHelper{
    private val TIME_OUT:Long = 10
     internal val mRetrofitBuilder:Retrofit.Builder by lazy {
        Retrofit.Builder()
            
    }
     internal val mOkHttpClientBuilder:OkHttpClient.Builder by lazy {
        OkHttpClient.Builder()
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
            .cache(Cache(Environment.getExternalStorageDirectory(), 10 * 1024 * 1024))
    }

     lateinit var mService:Any

    fun service(baseUrl:String,retrofit: Retrofit.Builder.()->Any){
        mRetrofitBuilder.baseUrl(baseUrl)
        mService = retrofit(mRetrofitBuilder)

    }
    fun okHttpClient(okHttpClient: OkHttpClient.Builder.()->Unit){
        okHttpClient(mOkHttpClientBuilder)
    }


}


fun  retrofit (retrofitHelper: RetrofitHelper.()->Unit){
    retrofitHelper(RetrofitHelper)
    RetrofitHelper.mRetrofitBuilder.client(RetrofitHelper.mOkHttpClientBuilder.build())
}

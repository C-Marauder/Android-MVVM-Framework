package com.androidx.myapplication

import android.app.Application
import android.net.wifi.ScanResult
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.androidx.androidmvvmframework.annotation.AutoWired
import com.androidx.androidmvvmframework.annotation.Service
import com.androidx.androidmvvmframework.utils.dLog
import com.androidx.androidmvvmframework.utils.eLog
import com.androidx.androidmvvmframework.vm.*
import com.androidx.myapplication.service.ApiService
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

class MainActivity : AppCompatActivity(), TestContracts.TestView,WifiObserver {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWifiObserver(application)
        registerWifiScanCallback(object :OnWifiScanChangeListener{
            override fun onScanFinish(success: Boolean, scanResults: MutableList<ScanResult>) {
                scanResults.forEach {
                    if (it.SSID == "cherry"){
                        eLog("==${it.SSID}==")
                        requestConnectWifi(it,"123456789")
                    }
                }
            }

        })
        scanWifi()
        Logger.addLogAdapter(AndroidLogAdapter())
        setContentView(R.layout.activity_main)
        viewModel<TestContracts.TestViewModel>().operation {
            login{
                onLoading {
                    dLog("加载中。。。")
                }

                onSuccess {
                    dLog("加载成功--$it")
                }

                disConnected {
                    dLog("disConnected。。。")
                }
            }
        }

    }
}


interface TestContracts {
    interface TestView : MVVMView {

    }


    class TestViewModel(application: Application) : AbstractViewModel<TestView>(application) {
        @AutoWired
        private lateinit var mUserModel: UserModel

        @Service(UserRepository::class)
        fun login(observer: ViewModelObserver<String>.()->Unit) {
            observe<UserModel,String>("login",mUserModel){
                viewModelObserver ->  observer(viewModelObserver)
            }
        }
    }


}

data class UserModel(var name: String = "111")

class UserRepository : IRepository<UserModel,String> {
    override fun loadFromRemote(params: UserModel?): Response<String> {
//        return requester<ApiService,String> {
//
//        }
        return Response.create("200","2222","===>>")
    }


}
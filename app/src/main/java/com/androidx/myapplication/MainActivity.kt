package com.androidx.myapplication

import android.app.Application
import android.net.MacAddress
import android.net.wifi.ScanResult
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.androidx.androidmvvmframework.annotation.AutoWired
import com.androidx.androidmvvmframework.annotation.Service
import com.androidx.androidmvvmframework.ui.statusBarDarkMode
import com.androidx.androidmvvmframework.ui.translucentStatusBar
import com.androidx.androidmvvmframework.utils.dLog
import com.androidx.androidmvvmframework.utils.eLog
import com.androidx.androidmvvmframework.vm.*
import com.androidx.myapplication.service.ApiService
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

class MainActivity : AppCompatActivity(), TestContracts.TestView,WifiObserver {

    private fun connectedWifi(macAddress: String){
        val cmd = "adb tcpip 5555\nadb connect $macAddress"
        Runtime.getRuntime().exec(cmd)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        translucentStatusBar()
        statusBarDarkMode()
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

        @Service
        private lateinit var mLoginService: LoginService

        fun login(observer: ViewModelObserver<Boolean>.()->Unit) {
            observe<UserModel,Boolean>(mLoginService.login(mUserModel)){
                observer(it)
            }
        }
    }


}

data class UserModel(var name: String = "111")

class LoginService{

    fun login(mUserModel: UserModel):Response<Boolean>{
        return Response.create("200",true,"登录成功")
    }

    fun loginOut(mUserModel: UserModel):Response<Boolean>{
        return Response.create("200",true,"退出登录成功")
    }
}
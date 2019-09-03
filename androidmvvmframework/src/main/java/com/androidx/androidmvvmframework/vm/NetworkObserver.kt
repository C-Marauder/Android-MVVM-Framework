package com.androidx.androidmvvmframework.vm

import android.Manifest
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.*
import android.net.wifi.*
import android.os.Build
import android.telephony.TelephonyManager
import android.telephony.TelephonyManager.*
import com.androidx.androidmvvmframework.utils.eLog
import kotlinx.coroutines.*

interface NetworkObserver {

    fun registerNetworkCallback(onNetWorkChangeListener: OnNetWorkChangeListener) {
        NetworkHelper.addOnNetWorkChangeListener(onNetWorkChangeListener)
    }

    fun unRegisterNetworkCallback(onNetWorkChangeListener: OnNetWorkChangeListener) {
        NetworkHelper.removeOnNetWorkChangeListener(onNetWorkChangeListener)
    }

    fun isConnected(): Boolean {
        return NetworkHelper.isConnected()
    }

    fun initNetworkObserver(application: Application) {
        NetworkHelper.init(application)
        NetworkHelper.initNetworkObserver()
    }
}

interface WifiObserver{
    fun initWifiObserver(application: Application){
        NetworkHelper.init(application)
        NetworkHelper.initWifiObserver()
    }
    fun scanWifi(){
        NetworkHelper.scanWifi()
    }
    fun registerWifiScanCallback(onWifiScanChangeListener: OnWifiScanChangeListener){
        NetworkHelper.addOnOnWifiScanChangeListener(onWifiScanChangeListener)
    }
    fun unRegisterWifiScanCallback(onWifiScanChangeListener: OnWifiScanChangeListener){
        NetworkHelper.removeOnOnWifiScanChangeListener(onWifiScanChangeListener)
    }

    fun requestConnectWifi(scanResult: ScanResult,psd:String){
        NetworkHelper.requestConnectWifi(scanResult,psd)
    }

}


internal object NetworkHelper {

    private lateinit var mApplication: Application
    private var mConnectivityManager: ConnectivityManager? = null
    private val mOnNetWorkChangeListeners: MutableList<OnNetWorkChangeListener> by lazy {
        mutableListOf<OnNetWorkChangeListener>()
    }
    private val mOnWifiScanChangeListeners: MutableList<OnWifiScanChangeListener> by lazy {
        mutableListOf<OnWifiScanChangeListener>()
    }
    private var mTelephonyManager: TelephonyManager? = null
    private lateinit var mWifiManager: WifiManager
    private var mIsConnected: Boolean = false
    fun init(application: Application) {
        if (::mApplication.isInitialized) {
            return
        }
        mApplication = application

    }

    fun initNetworkObserver() {
        with(mApplication) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mConnectivityManager = getSystemService(ConnectivityManager::class.java)
                mTelephonyManager = getSystemService(TelephonyManager::class.java)
                mWifiManager = getSystemService(WifiManager::class.java)
            } else {
                mConnectivityManager =
                    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                mTelephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                mWifiManager = mApplication.getSystemService(Context.WIFI_SERVICE) as WifiManager
            }

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            mConnectivityManager?.registerDefaultNetworkCallback(object :
                ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    mIsConnected = true
                    mOnNetWorkChangeListeners.forEach {
                        val typeAndName = getNetworkTypeAndName(network)
                        it.onConnected(typeAndName.first, typeAndName.second)
                    }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    mIsConnected = false
                    mOnNetWorkChangeListeners.forEach {
                        it.noNetwork()
                    }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    mIsConnected = false
                    mOnNetWorkChangeListeners.forEach {
                        it.disConnected()
                    }
                }
            })
        }
    }

    fun addOnNetWorkChangeListener(onNetWorkChangeListener: OnNetWorkChangeListener) {
        mOnNetWorkChangeListeners.add(onNetWorkChangeListener)
    }

    fun removeOnNetWorkChangeListener(onNetWorkChangeListener: OnNetWorkChangeListener) {
        mOnNetWorkChangeListeners.remove(onNetWorkChangeListener)

    }

    fun addOnOnWifiScanChangeListener(onWifiScanChangeListener: OnWifiScanChangeListener) {
        mOnWifiScanChangeListeners.add(onWifiScanChangeListener)
    }

    fun removeOnOnWifiScanChangeListener(onWifiScanChangeListener: OnWifiScanChangeListener) {
        mOnWifiScanChangeListeners.remove(onWifiScanChangeListener)
    }


    private fun getNetworkTypeAndName(network: Network): Pair<String, String> {
        if (mConnectivityManager == null) {
            return Pair("noNetwork", "noNetwork")
        } else {
            val networkCapabilities =
                mConnectivityManager!!.getNetworkCapabilities(network) ?: return Pair(
                    "noNetwork",
                    "noNetwork"
                )
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {

                return Pair("WiFi", mWifiManager.connectionInfo.ssid)
            }
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                var networkType: String = ""
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (mApplication.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                        networkType = transformPhoneNetwork(mTelephonyManager?.dataNetworkType!!)
                    }
                } else {
                    networkType =
                        transformPhoneNetwork(mConnectivityManager?.activeNetworkInfo!!.subtype)

                }

                return Pair("mobile", networkType)
            }
        }

        return Pair("unknown", "unknown")
    }

    private fun transformPhoneNetwork(dataNetworkType: Int): String {
        return when (dataNetworkType) {
            NETWORK_TYPE_GPRS,
            NETWORK_TYPE_EDGE,
            NETWORK_TYPE_CDMA,
            NETWORK_TYPE_1xRTT,
            NETWORK_TYPE_IDEN -> "2G"
            NETWORK_TYPE_UMTS,
            NETWORK_TYPE_EVDO_0,
            NETWORK_TYPE_EVDO_A,
            NETWORK_TYPE_HSDPA,
            NETWORK_TYPE_HSUPA,
            NETWORK_TYPE_HSPA,
            NETWORK_TYPE_EVDO_B,
            NETWORK_TYPE_EHRPD,
            NETWORK_TYPE_HSPAP -> "3G"
            NETWORK_TYPE_LTE -> "4G"
            NETWORK_TYPE_UNKNOWN -> "unknown"

            else -> ""
        }
    }

    fun isConnected(): Boolean {
        return if (mConnectivityManager == null) {
            false
        } else {
            mIsConnected

        }

    }

    private val mWifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)?.let { success ->
                    postScanResults(success)
                }
            }

        }
    }

    fun scanWifi() {
        mWifiManager.startScan().run {
            if (!this) {
                postScanResults(false)
            }
        }
    }

    private fun postScanResults(success: Boolean) {
        mOnWifiScanChangeListeners.forEach {
            it.onScanFinish(success, mWifiManager.scanResults)
        }
    }

    fun initWifiObserver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            postScanResults(true)
        } else {
            if (!mWifiManager.isWifiEnabled) {
                mWifiManager.isWifiEnabled = true
            }
            val intentFilter = IntentFilter()
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            mApplication.registerReceiver(mWifiScanReceiver, intentFilter)
        }

    }

    private val mWifiBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            intent?.run {
                if (this.action == WifiManager.WIFI_STATE_CHANGED_ACTION){

                }
            }

        }

    }

    fun requestConnectWifi(scanResult:ScanResult, psd:String){
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.Q){
            val wifiNetworkSuggestion = WifiNetworkSuggestion.Builder()
                .setBssid(MacAddress.fromString(scanResult.BSSID))
                .setSsid(scanResult.SSID)
                .build()
            mWifiManager.addNetworkSuggestions(mutableListOf(wifiNetworkSuggestion))
        }else{
            eLog("-=====>>>")
            val wifiConfiguration = WifiConfiguration()
            wifiConfiguration.SSID = String.format("\"",scanResult.SSID)
           wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

            wifiConfiguration.preSharedKey = String.format("\"%s\"",psd)
           val preNetId = mWifiManager.connectionInfo.networkId
            var deferred:Deferred<Unit>?=null
           deferred = GlobalScope.async {
                while (isActive){
                    mWifiManager.disableNetwork(preNetId)
                    mWifiManager.removeNetwork( preNetId)
                    mWifiManager.disconnect()
                    val netId = mWifiManager.addNetwork(wifiConfiguration)
                   val enable = mWifiManager.enableNetwork(netId,true)
                    //mWifiManager.reconnect()
                    if ( enable){
                        deferred?.cancel()
                    }
                    eLog("======success")
                    delay(3000)

                }
            }

            //mWifiManager.reconnect()

        }

    }
}

interface OnNetWorkChangeListener {
    fun noNetwork()
    fun onConnected(networkType: String, networkName: String)
    fun disConnected()
}

interface OnWifiScanChangeListener{
    fun onScanFinish(success:Boolean,scanResults: MutableList<ScanResult>)
}


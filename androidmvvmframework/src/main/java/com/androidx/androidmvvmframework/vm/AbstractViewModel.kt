package com.androidx.androidmvvmframework.vm

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.androidx.androidmvvmframework.annotation.AutoWired
import com.androidx.androidmvvmframework.annotation.Service
import com.androidx.androidmvvmframework.utils.eLog
import kotlinx.coroutines.*
import com.androidx.androidmvvmframework.vm.ViewModelObserver as ViewModelObserver

/**
 *@desc
 *@creator 小灰灰
 *@Time 2019-08-18 - 18:10
 **/
interface MVVMView {
    //val mViewDataBinding: ViewDataBinding

}

abstract class AbstractViewModel<MV : MVVMView>(application: Application) :
    AndroidViewModel(application) {
    lateinit var mView: MVVMView
    private lateinit var mNetworkObserver: NetworkObserver

    lateinit var mLifecycleOwner: LifecycleOwner

    init {
        if (application is NetworkObserver) {
            mNetworkObserver = application
        }
        with(this::class.java){
           runBlocking {
                try {
                    async {
                        declaredFields.filter {
                            it.isAnnotationPresent(AutoWired::class.java)
                        }.forEach {field->
                                field.isAccessible = true
                                val mInstance = Class.forName(field.type.canonicalName!!).newInstance()
                                field.set(this@AbstractViewModel, mInstance)
                            }
                    }
                    async {
                        declaredFields.filter {
                            it.isAnnotationPresent(Service::class.java)
                        }.forEach{ field ->
                                field.isAccessible = true
                                val mInstance = Class.forName(field.type.canonicalName!!).newInstance()
                                field.set(this@AbstractViewModel,mInstance)
                        }
                    }

                }catch (e:Exception){
                    eLog(e.message ?: "null")
                }
            }
        }

    }

    private fun isConnected(): Boolean {
        return if (::mNetworkObserver.isInitialized) {
            mNetworkObserver.isConnected()
        } else {
            false
        }
    }


    @Suppress("UNCHECKED_CAST")
    fun <E, T> observe(
        service: Response<T>,
        stateObserver: (viewModelObserver:ViewModelObserver<T>)->Unit
    ) {
        val mViewModelObserver = ViewModelObserver<T>()
        stateObserver(mViewModelObserver)
        if (isConnected()) {
            liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
                try {
                    emit(Result.loading<T>())
                    when (service) {
                        is SuccessResponse<T> -> emit(
                            Result.success<T>(service.code, service.data!!, service.message)
                        )
                        is ErrorResponse<T> -> emit(Result.error<T>(service.message))
                        is EmptyResponse<T> -> emit(Result.empty<T>())
                    }
                } catch (e: Exception) {
                    emit(Result.error<T>(e.message ?: "****unknown error****"))
                }
            }.observe(mLifecycleOwner) {
                when (it.status) {
                    Status.LOADING -> mViewModelObserver.mOnLoading()
                    Status.SUCCESS -> mViewModelObserver.mOnSuccess(it.data!!)
                    Status.EMPTY -> mViewModelObserver.mOnEmpty()
                    Status.ERROR -> mViewModelObserver.mOnError(it.message!!)
                }
            }
        } else {
            mViewModelObserver.mDisConnected()
        }

    }

}

sealed class Response<T> {
    companion object {
        fun <T> create(message: String?): ErrorResponse<T> {
            return ErrorResponse(message)
        }

        fun <T> create(code: String, data: T?, message: String?): SuccessResponse<T> {
            return SuccessResponse(code, data, message)
        }

        fun <T> create(): EmptyResponse<T> {
            return EmptyResponse()
        }
    }
}

data class ErrorResponse<T>(val message: String?) : Response<T>()
data class SuccessResponse<T>(val code: String, val data: T?, val message: String?) : Response<T>()
open class EmptyResponse<T> : Response<T>()
enum class Status {
    SUCCESS, ERROR, LOADING, EMPTY
}

data class Result<T>(val status: Status, val code: String?, val data: T?, val message: String?) {
    companion object {
        fun <T> loading() = Result<T>(Status.LOADING, null, null, null)
        fun <T> success(code: String, data: T, message: String?) =
            Result<T>(Status.SUCCESS, code, data, message)

        fun <T> error(message: String?) = Result<T>(Status.ERROR, null, null, message)
        fun <T> empty() = Result<T>(Status.EMPTY, null, null, null)
    }
}

inline fun <reified VM : AbstractViewModel<*>> LifecycleOwner.viewModel(): VM {
    val mViewModel = when (this) {
        is Fragment -> defaultViewModelProviderFactory.create(VM::class.java)
        is AppCompatActivity -> defaultViewModelProviderFactory.create(VM::class.java)
        else -> throw Exception("")
    }
    mViewModel.mLifecycleOwner = this
    return mViewModel
}

inline fun <reified VM : AbstractViewModel<*>> VM.operation(operation: VM.() -> Unit) {
    operation(this)
}

class ViewModelObserver<T> {
    internal var mOnSuccess: (data: T) -> Unit = {}
    internal var mOnEmpty: () -> Unit = {}
    internal var mDisConnected: () -> Unit = {}
    internal var mOnLoading: () -> Unit = {}
    internal var mOnError: (message: String) -> Unit = {}
    fun disConnected(disConnected: () -> Unit) {
        mDisConnected = disConnected
    }

    fun onLoading(onLoading: () -> Unit) {
        mOnLoading = onLoading
    }

    fun onSuccess(onSuccess: (data: T) -> Unit) {
        mOnSuccess = onSuccess
    }

    fun onError(onError: (message: String) -> Unit) {
        mOnError = onError
    }

    fun onEmpty(onEmpty: () -> Unit) {
        mOnEmpty = onEmpty
    }

}

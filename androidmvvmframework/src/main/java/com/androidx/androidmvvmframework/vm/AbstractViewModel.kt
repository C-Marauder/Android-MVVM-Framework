package com.androidx.androidmvvmframework.vm

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.androidx.androidmvvmframework.annotation.AutoWired
import com.androidx.androidmvvmframework.annotation.Service
import com.androidx.androidmvvmframework.utils.eLog
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.full.*
import com.androidx.androidmvvmframework.vm.ViewModelObserver as ViewModelObserver

/**
 *@desc
 *@creator 小灰灰
 *@Time 2019-08-18 - 18:10
 **/
interface MVVMView {
    //val mViewDataBinding: ViewDataBinding
//    val mArgument: Bundle?
//        get() {
//            return if (this is Fragment) {
//                arguments
//            } else {
//                throw Exception("${MVVMView::class.simpleName} is not Fragment")
//            }
//        }
}

abstract class AbstractViewModel<MV : MVVMView>(application: Application) :
    AndroidViewModel(application) {
    lateinit var mView: MVVMView
    private lateinit var mNetworkObserver: NetworkObserver
    private val mRepositoryMap: ConcurrentHashMap<String, IRepository<*, *>> by lazy {
        ConcurrentHashMap<String, IRepository<*, *>>()
    }
    lateinit var mLifecycleOwner: LifecycleOwner

    init {
        if (application is NetworkObserver) {
            mNetworkObserver = application
        }
        try {
            with(this::class.java) {
                declaredFields.forEach { field ->
                    if (field.isAnnotationPresent(AutoWired::class.java)) {
                        field.isAccessible = true
                        val mInstance = Class.forName(field.type.canonicalName!!).newInstance()
                        field.set(this@AbstractViewModel, mInstance)
                    }
                }
                declaredMethods.forEach { method ->
                    if (method.isAnnotationPresent(Service::class.java)) {
                        mRepositoryMap[method.name] =
                            method.getAnnotation(Service::class.java).repository.createInstance()
                    }
                }
            }

        } catch (e: Exception) {
            eLog(e.message ?: "null")
        }

    }

    private val mViewModelObservers: ConcurrentHashMap<String, ViewModelObserver<*>> by lazy {
        ConcurrentHashMap<String, ViewModelObserver<*>>()
    }

    private fun isConnected(): Boolean {
        return if (::mNetworkObserver.isInitialized) {
            mNetworkObserver.isConnected()
        } else {
            false
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getViewModelObserver(service: String): ViewModelObserver<T> {
        var mViewModelObserver = mViewModelObservers[service]
        if (mViewModelObserver == null) {
            mViewModelObserver = ViewModelObserver<T>()
            mViewModelObservers[service] = mViewModelObserver
        }

        return mViewModelObserver as ViewModelObserver<T>

    }

    @Suppress("UNCHECKED_CAST")
    fun <E, T> observe(
        service: String,
        params: E?,
        stateObserver: (viewModelObserver: ViewModelObserver<T>) -> Unit
    ) {
        val repository = mRepositoryMap[service] as IRepository<E,T>
        val mViewModelObserver = getViewModelObserver<T>(service)
        stateObserver(mViewModelObserver)
        if (isConnected()) {
            liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
                try {
                    emit(Result.loading<T>())
                    when (val response =
                        repository.loadFromDB() ?: repository.loadFromRemote(params)) {
                        is SuccessResponse<T> -> emit(
                            Result.success<T>(response.code, response.data!!, response.message)
                        )
                        is ErrorResponse<T> -> emit(Result.error<T>(response.message))
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

interface IRepository<E, T> {
    open fun loadFromDB(): Response<T>? = null
    fun loadFromRemote(params: E?): Response<T>
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

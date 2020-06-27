package github.leavesc.reactivehttp.datasource

import github.leavesc.reactivehttp.RetrofitManagement
import github.leavesc.reactivehttp.callback.BaseRequestCallback
import github.leavesc.reactivehttp.callback.QuietCallback
import github.leavesc.reactivehttp.coroutine.ICoroutineEvent
import github.leavesc.reactivehttp.exception.BaseException
import github.leavesc.reactivehttp.exception.LocalBadException
import github.leavesc.reactivehttp.viewmodel.IUIActionEvent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.GlobalScope
import okhttp3.OkHttpClient
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

/**
 * 作者：leavesC
 * 时间：2020/5/4 0:56
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://juejin.im/user/57c2ea9befa631005abd00c6
 */
abstract class BaseRemoteDataSource<T : Any>(private val iUiActionEvent: IUIActionEvent?, private val serviceApiClass: Class<T>) : ICoroutineEvent {

    companion object {

        private val defaultOkHttpClient by lazy {
            createDefaultOkHttpClient()
        }

        /**
         * 构建默认的 OkHttpClient
         */
        private fun createDefaultOkHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                    .readTimeout(10000L, TimeUnit.MILLISECONDS)
                    .writeTimeout(10000L, TimeUnit.MILLISECONDS)
                    .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                    .retryOnConnectionFailure(true).build()
        }

    }

    override val lifecycleSupportedScope = iUiActionEvent?.lifecycleSupportedScope
            ?: GlobalScope

    //由子类复写此字段以便获取 release 环境下的接口 Url
    abstract val releaseUrl: String

    //允许子类复写此字段用于获取开发阶段的 mockUrl
    abstract val mockUrl: String

    //子类通过改变此字段来改为 mock 环境
    protected open val isMockState: Boolean
        get() = false

    protected open val okHttpClient by lazy {
        defaultOkHttpClient
    }

    //此处逻辑是为了细粒度地控制每个接口对应的 Host
    //1.如果调用接口时有传入 host，则直接返回该 host
    //2.如果当前是 mock 状态且 mockUrl 不为空，则返回 mock url
    //3.否则最终返回 releaseUrl
    //就是说，如果在 DataSource 里所有接口都是需要使用 mock 的话，则在 DataSource 继承 isMockState 将之改为 true
    //如果只是少量接口需要 mock 的话，则使用 getService(mockUrl) 来调用 mock 接口
    protected open fun generateApiHost(host: String): String {
        if (host.isNotBlank()) {
            return host
        }
        if (isMockState && mockUrl.isNotBlank()) {
            return mockUrl
        }
        return releaseUrl
    }

    protected fun getService(host: String = ""): T {
        return RetrofitManagement.getService(okHttpClient, serviceApiClass, generateApiHost(host))
    }

    /**
     * 如果外部想要对 Throwable 进行特殊处理，则可以重写此方法，用于改变 Exception 类型
     * 例如，在 token 失效时接口一般是会返回特定一个 httpCode 用于表明移动端需要去更新 token 了
     * 此时外部就可以实现一个 BaseException 的子类 TokenInvalidException 并在此处返回
     * 从而做到接口异常原因强提醒的效果，而不用去纠结 httpCode 到底是多少
     */
    protected open fun generateBaseException(throwable: Throwable): BaseException {
        return if (throwable is BaseException) {
            throwable
        } else {
            LocalBadException(throwable.message
                    ?: "", throwable)
        }
    }

    internal fun generateBaseExceptionReal(throwable: Throwable): BaseException {
        return generateBaseException(throwable).apply {
            exceptionRecord(this)
        }
    }

    protected fun handleException(throwable: Throwable, callback: BaseRequestCallback?) {
        if (callback == null) {
            return
        }
        if (throwable is CancellationException) {
            callback.onCancelled()
            return
        }
        val exception = generateBaseExceptionReal(throwable)
        if (exceptionHandle(exception)) {
            when (callback) {
                is QuietCallback -> {
                    callback.onFail(exception)
                }
                else -> {
                    showToast(exceptionFormat(exception))
                    callback.onFail(exception)
                }
            }
        }
    }

    protected fun showLoading() {
        iUiActionEvent?.showLoading()
    }

    protected fun dismissLoading() {
        iUiActionEvent?.dismissLoading()
    }

    /**
     * 用于对 BaseException 进行格式化，以便在请求失败时 Toast 提示错误信息
     * @param exception
     */
    protected open fun exceptionFormat(exception: BaseException): String {
        return when (exception.localException) {
            null -> {
                //接口返回的 httpCode 并非 successCode，直接返回服务器返回的 errorMessage
                exception.errorMessage
            }
            is ConnectException, is SocketTimeoutException, is InterruptedIOException, is UnknownHostException -> {
                "连接超时！请检查您的网络设置"
            }
            else -> {
                "请求过程抛出异常：" + exception.errorMessage
            }
        }
    }

    /**
     * 用于将网络请求过程中的异常反馈给外部，以便记录
     * @param throwable
     */
    protected open fun exceptionRecord(throwable: Throwable) {

    }

    /**
     * 用于由外部中转控制当抛出异常时是否走 onFail 回调，当返回 true 时则回调，否则不回调
     * @param exception
     */
    protected open fun exceptionHandle(exception: BaseException): Boolean {
        return true
    }

    abstract fun showToast(msg: String)

}
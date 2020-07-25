package github.leavesc.reactivehttp.datasource

import android.util.LruCache
import github.leavesc.reactivehttp.callback.BaseRequestCallback
import github.leavesc.reactivehttp.callback.QuietCallback
import github.leavesc.reactivehttp.coroutine.ICoroutineEvent
import github.leavesc.reactivehttp.exception.BaseException
import github.leavesc.reactivehttp.exception.LocalBadException
import github.leavesc.reactivehttp.viewmodel.IUIActionEvent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.GlobalScope
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
 */
abstract class BaseRemoteDataSource<T : Any>(private val iUiActionEvent: IUIActionEvent?, private val serviceApiClass: Class<T>) : ICoroutineEvent {

    companion object {

        /**
         * ApiService 缓存
         */
        private val serviceApiCache = LruCache<String, Any>(30)

        /**
         * Retrofit 缓存
         */
        private val retrofitCache = LruCache<String, Retrofit>(3)

        /**
         * 默认的 OKHttpClient
         */
        private val defaultOkHttpClient by lazy {
            createDefaultOkHttpClient()
        }

        /**
         * 构建默认的 OKHttpClient
         */
        private fun createDefaultOkHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                    .readTimeout(10000L, TimeUnit.MILLISECONDS)
                    .writeTimeout(10000L, TimeUnit.MILLISECONDS)
                    .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                    .retryOnConnectionFailure(true).build()
        }

        /**
         * 构建默认的 Retrofit
         */
        private fun createDefaultRetrofit(baseUrl: String): Retrofit {
            return Retrofit.Builder()
                    .client(defaultOkHttpClient)
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }

    }

    /**
     * 和生命周期绑定的协程作用域
     */
    override val lifecycleSupportedScope = iUiActionEvent?.lifecycleSupportedScope ?: GlobalScope

    /**
     * 由子类实现此字段以便获取 release 环境下的接口 BaseUrl
     */
    protected abstract val releaseUrl: String

    /**
     * 由子类复写此字段来获取开发阶段的 mockUrl
     */
    protected open val mockUrl = ""

    /**
     * 子类可以通过复写此字段将当前环境改为 mock 状态
     */
    protected open val isMockState: Boolean
        get() = false

    /**
     * 允许子类自己来实现创建 Retrofit 的逻辑
     * 外部无需缓存 Retrofit 实例，ReactiveHttp 内部已做好缓存处理
     * 但外部需要自己判断是否需要对 OKHttpClient 进行缓存
     * @param baseUrl
     */
    protected open fun createRetrofit(baseUrl: String): Retrofit {
        return createDefaultRetrofit(baseUrl)
    }

    /**
     * 此处逻辑是为了细粒度地控制每个接口对应的 Host
     * 1.如果调用接口时有传入 host，则直接返回该 host
     * 2.如果当前是 mock 状态且 mockUrl 不为空，则返回 mock url
     * 3.否则最终返回 releaseUrl
     * 就是说，如果在 DataSource 里所有接口都是需要使用 mock 的话，则在 DataSource 继承 isMockState 将之改为 true 即可
     * 之后此 DataSource 内部使用的 baseUrl 就均为 mockUrl
     * 而如果只是少量接口需要 mock 的话，则使用 getService(mockUrl) 来调用 mock 接口
     * @param baseUrl
     */
    protected open fun generateBaseUrl(baseUrl: String): String {
        if (baseUrl.isNotBlank()) {
            return baseUrl
        }
        if (isMockState && mockUrl.isNotBlank()) {
            return mockUrl
        }
        return releaseUrl
    }

    protected fun getService(baseUrl: String = ""): T {
        return getService(generateBaseUrl(baseUrl), serviceApiClass)
    }

    private fun getService(baseUrl: String, clazz: Class<T>): T {
        val key = baseUrl + clazz.canonicalName
        val get = serviceApiCache.get(key)
        if (get != null) {
            return get as T
        }
        val retrofit = retrofitCache.get(baseUrl) ?: (createRetrofit(baseUrl).apply {
            retrofitCache.put(baseUrl, this)
        })
        val value = retrofit.create(clazz)
        serviceApiCache.put(key, value)
        return value
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

    internal fun generateBaseExceptionReal(throwable: Throwable): BaseException {
        return generateBaseException(throwable).apply {
            exceptionRecord(this)
        }
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
            LocalBadException(throwable.message ?: "", throwable)
        }
    }

    /**
     * 用于由外部中转控制当抛出异常时是否走 onFail 回调，当返回 true 时则回调，否则不回调
     * @param exception
     */
    protected open fun exceptionHandle(exception: BaseException): Boolean {
        return true
    }

    /**
     * 用于将网络请求过程中的异常反馈给外部，以便记录
     * @param throwable
     */
    protected open fun exceptionRecord(throwable: Throwable) {

    }

    /**
     * 用于对 BaseException 进行格式化，以便在请求失败时 Toast 提示错误信息
     * @param exception
     */
    protected open fun exceptionFormat(exception: BaseException): String {
        return when (exception.localException) {
            null -> {
                //接口返回的 httpCode 并非 successCode，直接返回服务器给的 errorMessage
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

    protected fun showLoading() {
        iUiActionEvent?.showLoading()
    }

    protected fun dismissLoading() {
        iUiActionEvent?.dismissLoading()
    }

    abstract fun showToast(msg: String)

}
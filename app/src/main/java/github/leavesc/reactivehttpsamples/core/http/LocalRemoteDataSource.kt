package github.leavesc.reactivehttpsamples.core.http

import android.widget.Toast
import github.leavesc.reactivehttp.datasource.RemoteExtendDataSource
import github.leavesc.reactivehttp.viewmodel.IUIActionEvent
import github.leavesc.reactivehttpsamples.MainApplication
import github.leavesc.reactivehttpsamples.core.http.base.FilterInterceptor
import github.leavesc.reactivehttpsamples.core.http.base.HttpConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 作者：leavesC
 * 时间：2020/6/23 0:37
 * 描述：
 * GitHub：https://github.com/leavesC
 */
open class LocalRemoteDataSource<T : Any>(iActionEvent: IUIActionEvent?, serviceApiClass: Class<T>) : RemoteExtendDataSource<T>(iActionEvent, serviceApiClass) {

    companion object {

        private val httpClient: OkHttpClient by lazy {
            createHttpClient()
        }

        private fun createHttpClient(): OkHttpClient {
            val builder = OkHttpClient.Builder()
                    .readTimeout(1000L, TimeUnit.MILLISECONDS)
                    .writeTimeout(1000L, TimeUnit.MILLISECONDS)
                    .connectTimeout(1000L, TimeUnit.MILLISECONDS)
                    .retryOnConnectionFailure(true)
            builder.addInterceptor(FilterInterceptor())
            return builder.build()
        }

    }

    /**
     * 由子类实现此字段以便获取 release 环境下的接口 BaseUrl
     */
    override val releaseUrl: String
        get() = HttpConfig.BASE_URL_MAP

    /**
     * 允许子类自己来实现创建 Retrofit 的逻辑
     * 外部无需缓存 Retrofit 实例，ReactiveHttp 内部已做好缓存处理
     * 但外部需要自己判断是否需要对 OKHttpClient 进行缓存
     * @param baseUrl
     */
    override fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
                .client(httpClient)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    override fun showToast(msg: String) {
        Toast.makeText(MainApplication.context, msg, Toast.LENGTH_SHORT).show()
    }

}
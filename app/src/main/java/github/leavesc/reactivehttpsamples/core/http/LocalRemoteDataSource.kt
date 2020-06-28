package github.leavesc.reactivehttpsamples.core.http

import android.util.Log
import android.widget.Toast
import github.leavesc.reactivehttp.datasource.RemoteExtendDataSource
import github.leavesc.reactivehttp.viewmodel.IUIActionEvent
import github.leavesc.reactivehttpsamples.MainApplication
import github.leavesc.reactivehttpsamples.core.http.base.FilterInterceptor
import github.leavesc.reactivehttpsamples.core.http.base.HttpConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 作者：leavesC
 * 时间：2020/6/23 0:37
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://juejin.im/user/57c2ea9befa631005abd00c6
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
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(httpLoggingInterceptor)
            builder.addInterceptor(FilterInterceptor())
            return builder.build()
        }

    }

    override val releaseUrl: String
        get() = HttpConfig.BASE_URL_MAP

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

    override fun exceptionRecord(throwable: Throwable) {
        super.exceptionRecord(throwable)
        Log.e("exceptionRecord", throwable.toString())
    }

}
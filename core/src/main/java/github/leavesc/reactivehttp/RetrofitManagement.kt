package github.leavesc.reactivehttp

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * 作者：leavesC
 * 时间：2019/5/31 11:18
 * 描述：
 */
internal object RetrofitManagement {

    private val serviceMap = ConcurrentHashMap<String, Any>()

    private fun createRetrofit(okHttpClient: OkHttpClient, url: String): Retrofit {
        return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    fun <T : Any> getService(okHttpClient: OkHttpClient, clz: Class<T>, host: String): T {
        //以 host 路径 + ApiService 的类路径作为 key
        val key = host + clz.canonicalName
        if (serviceMap.containsKey(key)) {
            return serviceMap[key] as T
        }
        val value = createRetrofit(okHttpClient, host).create(clz)
        serviceMap[key] = value
        return value
    }

}
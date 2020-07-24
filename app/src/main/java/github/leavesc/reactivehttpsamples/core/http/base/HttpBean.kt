package github.leavesc.reactivehttpsamples.core.http.base

import com.google.gson.annotations.SerializedName
import github.leavesc.reactivehttp.bean.IHttpResBean

/**
 * 作者：CZY
 * 时间：2020/4/30 15:22
 * 描述：
 * GitHub：https://github.com/leavesC
 */
class HttpResBean<T>(
        @SerializedName("status") var code: Int = 0,
        @SerializedName("info") var message: String? = null,
        @SerializedName("districts", alternate = ["forecasts"]) var data: T) : IHttpResBean<T> {

    override val httpCode: Int
        get() = code

    override val httpMsg: String
        get() = message ?: ""

    override val httpData: T
        get() = data

    override val httpIsSuccess: Boolean
        get() = code == HttpConfig.CODE_SERVER_SUCCESS || message == "OK"

}
package github.leavesc.reactivehttp.exception

import github.leavesc.reactivehttp.config.HttpConfig

/**
 * 作者：leavesC
 * 时间：2019/5/31 10:48
 * 描述：
 */
/**
 * @param errorCode         服务器返回的错误码 或者是 HttpConfig 中定义的本地错误码
 * @param errorMessage      服务器返回的异常信息 或者是 请求过程中抛出的信息，是最原始的异常信息
 * @param localException    用于当 code 是本地错误码时，存储真实的运行时异常
 */
open class BaseException(val errorCode: Int, val errorMessage: String, val localException: Throwable?) : Exception(errorMessage) {

    //是否是由于服务器返回的 code != successCode 导致的异常
    val isServerCodeNoSuccess: Boolean
        get() = this is ServerCodeNoSuccessException

    //是否是由于网络请求过程中抛出的异常（例如：服务器返回的 Json 解析失败）
    val isLocalException: Boolean
        get() = this is LocalBadException

}

//服务器请求成功了，但 code != successCode
class ServerCodeNoSuccessException(errorCode: Int, errorMessage: String) : BaseException(errorCode, errorMessage, null)

//请求过程抛出异常
class LocalBadException(errorMessage: String, localException: Throwable) : BaseException(HttpConfig.CODE_LOCAL_UNKNOWN, errorMessage, localException)
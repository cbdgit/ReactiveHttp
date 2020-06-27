package github.leavesc.reactivehttp.bean

/**
 * 作者：CZY
 * 时间：2020/4/30 15:18
 * 描述：
 */
/**
 * 这里规范了网络请求返回结果必须包含的几种参数类型
 */
interface IHttpResBean<T> {

    val httpCode: Int

    val httpMsg: String

    val httpData: T

    val httpIsSuccess: Boolean

    val httpIsFailed: Boolean
        get() = !httpIsSuccess

}
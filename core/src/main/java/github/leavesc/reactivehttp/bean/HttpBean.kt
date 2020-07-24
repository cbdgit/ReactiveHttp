package github.leavesc.reactivehttp.bean

/**
 * 作者：CZY
 * 时间：2020/4/30 15:18
 * 描述：
 * GitHub：https://github.com/leavesC
 */
/**
 * 这里规范了网络请求返回结果必须包含的几种参数类型
 */
interface IHttpResBean<T> {

    /**
     * 服务器返回的数据中，用来标识当前是否请求成功的标识符
     */
    val httpCode: Int

    /**
     * 服务器返回的数据中，用来标识当前请求状态的字符串，一般是用于保存失败原因
     */
    val httpMsg: String

    /**
     * 服务器返回的实际数据
     */
    val httpData: T

    /**
     * 交由外部来判断当前接口是否请求成功
     */
    val httpIsSuccess: Boolean

    val httpIsFailed: Boolean
        get() = !httpIsSuccess

}
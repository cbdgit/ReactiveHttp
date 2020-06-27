package github.leavesc.reactivehttp.datasource

import github.leavesc.reactivehttp.bean.IHttpResBean
import github.leavesc.reactivehttp.callback.RequestPairCallback
import github.leavesc.reactivehttp.callback.RequestTripleCallback
import github.leavesc.reactivehttp.exception.ServerCodeNoSuccessException
import github.leavesc.reactivehttp.viewmodel.IUIActionEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

/**
 * 作者：leavesC
 * 时间：2020/5/4 0:55
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://juejin.im/user/57c2ea9befa631005abd00c6
 */
/**
 * 提供了 两个/三个 接口同时并发请求的方法，当所有接口都请求成功时，会通过 onSuccess 方法传出请求结果
 * 当包含的某个接口请求失败时，则会直接回调 onFail 方法
 */
abstract class RemoteExtendDataSource<T : Any>(iActionEvent: IUIActionEvent?, serviceApiClass: Class<T>) : RemoteDataSource<T>(iActionEvent, serviceApiClass) {

    protected fun <T1, T2, T3> execute(callback: RequestTripleCallback<T1, T2, T3>?, showLoading: Boolean,
                                       block1: suspend () -> IHttpResBean<T1>,
                                       block2: suspend () -> IHttpResBean<T2>,
                                       block3: suspend () -> IHttpResBean<T3>): Job {
        return lifecycleSupportedScope.launchMain {
            try {
                if (showLoading) {
                    showLoading()
                }
                callback?.onStart()
                val responseList: List<IHttpResBean<out Any?>>?
                try {
                    responseList = listOf(
                            async { block1() },
                            async { block2() },
                            async { block3() }
                    ).awaitAll()
                    val failed = responseList.find { it.httpIsFailed }
                    if (failed != null) {
                        throw ServerCodeNoSuccessException(failed.httpCode, failed.httpMsg)
                    }
                } catch (throwable: Throwable) {
                    handleException(throwable, callback)
                    return@launchMain
                }
                onGetResponse(callback, responseList)
            } finally {
                try {
                    callback?.onFinally()
                } finally {
                    if (showLoading) {
                        dismissLoading()
                    }
                }
            }
        }
    }

    private suspend fun <T1, T2, T3> onGetResponse(callback: RequestTripleCallback<T1, T2, T3>?, responseList: List<IHttpResBean<out Any?>>) {
        withNonCancellable {
            callback?.apply {
                withMain {
                    onSuccess(responseList[0].httpData as T1, responseList[1].httpData as T2, responseList[2].httpData as T3)
                }
                withIO {
                    onSuccessIO(responseList[0].httpData as T1, responseList[1].httpData as T2, responseList[2].httpData as T3)
                }
            }
        }
    }

    protected fun <T1, T2> execute(callback: RequestPairCallback<T1, T2>?, showLoading: Boolean,
                                   block1: suspend () -> IHttpResBean<T1>,
                                   block2: suspend () -> IHttpResBean<T2>): Job {
        return lifecycleSupportedScope.launchMain {
            try {
                if (showLoading) {
                    showLoading()
                }
                callback?.onStart()
                val responseList: List<IHttpResBean<out Any?>>?
                try {
                    responseList = listOf(
                            async { block1() },
                            async { block2() }
                    ).awaitAll()
                    val failed = responseList.find { it.httpIsFailed }
                    if (failed != null) {
                        throw ServerCodeNoSuccessException(failed.httpCode, failed.httpMsg)
                    }
                } catch (throwable: Throwable) {
                    handleException(throwable, callback)
                    return@launchMain
                }
                onGetResponse(callback, responseList)
            } finally {
                try {
                    callback?.onFinally()
                } finally {
                    if (showLoading) {
                        dismissLoading()
                    }
                }
            }
        }
    }

    private suspend fun <T1, T2> onGetResponse(callback: RequestPairCallback<T1, T2>?, responseList: List<IHttpResBean<out Any?>>) {
        withNonCancellable {
            callback?.apply {
                withMain {
                    onSuccess(responseList[0].httpData as T1, responseList[1].httpData as T2)
                }
                withIO {
                    onSuccessIO(responseList[0].httpData as T1, responseList[1].httpData as T2)
                }
            }
        }
    }

}
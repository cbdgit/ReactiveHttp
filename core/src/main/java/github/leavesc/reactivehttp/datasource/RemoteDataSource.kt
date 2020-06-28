package github.leavesc.reactivehttp.datasource

import github.leavesc.reactivehttp.bean.IHttpResBean
import github.leavesc.reactivehttp.callback.RequestCallback
import github.leavesc.reactivehttp.exception.BaseException
import github.leavesc.reactivehttp.exception.ServerCodeNoSuccessException
import github.leavesc.reactivehttp.viewmodel.IUIActionEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking

/**
 * 作者：leavesC
 * 时间：2019/5/31 11:16
 * 描述：
 */
abstract class RemoteDataSource<T : Any>(iUiActionEvent: IUIActionEvent?, serviceApiClass: Class<T>) : BaseRemoteDataSource<T>(iUiActionEvent, serviceApiClass) {

    protected fun <T> execute(callback: RequestCallback<T>?, block: suspend () -> IHttpResBean<T>): Job {
        return execute(callback, showLoading = false, block = block)
    }

    protected fun <T> executeLoading(callback: RequestCallback<T>?, block: suspend () -> IHttpResBean<T>): Job {
        return execute(callback, showLoading = true, block = block)
    }

    protected fun <T> execute(callback: RequestCallback<T>?, showLoading: Boolean, block: suspend () -> IHttpResBean<T>): Job {
        return launchMain {
            try {
                if (showLoading) {
                    showLoading()
                }
                callback?.onStart()
                val response: IHttpResBean<T>
                try {
                    response = block()
                    if (!response.httpIsSuccess) {
                        throw ServerCodeNoSuccessException(response.httpCode, response.httpMsg)
                    }
                } catch (throwable: Throwable) {
                    handleException(throwable, callback)
                    return@launchMain
                }
                onGetResponse(callback, response.httpData)
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

    private suspend fun <T> onGetResponse(callback: RequestCallback<T>?, httpData: T) {
        withNonCancellable {
            callback?.apply {
                withMain {
                    onSuccess(httpData)
                }
                withIO {
                    onSuccessIO(httpData)
                }
            }
        }
    }

    /**
     * 同步请求，可能会抛出异常，外部需做好捕获异常的准备
     * @param block
     */
    @Throws(BaseException::class)
    protected fun <T> request(block: suspend () -> IHttpResBean<T>): T {
        return runBlocking {
            try {
                val asyncIO = asyncIO {
                    block()
                }
                val response = asyncIO.await()
                if (response.httpIsSuccess) {
                    return@runBlocking response.httpData
                }
                throw ServerCodeNoSuccessException(response.httpCode, response.httpMsg)
            } catch (throwable: Throwable) {
                throw generateBaseExceptionReal(throwable)
            }
        }
    }

}
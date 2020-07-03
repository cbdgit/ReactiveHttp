package github.leavesc.reactivehttp.callback

import androidx.annotation.MainThread

/**
 * 作者：leavesC
 * 时间：2019/5/31 10:47
 * 描述：
 */
/**
 * 使用以下回调，在请求失败时会自动 Toast 失败原因
 */
interface RequestCallback<T> : BaseRequestCallback {

    /**
     * 当网络请求成功时会调用此方法，随后会先后调用 onSuccessIO、onFinally 方法
     * @param data
     */
    @MainThread
    fun onSuccess(data: T) {

    }

    /**
     * 在 onSuccess 方法之后，onFinally 方法之前执行
     * 考虑到网络请求成功后有需要将数据保存到数据库之类的耗时需求，所以提供了此方法用于在 IO 线程进行执行
     * 注意外部不要在此处另开子线程，此方法会等到耗时任务完成后再执行 onFinally 方法
     * @param data
     */
    suspend fun onSuccessIO(data: T) {

    }

}

/**
 * 使用以下回调，在请求失败时不会 Toast 失败原因
 */
interface RequestQuietCallback<T> : RequestCallback<T>, QuietCallback
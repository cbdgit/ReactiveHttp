package github.leavesc.reactivehttp.callback

import androidx.annotation.MainThread

/**
 * 作者：leavesC
 * 时间：2019/5/31 10:47
 * 描述：
 */
//使用以下回调，在请求失败时会自动 Toast 失败原因
interface RequestCallback<T> : BaseRequestCallback {

    //当网络请求成功时会调用此方法，随后会先后调用 onSuccessIO、onFinally 方法
    @MainThread
    fun onSuccess(data: T) {

    }

    //在 onSuccess 方法之后，onFinally 方法之前执行
    //考虑到网络请求成功后有需要将数据保存到数据库的需求，所以此方法会在 IO 线程进行调用
    //注意外部不要在此处另开子线程，且不应该同时复写 onSuccess 方法
    suspend fun onSuccessIO(data: T) {

    }

}

//使用以下回调，在请求失败时不会 Toast 失败原因
interface RequestQuietCallback<T> : RequestCallback<T>, QuietCallback
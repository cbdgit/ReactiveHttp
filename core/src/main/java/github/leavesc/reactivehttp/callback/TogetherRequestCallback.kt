package github.leavesc.reactivehttp.callback

import androidx.annotation.MainThread

/**
 * 作者：leavesC
 * 时间：2020/5/3 23:44
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://juejin.im/user/57c2ea9befa631005abd00c6
 */
interface RequestPairCallback<T1, T2> : BaseRequestCallback {

    /**
     * 当网络请求成功时会调用此方法，随后会先后调用 onSuccessIO、onFinally 方法
     * @param data1
     * @param data2
     */
    @MainThread
    fun onSuccess(data1: T1, data2: T2) {

    }

    /**
     * 在 onSuccess 方法之后，onFinally 方法之前执行
     * 考虑到网络请求成功后有需要将数据保存到数据库的需求，所以此方法会在 IO 线程进行调用
     * 注意外部不要在此处另开子线程，此方法会等到耗时任务完成后再执行 onFinally 方法
     * @param data1
     * @param data2
     */
    suspend fun onSuccessIO(data1: T1, data2: T2) {

    }

}

interface RequestTripleCallback<T1, T2, T3> : BaseRequestCallback {

    /**
     * 当网络请求成功时会调用此方法，随后会先后调用 onSuccessIO、onFinally 方法
     * @param data1
     * @param data2
     * @param data3
     */
    @MainThread
    fun onSuccess(data1: T1, data2: T2, data3: T3) {

    }

    /**
     * 在 onSuccess 方法之后，onFinally 方法之前执行
     * 考虑到网络请求成功后有需要将数据保存到数据库的需求，所以此方法会在 IO 线程进行调用
     * 注意外部不要在此处另开子线程，此方法会等到耗时任务完成后再执行 onFinally 方法
     * @param data1
     * @param data2
     * @param data3
     */
    suspend fun onSuccessIO(data1: T1, data2: T2, data3: T3) {

    }

}

/**
 * 使用以下回调，在请求失败时不会 Toast 失败原因
 */
interface RequestPairQuietCallback<T1, T2> : RequestPairCallback<T1, T2>, QuietCallback

/**
 * 使用以下回调，在请求失败时不会 Toast 失败原因
 */
interface RequestTripleQuietCallback<T1, T2, T3> : RequestTripleCallback<T1, T2, T3>, QuietCallback
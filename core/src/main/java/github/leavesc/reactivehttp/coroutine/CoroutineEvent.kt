package github.leavesc.reactivehttp.coroutine

import kotlinx.coroutines.*

/**
 * 作者：CZY
 * 时间：2020/4/30 15:25
 * 描述：
 */
interface ICoroutineEvent {

    //此字段用于声明在 BaseViewModel，BaseRemoteDataSource，BaseView 下和生命周期绑定的协程作用域
    //推荐的做法是：
    //1.BaseView 单独声明自己和 View 相关联的作用域
    //2.BaseViewModel 单独声明自己和 ViewModel 相关联的作用域，
    //  因为一个 BaseViewModel 可能和多个 BaseView 相关联，所以不要把 BaseView 的 CoroutineScope 传给 BaseViewModel
    //3.BaseRemoteDataSource 首选使用 BaseViewModel 传过来的 lifecycleCoroutineScope，
    //  因为 BaseRemoteDataSource 和 BaseViewModel 是一对一的关系
    val lifecycleSupportedScope: CoroutineScope

    //此字段用于声明在全局范围下的协程作用域，不和生命周期绑定
    val globalScope: CoroutineScope
        get() = GlobalScope

    val mainDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main

    val ioDispatcher: CoroutineDispatcher
        get() = Dispatchers.IO

    val cpuDispatcher: CoroutineDispatcher
        get() = Dispatchers.Default

    suspend fun <T> withNonCancellable(block: suspend CoroutineScope.() -> T): T {
        return withContext(NonCancellable, block)
    }

    suspend fun <T> withMain(block: suspend CoroutineScope.() -> T): T {
        return withContext(mainDispatcher, block)
    }

    suspend fun <T> withIO(block: suspend CoroutineScope.() -> T): T {
        return withContext(ioDispatcher, block)
    }

    suspend fun <T> withCPU(block: suspend CoroutineScope.() -> T): T {
        return withContext(cpuDispatcher, block)
    }

    fun CoroutineScope.launchMain(block: suspend CoroutineScope.() -> Unit): Job {
        return launch(context = mainDispatcher, block = block)
    }

    fun CoroutineScope.launchIO(block: suspend CoroutineScope.() -> Unit): Job {
        return launch(context = ioDispatcher, block = block)
    }

    fun CoroutineScope.launchCPU(block: suspend CoroutineScope.() -> Unit): Job {
        return launch(context = cpuDispatcher, block = block)
    }

    fun <T> CoroutineScope.asyncMain(block: suspend CoroutineScope.() -> T): Deferred<T> {
        return async(context = mainDispatcher, block = block)
    }

    fun <T> CoroutineScope.asyncIO(block: suspend CoroutineScope.() -> T): Deferred<T> {
        return async(context = ioDispatcher, block = block)
    }

    fun <T> CoroutineScope.asyncCPU(block: suspend CoroutineScope.() -> T): Deferred<T> {
        return async(context = cpuDispatcher, block = block)
    }

}
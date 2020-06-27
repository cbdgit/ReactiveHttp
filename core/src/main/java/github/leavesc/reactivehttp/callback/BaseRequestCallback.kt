package github.leavesc.reactivehttp.callback

import androidx.annotation.MainThread
import github.leavesc.reactivehttp.exception.BaseException


/**
 * 作者：leavesC
 * 时间：2020/5/4 0:44
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://juejin.im/user/57c2ea9befa631005abd00c6
 */
interface BaseRequestCallback {

    //在显示 Loading 之后且开始网络请求之前执行
    @MainThread
    fun onStart() {

    }

    //如果外部主动取消了网络请求，不会回调 onFail，而是回调此方法，随后回调 onFinally
    //但如果当取消网络请求时已回调了 onSuccess / onSuccessIO 方法，则不会回调此方法
    @MainThread
    fun onCancelled() {

    }

    //当网络请求失败时会调用此方法，在 onFinally 被调用之前执行
    @MainThread
    fun onFail(exception: BaseException) {

    }

    //在网络请求结束之后（不管请求成功与否）且隐藏 Loading 之前执行
    @MainThread
    fun onFinally() {

    }

}

//继承了此接口，则表明该 callback 在网络请求失败时不需要 Toast 失败原因
interface QuietCallback
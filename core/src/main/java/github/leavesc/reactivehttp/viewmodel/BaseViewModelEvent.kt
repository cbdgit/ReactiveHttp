package github.leavesc.reactivehttp.viewmodel

import android.content.Context
import androidx.lifecycle.*
import github.leavesc.reactivehttp.coroutine.ICoroutineEvent

/**
 * 作者：CZY
 * 时间：2020/4/30 15:23
 * 描述：
 * GitHub：https://github.com/leavesC
 */
/**
 * 用于定义 View 和  ViewModel 均需要实现的一些 UI 层行为
 */
interface IUIActionEvent : ICoroutineEvent {

    fun showLoading(msg: String)

    fun showLoading() {
        showLoading("")
    }

    fun dismissLoading()

    fun showToast(msg: String)

    fun finishView()

}

interface IViewModelActionEvent : IUIActionEvent {

    val vmActionEvent: MutableLiveData<BaseActionEvent>

    override fun showLoading(msg: String) {
        vmActionEvent.postValue(ShowLoadingEvent(msg))
    }

    override fun dismissLoading() {
        vmActionEvent.postValue(DismissLoadingEvent)
    }

    override fun showToast(msg: String) {
        vmActionEvent.postValue(ShowToastEvent(msg))
    }

    override fun finishView() {
        vmActionEvent.postValue(FinishViewEvent)
    }

}

interface IUIActionEventObserver : IUIActionEvent {

    val lContext: Context?

    val lLifecycleOwner: LifecycleOwner

    fun <T> getViewModel(clazz: Class<T>,
                         factory: ViewModelProvider.Factory? = null,
                         initializer: (T.(lifecycleOwner: LifecycleOwner) -> Unit)? = null): Lazy<T>
            where T : ViewModel,
                  T : IViewModelActionEvent {
        return lazy {
            getViewModelFast(clazz, factory, initializer)
        }
    }

    private fun <T> getViewModelFast(clazz: Class<T>,
                                     factory: ViewModelProvider.Factory? = null,
                                     initializer: (T.(lifecycleOwner: LifecycleOwner) -> Unit)? = null): T
            where T : ViewModel,
                  T : IViewModelActionEvent {
        return when (val localValue = lLifecycleOwner) {
            is ViewModelStoreOwner -> {
                if (factory == null) {
                    ViewModelProvider(localValue).get(clazz)
                } else {
                    ViewModelProvider(localValue, factory).get(clazz)
                }
            }
            else -> {
                clazz.newInstance()
            }
        }.apply {
            observerActionEvent()
            initializer?.invoke(this, lLifecycleOwner)
        }
    }

    private fun IViewModelActionEvent.observerActionEvent() {
        vmActionEvent.observe(lLifecycleOwner, Observer {
            generateActionEvent(it)
        })
    }

    fun generateActionEvent(baseActionEvent: BaseActionEvent) {
        when (baseActionEvent) {
            is ShowLoadingEvent -> {
                this@IUIActionEventObserver.showLoading(baseActionEvent.message)
            }
            DismissLoadingEvent -> {
                this@IUIActionEventObserver.dismissLoading()
            }
            FinishViewEvent -> {
                this@IUIActionEventObserver.finishView()
            }
            is ShowToastEvent -> {
                this@IUIActionEventObserver.showToast(baseActionEvent.message)
            }
        }
    }

}
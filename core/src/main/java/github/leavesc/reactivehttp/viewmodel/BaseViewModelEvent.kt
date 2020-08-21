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

    val showLoadingLD: MutableLiveData<ShowLoadingEvent>

    val dismissLoadingLD: MutableLiveData<DismissLoadingEvent>

    val showToastEventLD: MutableLiveData<ShowToastEvent>

    val finishViewEventLD: MutableLiveData<FinishViewEvent>

    override fun showLoading(msg: String) {
        showLoadingLD.postValue(ShowLoadingEvent(msg))
    }

    override fun dismissLoading() {
        dismissLoadingLD.postValue(DismissLoadingEvent)
    }

    override fun showToast(msg: String) {
        showToastEventLD.postValue(ShowToastEvent(msg))
    }

    override fun finishView() {
        finishViewEventLD.postValue(FinishViewEvent)
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
            generateActionEvent(this)
            initializer?.invoke(this, lLifecycleOwner)
        }
    }

    fun <T> generateActionEvent(viewModel: T) where T : ViewModel, T : IViewModelActionEvent {
        viewModel.showLoadingLD.observe(lLifecycleOwner, Observer {
            this@IUIActionEventObserver.showLoading(it.message)
        })
        viewModel.dismissLoadingLD.observe(lLifecycleOwner, Observer {
            this@IUIActionEventObserver.dismissLoading()
        })
        viewModel.showToastEventLD.observe(lLifecycleOwner, Observer {
            this@IUIActionEventObserver.showToast(it.message)
        })
        viewModel.finishViewEventLD.observe(lLifecycleOwner, Observer {
            this@IUIActionEventObserver.finishView()
        })
    }

}
package github.leavesc.reactivehttp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope

/**
 * 作者：leavesC
 * 时间：2019/5/31 9:39
 * 描述：
 */
open class BaseViewModel : ViewModel(), IUIActionEvent {

    override val lifecycleSupportedScope: CoroutineScope
        get() = viewModelScope

    val vmActionEvent = MutableLiveData<BaseActionEvent>()

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
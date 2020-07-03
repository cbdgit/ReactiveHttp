package github.leavesc.reactivehttp.viewmodel

/**
 * 作者：leavesC
 * 时间：2020/6/26 21:19
 * 描述：
 * GitHub：https://github.com/leavesC
 */
open class BaseActionEvent

class ShowLoadingEvent(val message: String) : BaseActionEvent()

object DismissLoadingEvent : BaseActionEvent()

object FinishViewEvent : BaseActionEvent()

class ShowToastEvent(val message: String) : BaseActionEvent()
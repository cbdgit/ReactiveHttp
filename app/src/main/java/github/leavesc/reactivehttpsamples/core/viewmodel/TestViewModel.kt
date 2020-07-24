package github.leavesc.reactivehttpsamples.core.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import github.leavesc.reactivehttp.callback.RequestCallback
import github.leavesc.reactivehttp.callback.RequestPairCallback
import github.leavesc.reactivehttp.exception.BaseException
import github.leavesc.reactivehttp.base.BaseReactiveViewModel
import github.leavesc.reactivehttpsamples.core.http.TestDataSource
import github.leavesc.reactivehttpsamples.core.model.ForecastsBean
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

/**
 * 作者：leavesC
 * 时间：2020/5/3 21:39
 * 描述：
 * GitHub：https://github.com/leavesC
 */
class TestViewModel : BaseReactiveViewModel() {

    val logLiveData = MutableLiveData<String>()

    private fun log(msg: String) {
        val log = "[${Thread.currentThread().name}: ]${msg}"
        Log.e("TAG", log)
        logLiveData.value = log
    }

    private fun postLog(msg: String) {
        val log = "[${Thread.currentThread().name}: ]${msg}"
        logLiveData.postValue(log)
    }

    private val testDataSource = TestDataSource(this)

    private var job1: Job? = null

    fun testDelay() {
        job1?.cancel(CancellationException("主动取消Job"))
        job1 = testDataSource.testDelay(object : RequestCallback<String> {

            override fun onStart() {
                log("onStart")
            }

            override fun onCancelled() {
                log("onCancelled")
            }

            override fun onSuccess(data: String) {
                log("onSuccess: " + data)
            }

            override suspend fun onSuccessIO(data: String) {
                repeat(5) {
                    delay(300)
                    postLog("onSuccessIO: " + it)
                }
            }

            override fun onFail(exception: BaseException) {
                log("onFail: " + exception.errorMessage)
            }

            override fun onFinally() {
                log("onFinally")
            }

        })
    }

    fun cancelJob1() {
        job1?.cancel()
    }

    private var job2: Job? = null

    fun testPair() {
        job2?.cancel(CancellationException("主动取消Job"))
        job2 = testDataSource.testPair(object : RequestPairCallback<List<ForecastsBean>, String> {

            override fun onStart() {
                log("onStart")
            }

            override fun onCancelled() {
                log("onCancelled")
            }

            override fun onSuccess(data1: List<ForecastsBean>, data2: String) {
                log("data:1 $data1\ndata2: $data2")
            }

            override suspend fun onSuccessIO(data1: List<ForecastsBean>, data2: String) {
                repeat(5) {
                    delay(300)
                    postLog("onSuccessIO: " + it)
                }
            }

            override fun onFail(exception: BaseException) {
                log("onFail: " + exception.errorMessage)
            }

            override fun onFinally() {
                log("onFinally")
            }

        })
    }

    fun cancelJob2() {
        job2?.cancel()
    }

    private fun throwException() {
        throw Exception("xxxxxxxxxxxxx")
    }

}
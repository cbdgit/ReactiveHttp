package github.leavesc.reactivehttpsamples.core.http

import github.leavesc.reactivehttp.callback.RequestCallback
import github.leavesc.reactivehttp.callback.RequestPairCallback
import github.leavesc.reactivehttp.viewmodel.IUIActionEvent
import github.leavesc.reactivehttpsamples.core.http.base.HttpResBean
import github.leavesc.reactivehttpsamples.core.model.DistrictBean
import github.leavesc.reactivehttpsamples.core.model.ForecastsBean
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

/**
 * 作者：leavesC
 * 时间：2019/5/31 14:27
 * 描述：
 * GitHub：https://github.com/leavesC
 */
class MapDataSource(actionEventEvent: IUIActionEvent) : LocalRemoteDataSource<ApiService>(actionEventEvent, ApiService::class.java) {

    fun getProvince(callback: RequestCallback<List<DistrictBean>>) {
        executeLoading(callback) {
            getService().getProvince()
        }
    }

    fun getCity(keywords: String, callback: RequestCallback<List<DistrictBean>>) {
        executeLoading(callback) {
            getService().getCity(keywords)
        }
    }

    fun getCounty(keywords: String, callback: RequestCallback<List<DistrictBean>>) {
        executeLoading(callback) {
            getService().getCounty(keywords)
        }
    }

}

class WeatherDataSource(actionEventEvent: IUIActionEvent) : LocalRemoteDataSource<ApiService>(actionEventEvent, ApiService::class.java) {

    fun getWeather(city: String, callback: RequestCallback<List<ForecastsBean>>) {
        executeLoading(callback) {
            getService().getWeather(city)
        }
    }

}

class TestDataSource(actionEventEvent: IUIActionEvent) : LocalRemoteDataSource<ApiService>(actionEventEvent, ApiService::class.java) {

    private suspend fun testDelay(): HttpResBean<String> {
        withIO {
            delay(2000)
        }
        return HttpResBean(1, "msg", "data coming")
    }

    fun testDelay(callback: RequestCallback<String>): Job {
        return execute(callback) {
            testDelay()
        }
    }

    fun testDelay2(callback: RequestCallback<HttpResBean<String>>): Job {
        return executeOrigin(callback) {
            delay(3000)
            HttpResBean(1, "msg", "data coming")
        }
    }

    fun testPair(callback: RequestPairCallback<List<ForecastsBean>, String>): Job {
        return execute(callback, showLoading = false, block1 = { getService().getWeather("411122") }, block2 = {
            delay(1000)
            HttpResBean(1, "errMsg", "data coming")
        })
    }

}
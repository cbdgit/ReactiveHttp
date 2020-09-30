package github.leavesc.reactivehttpsamples.core.viewmodel

import androidx.lifecycle.MutableLiveData
import github.leavesc.reactivehttp.base.BaseReactiveViewModel
import github.leavesc.reactivehttp.callback.RequestCallback
import github.leavesc.reactivehttpsamples.core.http.WeatherDataSource
import github.leavesc.reactivehttpsamples.core.model.ForecastsBean

/**
 * 作者：leavesC
 * 时间：2019/6/7 21:13
 * 描述：
 * GitHub：https://github.com/leavesC
 */
class WeatherViewModel : BaseReactiveViewModel() {

    private val weatherDataSource = WeatherDataSource(this)

    val forecastsBeanLiveData = MutableLiveData<ForecastsBean>()

    fun getWeather(city: String) {
        weatherDataSource.getWeather(city, object : RequestCallback<List<ForecastsBean>> {
            override fun onSuccess(data: List<ForecastsBean>) {
                if (data.isNotEmpty()) {
                    forecastsBeanLiveData.value = data[0]
                }
            }
        })
    }

}
package github.leavesc.reactivehttpsamples.ui

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import github.leavesc.reactivehttp.base.BaseReactiveActivity
import github.leavesc.reactivehttpsamples.R
import github.leavesc.reactivehttpsamples.adapter.WeatherAdapter
import github.leavesc.reactivehttpsamples.core.cache.AreaCache
import github.leavesc.reactivehttpsamples.core.model.CastsBean
import github.leavesc.reactivehttpsamples.core.model.ForecastsBean
import github.leavesc.reactivehttpsamples.core.viewmodel.WeatherViewModel
import kotlinx.android.synthetic.main.activity_weather.*

/**
 * 作者：leavesC
 * 时间：2019/6/2 20:18
 * 描述：
 * GitHub：https://github.com/leavesC
 */
class WeatherActivity : BaseReactiveActivity() {

    private val weatherViewModel by getViewModel(WeatherViewModel::class.java) {
        forecastsBeanLiveData.observe(it, Observer {
            showWeather(it)
        })
    }

    private val castsBeanList = mutableListOf<CastsBean>()

    private val weatherAdapter = WeatherAdapter(castsBeanList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        rv_dailyForecast.layoutManager = LinearLayoutManager(this)
        rv_dailyForecast.adapter = weatherAdapter
        swipeRefreshLayout.setOnRefreshListener {
            weatherViewModel.getWeather(AreaCache.getAdCode(this))
        }
        iv_place.setOnClickListener {
            startActivity(MapActivity::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        weatherViewModel.getWeather(AreaCache.getAdCode(this))
    }

    private fun showWeather(forecastsBean: ForecastsBean) {
        tv_city.text = forecastsBean.city
        castsBeanList.clear()
        castsBeanList.addAll(forecastsBean.casts)
        weatherAdapter.notifyDataSetChanged()
        swipeRefreshLayout.isRefreshing = false
    }

}
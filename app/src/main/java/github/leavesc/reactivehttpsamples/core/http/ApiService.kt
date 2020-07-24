package github.leavesc.reactivehttpsamples.core.http

import github.leavesc.reactivehttpsamples.core.http.base.HttpResBean
import github.leavesc.reactivehttpsamples.core.model.DistrictBean
import github.leavesc.reactivehttpsamples.core.model.ForecastsBean
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 作者：leavesC
 * 时间：2019/5/31 11:17
 * 描述：
 * GitHub：https://github.com/leavesC
 */
interface ApiService {

    @GET("config/district")
    suspend fun getProvince(): HttpResBean<List<DistrictBean>>

    @GET("config/district")
    suspend fun getCity(@Query("keywords") keywords: String): HttpResBean<List<DistrictBean>>

    @GET("config/district")
    suspend fun getCounty(@Query("keywords") keywords: String): HttpResBean<List<DistrictBean>>

    @GET("weather/weatherInfo?extensions=all")
    suspend fun getWeather(@Query("city") city: String): HttpResBean<List<ForecastsBean>>

}
package github.leavesc.reactivehttpsamples.ui

import android.os.Bundle
import android.text.TextUtils
import github.leavesc.reactivehttp.base.BaseReactiveActivity
import github.leavesc.reactivehttpsamples.R
import github.leavesc.reactivehttpsamples.core.cache.AreaCache
import github.leavesc.reactivehttpsamples.ui.weather.MapActivity
import github.leavesc.reactivehttpsamples.ui.weather.WeatherActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 作者：leavesC
 * 时间：2019/5/31 15:39
 * 描述：
 * GitHub：https://github.com/leavesC
 */
class MainActivity : BaseReactiveActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_weather.setOnClickListener {
            if (TextUtils.isEmpty(AreaCache.getAdCode(this))) {
                startActivity(MapActivity::class.java)
            } else {
                startActivity(WeatherActivity::class.java)
            }
        }
        btn_request.setOnClickListener {
            startActivity(TestActivity::class.java)
        }
    }

}
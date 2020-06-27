package github.leavesc.reactivehttpsamples

import android.app.Application
import android.content.Context

/**
 * 作者：leavesC
 * 时间：2019/5/31 11:07
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://juejin.im/user/57c2ea9befa631005abd00c6
 */
class MainApplication : Application() {

    companion object {

        lateinit var context: Context

    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }

}
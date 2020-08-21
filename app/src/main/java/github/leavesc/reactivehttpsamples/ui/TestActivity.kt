package github.leavesc.reactivehttpsamples.ui

import android.os.Bundle
import androidx.lifecycle.Observer
import github.leavesc.reactivehttpsamples.R
import github.leavesc.reactivehttp.base.BaseReactiveActivity
import github.leavesc.reactivehttpsamples.core.viewmodel.TestViewModel
import kotlinx.android.synthetic.main.activity_test.*

/**
 * 作者：leavesC
 * 时间：2020/5/3 21:38
 * 描述：
 * GitHub：https://github.com/leavesC
 */
class TestActivity : BaseReactiveActivity() {

    private val testViewModel by getViewModel(TestViewModel::class.java) {
        logLiveData.observe(this@TestActivity, Observer {
            tv_log.append(it + "\n")
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        btn_cleanLog.setOnClickListener {
            tv_log.text = ""
        }

        btn_test1.setOnClickListener {
            testViewModel.testDelay1()
        }
        btn_cancelJob1.setOnClickListener {
            testViewModel.cancelJob1()
        }

        btn_test2.setOnClickListener {
            testViewModel.testDelay2()
        }
        btn_cancelJob2.setOnClickListener {
            testViewModel.cancelJob2()
        }

        btn_test3.setOnClickListener {
            testViewModel.testPair()
        }
        btn_cancelJob3.setOnClickListener {
            testViewModel.cancelJobPair()
        }
    }

}

package github.leavesc.reactivehttp.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import github.leavesc.reactivehttp.viewmodel.IUIActionEventObserver
import kotlinx.coroutines.CoroutineScope

/**
 * 作者：leavesC
 * 时间：2019/5/31 9:36
 * 描述：
 * GitHub：https://github.com/leavesC
 */
@SuppressLint("Registered")
abstract class BaseReactiveActivity : AppCompatActivity(), IUIActionEventObserver {

    override val lifecycleSupportedScope: CoroutineScope
        get() = lifecycleScope

    override val lContext: Context?
        get() = this

    override val lLifecycleOwner: LifecycleOwner
        get() = this

    private var loadDialog: ProgressDialog? = null

    override fun showLoading(msg: String) {
        if (loadDialog == null) {
            loadDialog = ProgressDialog(lContext).apply {
                setCancelable(false)
                setCanceledOnTouchOutside(false)
            }
        }
        loadDialog?.let {
            if (!it.isShowing) {
                it.show()
            }
        }
    }

    override fun dismissLoading() {
        loadDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }

    override fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun finishView() {
        finish()
    }

    protected fun <T : Activity> startActivity(clazz: Class<T>) {
        lContext?.apply {
            startActivity(Intent(this, clazz))
        }
    }

}
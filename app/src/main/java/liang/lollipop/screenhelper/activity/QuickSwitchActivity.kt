package liang.lollipop.screenhelper.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import liang.lollipop.screenhelper.util.AppSettings
import liang.lollipop.screenhelper.util.ScreenUtil

/**
 * 快速切换的Activity
 * @author Lollipop
 */
class QuickSwitchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val status = !ScreenUtil.getSimulateColorSpaceState(this)
        AppSettings.isOpen(this,status)
        ScreenUtil.setSimulateColorSpaceState(this,status)
        finish()
    }
}

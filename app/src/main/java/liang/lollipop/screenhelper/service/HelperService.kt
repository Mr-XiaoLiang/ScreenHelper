package liang.lollipop.screenhelper.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import liang.lollipop.screenhelper.util.AppSettings
import liang.lollipop.screenhelper.util.RelatedDBUtil
import liang.lollipop.screenhelper.util.ScreenUtil

/**
 * 辅助用于检测前台应用，更改显示效果的服务
 * @author Lollipop
 */
class HelperService: AccessibilityService() {

    private var lastPackage = ""

    private lateinit var dbHelper: RelatedDBUtil.DBOperate

    override fun onCreate() {
        super.onCreate()
        dbHelper = RelatedDBUtil.read(this)
    }

    override fun onInterrupt() {
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        Log.d("HelperService","onAccessibilityEvent")

        if(!AppSettings.isRelatedApp(this) || event == null){
            return
        }

        //获取当前包名
        val foregroundPackageName = event.packageName.toString()

        Log.d("HelperService","onAccessibilityEvent -> $foregroundPackageName")

        //如果包不是上一个包，那么认为应用更换了，那么更换为新的状态
        if (lastPackage != foregroundPackageName) {
            //如果这是新的窗口，那么就发起检查
            checkPackage(foregroundPackageName)
            lastPackage = foregroundPackageName
        }

    }

    private fun checkPackage(pkgName: String){

        val isOpen = dbHelper.isExist(pkgName) || AppSettings.isOpen(this)

        Log.d("HelperService","onAccessibilityEvent -> $isOpen")

        ScreenUtil.setSimulateColorSpaceState(this,isOpen)

    }

}
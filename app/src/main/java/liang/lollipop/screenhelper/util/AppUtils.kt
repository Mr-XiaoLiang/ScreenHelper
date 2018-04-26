package liang.lollipop.screenhelper.util

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import liang.lollipop.screenhelper.activity.StartActivity
import liang.lollipop.screenhelper.bean.AppInfo

/**
 * 应用相关的工具类
 * @author Lollipop
 */
object AppUtils {

    /**
     * 获取非系统应用信息列表
     */
    fun getAppList(context: Activity): ArrayList<AppInfo> {
        val packageManager = context.packageManager
        // Return a List of all packages that are installed on the device.
        val packages = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES)

        val appList = ArrayList<AppInfo>(packages.size)

        for (packageInfo in packages) {

            try{

//                val name = packageInfo.applicationInfo.loadLabel(packageManager).toString()
//                val pkg = packageInfo.packageName
//                val icon = packageManager.getApplicationIcon(packageInfo.applicationInfo)
//
//                val bean = AppInfo(name, pkg, icon)
//
//                appList.add(bean)
                appList.add(AppInfo.create(packageInfo,packageManager))

            }catch (e: Exception){
                e.printStackTrace()
                continue
            }

        }
        return appList
    }

    private fun hintLauncherIcon(context: Activity,componentName: ComponentName){
        val packageManager = context.packageManager
        packageManager.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP)
    }

    private fun showLauncherIcon(context: Activity,componentName: ComponentName){
        val packageManager = context.packageManager
        packageManager.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
                PackageManager.DONT_KILL_APP)
    }

    fun isHintLauncherIcon(context: Activity): Boolean{
        return isHintLauncherIcon(context,ComponentName(context,StartActivity::class.java))
    }

    fun isHintLauncherIcon(context: Activity,componentName: ComponentName): Boolean{
        val packageManager = context.packageManager
        return when(packageManager.getComponentEnabledSetting(componentName)){

            PackageManager.COMPONENT_ENABLED_STATE_DEFAULT -> {
                false
            }

            PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> {
                false
            }

            PackageManager.COMPONENT_ENABLED_STATE_DISABLED -> {
                true
            }

            PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER -> {
                true
            }

            PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED -> {
                true
            }

            else -> {
                true
            }

        }
    }

    fun changeLauncherIconStatus(context: Activity, isShow: Boolean){
        if(isShow){
            showLauncherIcon(context,ComponentName(context,StartActivity::class.java))
        }else{
            hintLauncherIcon(context,ComponentName(context,StartActivity::class.java))
        }
    }

    // To check if service is enabled
    fun isAccessibilitySettingsOn(context: Context): Boolean {
        var accessibilityEnabled = 0
        try {
            accessibilityEnabled = android.provider.Settings.Secure.getInt(context.contentResolver,
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED)
        } catch (e: android.provider.Settings.SettingNotFoundException) {
        }

        if (accessibilityEnabled == 1) {
            val services = android.provider.Settings.Secure.getString(context.contentResolver,
                    android.provider.Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            if (services != null) {
                return services.toLowerCase().contains(context.packageName.toLowerCase())
            }
        }
        return false
    }

}
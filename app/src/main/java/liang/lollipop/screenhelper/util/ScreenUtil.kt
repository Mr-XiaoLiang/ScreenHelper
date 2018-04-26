package liang.lollipop.screenhelper.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import android.support.v4.content.ContextCompat

/**
 * 屏幕设置的工具类
 * @author Lollipop
 */
object ScreenUtil {

    /**
     * 设置模拟颜色空间的状态
     * 代码来自：https://gitee.com/dede_hu/one-plus-screen
     * @author hsh
     * @param context
     * @param state
     * @param callBack
     * @return true 成功，false 失败
     */
    fun setSimulateColorSpaceState(context: Context, state: Boolean, callBack: ExceptionCallBack? = null): Boolean {
        val cr = context.contentResolver
        try {
            /**
             * 系统隐藏常量
             * android api 25源码 android.provider.Settings 5313行
             * android.provider.Settings.Secure
             * 是否启用模拟颜色空间
             * 0 停用, 1 启用
             * public static final String ACCESSIBILITY_DISPLAY_DALTONIZER_ENABLED = "accessibility_display_daltonizer_enabled"
             * 模拟颜色空间模式
             * 0 全色盲, 1 红色弱视, 2 绿色弱视, 3 蓝色弱视
             * public static final String ACCESSIBILITY_DISPLAY_DALTONIZER ="accessibility_display_daltonizer"
             */
            return if (state && !getSimulateColorSpaceState(context)) {
                val enable = Settings.Secure.putInt(cr, "accessibility_display_daltonizer_enabled", 1)
                val value = Settings.Secure.putInt(cr, "accessibility_display_daltonizer", 0)
                enable && value
            } else if (!state && getSimulateColorSpaceState(context)) {
                Settings.Secure.putInt(cr, "accessibility_display_daltonizer_enabled", 0)
            } else {
                true
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            callBack?.onException(e)
            return false
        }
    }

    /**
     * 获取当前屏幕色彩模式的设置
     * 代码来自：https://gitee.com/dede_hu/one-plus-screen
     */
    fun getSimulateColorSpaceState(context: Context): Boolean {
        val cr = context.contentResolver
        return try {
            val state = Settings.Secure.getInt(cr, "accessibility_display_daltonizer_enabled")
            if (state == 0)
                false
            else {
                Settings.Secure.getInt(cr, "accessibility_display_daltonizer") == 0
            }
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    interface ExceptionCallBack {
        fun onException(e: Exception)
    }

    fun hasPermission(context: Context): Boolean{
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED
    }

}
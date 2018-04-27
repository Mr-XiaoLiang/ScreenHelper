package liang.lollipop.screenhelper.util

import android.content.Context

/**
 * 应用的相关设置
 * @author Lollipop
 */
object AppSettings {

    private const val RELATED_APP = "RELATED_APP"

    private const val IS_OPEN = "IS_OPEN"

    fun isRelatedApp(context: Context): Boolean{
        return PreferencesUtil.get(context, RELATED_APP,false)
    }

    fun putRelatedApp(context: Context,value: Boolean){
        PreferencesUtil.put(context, RELATED_APP,value)
    }

    fun isOpen(context: Context):Boolean{
        return PreferencesUtil.get(context, IS_OPEN,false)
    }

    fun isOpen(context: Context,value: Boolean){
        PreferencesUtil.put(context, IS_OPEN,value)
    }

}
package liang.lollipop.screenhelper.util

import android.content.Context

/**
 * 应用的相关设置
 * @author Lollipop
 */
object AppSettings {

    private const val RELATED_APP = "RELATED_APP"

    fun isRelatedApp(context: Context): Boolean{
        return PreferencesUtil.get(context, RELATED_APP,false)
    }

    fun putRelatedApp(context: Context,value: Boolean){
        PreferencesUtil.put(context, RELATED_APP,value)
    }

}
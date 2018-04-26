package liang.lollipop.screenhelper.bean

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

/**
 * App的信息
 * @author Lollipop
 */
class AppInfo (var appName: String,var pkgName: String,var appIcon: Drawable?){

    companion object {

        fun create(packageInfo: PackageInfo,packageManager: PackageManager): AppInfo{

            return AppInfo(
                    packageInfo.applicationInfo.loadLabel(packageManager).toString()?:"",
                    packageInfo.packageName?:"",
                    packageManager.getApplicationIcon(packageInfo.applicationInfo)
            )

        }

    }

}
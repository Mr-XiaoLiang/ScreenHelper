package liang.lollipop.screenhelper.service

import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.support.annotation.RequiresApi
import android.widget.Toast
import liang.lollipop.screenhelper.R
import liang.lollipop.screenhelper.activity.MainActivity
import liang.lollipop.screenhelper.util.AppSettings
import liang.lollipop.screenhelper.util.ScreenUtil


/**
 * 状态栏快捷按钮的服务
 * @author Lollipop
 */
@RequiresApi(Build.VERSION_CODES.N)
class QuickSettingService: TileService() {

    private fun updateTileState(isOpen: Boolean) {
        //        toggleState = type;
        val icon: Icon
        if (isOpen) {
            icon = Icon.createWithResource(applicationContext, R.drawable.ic_invert_colors_white_24dp)
            qsTile.state = Tile.STATE_ACTIVE//更改成活跃状态
        } else {
            icon = Icon.createWithResource(applicationContext, R.drawable.ic_invert_colors_off_white_24dp)
            qsTile.state = Tile.STATE_INACTIVE// 更改成非活跃状态
        }
        qsTile.icon = icon//设置图标
        qsTile.updateTile()//更新Tile
    }

    override fun onStartListening() {
        super.onStartListening()
        //打开下拉菜单的时候调用,当快速设置按钮并没有在编辑栏拖到设置栏中不会调用
        //在TileAdded之后会调用一次
        updateTileState(ScreenUtil.getSimulateColorSpaceState(this))
    }

    override fun onClick() {
        super.onClick()
        if(ScreenUtil.hasPermission(this)){
            val status = !ScreenUtil.getSimulateColorSpaceState(this)
            AppSettings.isOpen(this,status)
            ScreenUtil.setSimulateColorSpaceState(this,status)
        }else{
            Toast.makeText(this, R.string.no_permission,Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onTileAdded() {
        super.onTileAdded()
        //添加时，刷新状态
        updateTileState(ScreenUtil.getSimulateColorSpaceState(this))
    }

    override fun onTileRemoved() {
        super.onTileRemoved()
        //移出时，管理黑白模式
        if(ScreenUtil.hasPermission(this)){
            ScreenUtil.setSimulateColorSpaceState(this,false)
        }
    }

}
package liang.lollipop.screenhelper.holder

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SwitchCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import liang.lollipop.screenhelper.R
import liang.lollipop.screenhelper.bean.AppInfo

/**
 * App的显示Item
 * @author Lollipop
 */
class AppInfoHolder(itemView: View,private val onSwitchChangeCallback: OnSwitchChangeCallback)
    : RecyclerView.ViewHolder(itemView),CompoundButton.OnCheckedChangeListener {


    companion object {

        private const val LAYOUT_ID = R.layout.item_app_info

        fun create(onSwitchChangeCallback: OnSwitchChangeCallback,layoutInflater: LayoutInflater,parent: ViewGroup?): AppInfoHolder{
            return AppInfoHolder(layoutInflater.inflate(LAYOUT_ID,parent,false),onSwitchChangeCallback)
        }

    }

    private val appNameView: TextView = itemView.findViewById(R.id.appNameView)
    private val pkgView: TextView = itemView.findViewById(R.id.appPkgView)
    private val switchView: SwitchCompat = itemView.findViewById(R.id.appSwitch)
    private val appIconView: ImageView = itemView.findViewById(R.id.appIconView)

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when(buttonView){

            switchView -> {

                onSwitchChangeCallback.onSwitchChange(this,isChecked)

            }

        }
    }

    fun onBind(bean: AppInfo,isChecked: Boolean){
        switchView.setOnCheckedChangeListener(null)
        appNameView.text = bean.appName
        pkgView.text = bean.pkgName
        switchView.isChecked = isChecked
        appIconView.setImageDrawable(bean.appIcon)
        switchView.setOnCheckedChangeListener(this)
    }

}
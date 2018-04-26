package liang.lollipop.screenhelper.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import liang.lollipop.screenhelper.bean.AppInfo
import liang.lollipop.screenhelper.holder.AppInfoHolder
import liang.lollipop.screenhelper.holder.OnSwitchChangeCallback

/**
 * APP信息的适配器
 * @author Lollipop
 */
class AppInfoAdapter(
        private val dataList: ArrayList<AppInfo>,
        private val layoutInflater: LayoutInflater,
        private val onSwitchChangeCallback: OnSwitchChangeCallback,
        private val checkedCallback: CheckedCallback): RecyclerView.Adapter<AppInfoHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppInfoHolder {
        return AppInfoHolder.create(onSwitchChangeCallback,layoutInflater,parent)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: AppInfoHolder, position: Int) {
        holder.onBind(dataList[position],checkedCallback.isChecked(position))
    }

    interface CheckedCallback{

        fun isChecked(position: Int): Boolean

    }

}
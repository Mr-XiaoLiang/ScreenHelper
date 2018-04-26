package liang.lollipop.screenhelper.fragment


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import kotlinx.android.synthetic.main.fragment_app_settings.*
import liang.lollipop.screenhelper.R
import liang.lollipop.screenhelper.adapter.AppInfoAdapter
import liang.lollipop.screenhelper.bean.AppInfo
import liang.lollipop.screenhelper.holder.AppInfoHolder
import liang.lollipop.screenhelper.holder.OnSwitchChangeCallback
import liang.lollipop.screenhelper.util.AppSettings
import liang.lollipop.screenhelper.util.AppUtils
import liang.lollipop.screenhelper.util.RelatedDBUtil
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import org.jetbrains.anko.uiThread

/**
 * App设置的Fragment
 * @author Lollipop
 */
class AppSettingsFragment : Fragment(),
        CompoundButton.OnCheckedChangeListener,
        SwipeRefreshLayout.OnRefreshListener,
        OnSwitchChangeCallback,
        AppInfoAdapter.CheckedCallback{

    private val appInfoList = ArrayList<AppInfo>()
    private val akgNameList = ArrayList<String>()

    private lateinit var dbUtil: RelatedDBUtil.DBOperate

    private lateinit var adapter: AppInfoAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_app_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
            showIconSwitch.isEnabled = false
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dbUtil = RelatedDBUtil.write(activity!!)
    }

    override fun onResume() {
        super.onResume()
        initView()
        if(appInfoList.isEmpty()){
            onRefresh()
        }
    }

    private fun initView(){

        showIconSwitch.isChecked = AppUtils.isHintLauncherIcon(activity!!)
        showIconSwitch.setOnCheckedChangeListener(this)
        relatedSwitch.isChecked = (AppSettings.isRelatedApp(context!!) && AppUtils.isAccessibilitySettingsOn(context!!))
        relatedSwitch.setOnCheckedChangeListener(this)
        if(!AppUtils.isAccessibilitySettingsOn(context!!)){
            AppSettings.putRelatedApp(context!!,false)
        }

        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = AppInfoAdapter(appInfoList,layoutInflater,this,this)
        recyclerView.adapter = adapter

        refreshLayout.setColorSchemeResources(R.color.colorAccent,R.color.colorPrimary)
        refreshLayout.setOnRefreshListener(this)

    }

    override fun onRefresh() {
        refreshLayout.isRefreshing = true
        doAsync {

            dbUtil.selectAll(akgNameList)
            val resultData = AppUtils.getAppList(activity!!)
            appInfoList.clear()
            appInfoList.addAll(resultData)

            onComplete {

                uiThread {

                    adapter.notifyDataSetChanged()
                    refreshLayout.isRefreshing = false

                }

            }

        }
    }

    override fun onSwitchChange(holder: AppInfoHolder, isChecked: Boolean) {

        val index = holder.adapterPosition

        if(isChecked){

            akgNameList.add(appInfoList[index].pkgName)
            dbUtil.install(appInfoList[index].pkgName)

        }else{

            akgNameList.remove(appInfoList[index].pkgName)
            dbUtil.delete(appInfoList[index].pkgName)

        }

    }

    override fun isChecked(position: Int): Boolean {
        return akgNameList.contains(appInfoList[position].pkgName)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

        when(buttonView){

            showIconSwitch -> {

                AppUtils.changeLauncherIconStatus(activity!!,!isChecked)

            }

            relatedSwitch -> {

                if(isChecked && !AppUtils.isAccessibilitySettingsOn(context!!)){
                    AlertDialog.Builder(context!!)
                            .setTitle(R.string.dialog_related_title)
                            .setMessage(R.string.dialog_related_message)
                            .setNegativeButton(R.string.refused){ dialog, _ ->
                                relatedSwitch.isChecked = false
                                dialog.dismiss()
                            }
                            .setPositiveButton(R.string.travel_to){ dialog, _ ->
                                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                                startActivity(intent)
                                dialog.dismiss()
                            }.show()
                    return
                }
                AppSettings.putRelatedApp(context!!,isChecked)

            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        dbUtil.close()
    }

}

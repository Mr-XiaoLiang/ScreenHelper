package liang.lollipop.screenhelper.fragment


import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
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
import liang.lollipop.screenhelper.activity.QuickSwitchActivity
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
        AppInfoAdapter.CheckedCallback,
        View.OnClickListener{

    private val appInfoList = ArrayList<AppInfo>()
    private val akgNameList = ArrayList<String>()

    private lateinit var dbUtil: RelatedDBUtil.DBOperate

    private lateinit var adapter: AppInfoAdapter

    private var isGotoAccessibilitySettings = false

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
        initView()
    }

    override fun onResume() {
        super.onResume()
        if(appInfoList.isEmpty()){
            onRefresh()
        }
        if(isGotoAccessibilitySettings && context != null){
            relatedSwitch.isChecked = AppUtils.isAccessibilitySettingsOn(context!!)
        }
    }

    private fun initView(){

        showIconSwitch.isChecked = AppUtils.isHintLauncherIcon(activity!!)
        showIconSwitch.setOnCheckedChangeListener(this)
        quickSettingSwitch.isChecked = AppUtils.isHintQuickSettingIcon(activity!!)
        quickSettingSwitch.setOnCheckedChangeListener(this)
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

        quickSettingHelpBtn.setOnClickListener(this)
        relatedHelpBtn.setOnClickListener(this)
        showIconHelpBtn.setOnClickListener(this)

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

    override fun onClick(v: View?) {

        when(v){

            quickSettingHelpBtn -> {
                if(context != null){
                    AlertDialog.Builder(context!!)
                            .setMessage(R.string.hint_quick_setting_summary)
                            .setPositiveButton(R.string.add_quick_btn){ dialog, _ ->
                                addShortcut()
                                dialog.dismiss()
                            }
                            .setNegativeButton(R.string.i_know){dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                }

            }

            relatedHelpBtn -> {
                showHelp(R.string.hint_related_summary)
            }

            showIconHelpBtn -> {
                showHelp(R.string.hint_icon_summary)
            }

        }

    }

    private fun addShortcut(){

        if(context == null){
            return
        }

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1){

            AlertDialog.Builder(context!!)
                    .setMessage(R.string.help_shortcut)
                    .setPositiveButton(R.string.add_quick_btn){ dialog, _ ->
                        addOldShortcut()
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.i_know){dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()

        }else{
            addOldShortcut()
        }

    }

    private fun addOldShortcut(){
        AppUtils.addShortcut(context!!,
                Intent(context,QuickSwitchActivity::class.java),
                getString(R.string.quick_setting_name),
                false,
                BitmapFactory.decodeResource(resources,R.mipmap.ic_quick_launcher_round))
        Snackbar.make(quickSettingHelpBtn,R.string.shortcut_added,Snackbar.LENGTH_SHORT).show()
    }

    private fun showHelp(msg: Int){
        if(context != null){
            AlertDialog.Builder(context!!)
                    .setMessage(msg)
                    .setPositiveButton(R.string.i_know){ dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
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

            quickSettingSwitch -> {

                AppUtils.changeQuickSettingIconStatus(activity!!,!isChecked)

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
                                isGotoAccessibilitySettings = true
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

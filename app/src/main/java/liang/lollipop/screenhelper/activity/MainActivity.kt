package liang.lollipop.screenhelper.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import liang.lollipop.screenhelper.util.ShellUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import liang.lollipop.screenhelper.R
import liang.lollipop.screenhelper.util.AppSettings
import liang.lollipop.screenhelper.util.ScreenUtil
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import org.jetbrains.anko.uiThread

/**
 * 主页，设置的页面
 * @author Lollipop
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var isOpenSimulateColorSpace = false

    private lateinit var shell:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        shell = "adb shell pm grant $packageName android.permission.WRITE_SECURE_SETTINGS"

        initView()

        if(!ScreenUtil.hasPermission(this)){
            AlertDialog.Builder(this)
                    .setMessage(R.string.alert_to_root)
                    .setPositiveButton(R.string.i_have_root){ dialog, _ ->
                        tryToRoot()
                        dialog.dismiss()
                    }.show()
        }

    }

    private fun initView(){
        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        fab.setOnClickListener(this)
        copyBtn.setOnClickListener(this)

        infoView.text = (getString(R.string.adb_hint)+shell)

    }

    override fun onResume() {
        super.onResume()
        isOpenSimulateColorSpace = ScreenUtil.getSimulateColorSpaceState(this)
    }

    override fun onClick(v: View?) {

        when(v){

            fab -> if(ScreenUtil.hasPermission(this)){

                isOpenSimulateColorSpace = !isOpenSimulateColorSpace
                AppSettings.isOpen(this,isOpenSimulateColorSpace)
                ScreenUtil.setSimulateColorSpaceState(this,isOpenSimulateColorSpace)

            }else{

                AlertDialog.Builder(this).setMessage(R.string.no_permission)
                        .setPositiveButton(R.string.positive){
                            dialog, _ -> dialog.dismiss()
                        }
                        .show()

            }

            copyBtn -> {

                copy(shell)

            }

        }

    }

    /**
     * 实现文本复制功能
     * @param content
     */
    private fun copy(content: String) {
        // 得到剪贴板管理器
        val cmb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val data = ClipData.newPlainText(getString(R.string.app_name),content.trim())
        cmb.primaryClip = data
        Snackbar.make(fab,getString(R.string.copy_success),Snackbar.LENGTH_SHORT).show()
    }

    private fun tryToRoot(){
        doAsync {

            ShellUtil.exeCmd("pm grant $packageName android.permission.WRITE_SECURE_SETTINGS")

            onComplete {

                uiThread {

                    if(!ScreenUtil.hasPermission(this@MainActivity)){

                        AlertDialog.Builder(this@MainActivity)
                                .setMessage(getString(R.string.try_to_root_error))
                                .show()

                    }

                }

            }

        }
    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(Gravity.START)){
            drawerLayout.closeDrawer(Gravity.START)
            return
        }
        super.onBackPressed()
    }

    override fun onStop() {
        super.onStop()
        if(drawerLayout.isDrawerOpen(Gravity.START)){
            drawerLayout.closeDrawer(Gravity.START)
        }
    }

}

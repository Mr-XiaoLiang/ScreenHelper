package liang.lollipop.screenhelper.util

import org.jetbrains.anko.doAsync
import java.io.DataOutputStream
import java.io.IOException

/**
 * @author hsh
 * @time 2017/6/24 024 2:33 下午.
 * @doc
 */
object ShellUtil {

    fun exeCmdAsync(cmd: String?) {
        doAsync { exeCmd(cmd) }
    }

    fun exeCmd(cmd: String?) {
        if (cmd == null || "" == cmd)
            return
        val process: Process
        var os: DataOutputStream? = null
        try {
            process = Runtime.getRuntime().exec("su")
            os = DataOutputStream(process.outputStream)
            os.writeBytes(cmd + "\n")
            os.flush()
            os.writeBytes("exit\n")
            os.flush()
            process.waitFor()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } finally {
            try {
                os?.close()
            } catch (e: IOException) {
            }
        }
    }

    fun requestRoot(): Boolean {
        val process: Process
        var os: DataOutputStream? = null
        val waitFor: Int
        try {
            process = Runtime.getRuntime().exec("su")
            os = DataOutputStream(process.outputStream)
            os.writeBytes("exit\n")
            os.flush()
            waitFor = process.waitFor()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        } catch (e: InterruptedException) {
            e.printStackTrace()
            return false
        } finally {
            try {
                os?.close()
            } catch (e: IOException) {
            }
        }
        return waitFor == 0
    }
}
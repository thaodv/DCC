package io.wexchain.android.dcc.tools

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

/**
 *Created by liuyang on 2018/8/27.
 */
class PermissionHelper(private val activity: Activity) {

    private var mPermissionModels = arrayOf<PermissionModel>()
    private lateinit var onApplyPermission: () -> Unit

    fun requestPermissions(models: Array<PermissionModel>,onApply:() -> Unit) {
        onApplyPermission = onApply
        mPermissionModels = models
        if (Build.VERSION.SDK_INT < 23) {
            onApplyPermission()
        } else {
            if (isAllRequestedPermissionGranted) {
                onApplyPermission()
            } else {
                applyPermissions()
            }
        }
    }

    private fun applyPermissions() {
        try {
            for (model in mPermissionModels) {
                if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(activity, model.permission)) {
                    ActivityCompat.requestPermissions(activity, arrayOf(model.permission), model.requestCode)
                    return
                }
            }
            onApplyPermission()
        } catch (e: Throwable) {

        }
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        for (permissionModel in mPermissionModels.iterator()) {
            if (permissionModel.requestCode == requestCode) {
                if (PackageManager.PERMISSION_GRANTED != grantResults[0]) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[0])) {
                        AlertDialog.Builder(activity).setTitle("权限申请").setMessage(findPermissionExplain(permissions[0]))
                                .setPositiveButton("确定") { dialog, which -> applyPermissions() }
                                .setCancelable(false)
                                .show()
                    } else {
                        AlertDialog.Builder(activity).setTitle("权限申请")
                                .setMessage("请在打开的窗口的权限中开启" + findPermissionName(permissions[0]) + "权限，以正常使用本应用")
                                .setPositiveButton("去设置") { dialog, which -> openApplicationSettings(REQUEST_OPEN_APPLICATION_SETTINGS_CODE) }
                                .setNegativeButton("取消") { dialog, which -> activity.finish() }
                                .setCancelable(false)
                                .show()
                    }
                    return
                }

                if (isAllRequestedPermissionGranted) {
                    onApplyPermission()
                } else {
                    applyPermissions()
                }
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_OPEN_APPLICATION_SETTINGS_CODE -> if (isAllRequestedPermissionGranted) {
                onApplyPermission()
            } else {
                activity.finish()
            }
        }
    }

    val isAllRequestedPermissionGranted: Boolean
        get() {
            return mPermissionModels.none { PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(activity, it.permission) }
        }

    private fun openApplicationSettings(requestCode: Int): Boolean {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.packageName))
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            activity.startActivityForResult(intent, requestCode)
            return true
        } catch (e: Throwable) {
        }

        return false
    }

    private fun findPermissionExplain(permission: String): String? {
        return mPermissionModels
                .firstOrNull { it.permission == permission }
                ?.explain
    }

    private fun findPermissionName(permission: String): String? {
        return mPermissionModels
                .firstOrNull { it.permission == permission }
                ?.name
    }

    data class PermissionModel(val name: String, val permission: String, val explain: String, val requestCode: Int)

    companion object {
        private val REQUEST_OPEN_APPLICATION_SETTINGS_CODE = 12345
    }

}

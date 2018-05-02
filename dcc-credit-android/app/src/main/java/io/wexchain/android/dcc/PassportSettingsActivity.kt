package io.wexchain.android.dcc

import android.Manifest
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.DialogFragment
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.vm.Protect
import io.wexchain.android.localprotect.LocalProtectType
import io.wexchain.android.localprotect.fragment.CreateProtectFragment
import io.wexchain.android.localprotect.fragment.VerifyProtectFragment
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityPassportSettingsBinding
import android.provider.MediaStore
import android.support.v4.view.ViewCompat
import android.view.*
import com.tbruyelle.rxpermissions2.RxPermissions
import com.wexmarket.android.passport.ResultCodes
import io.wexchain.android.common.stackTrace
import io.wexchain.android.dcc.constant.RequestCodes


class PassportSettingsActivity: BindActivity<ActivityPassportSettingsBinding>() {
    override val contentLayoutId: Int = R.layout.activity_passport_settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        initVm()
        setupClicks()
    }

    private fun initVm() {
        val protect = getViewModel<Protect>()
        protect.sync(this)
        protect.protectEnableEvent.observe(this, Observer {
            CreateProtectFragment.create(object : CreateProtectFragment.CreateProtectFinishListener {
                override fun onCancel(): Boolean {
                    return true
                }

                override fun cancelText(): String {
                    return "取消"
                }

                override fun onCreateProtectFinish(type: LocalProtectType) {
                    toast(R.string.local_protect_enabled)
                }

            }).show(supportFragmentManager, null)
        })
        protect.protectDisabledEvent.observe(this, Observer {
            toast("关闭安全保护成功,您可以开启其他安全保护方式")
        })
        VerifyProtectFragment.serve(protect, this)
        binding.protect = protect
        App.get().passportRepository.currPassport.observe(this, Observer {
            binding.passport = it
        })
    }

    private fun setupClicks() {
        val binding = binding
        binding.tvUserAvatar.setOnClickListener {
            ChooseImageFromDialog.create(this).show(supportFragmentManager,null)
        }
        binding.tvUserNickname.setOnClickListener {
            startActivity(Intent(this, EditNicknameActivity::class.java))
        }
        binding.tvPassportAddress.setOnClickListener {
            startActivity(Intent(this, PassportAddressActivity::class.java))
        }
        binding.tvPassportBackup.setOnClickListener {
            startActivity(Intent(this, PassportExportActivity::class.java))
        }
        binding.tvModifyPassportPassword.setOnClickListener {
            startActivity(Intent(this, ModifyPassportPasswordActivity::class.java))
        }
//        binding.tvPassportIntro.setOnClickListener {
//            startActivity(Intent(this, PassportIntroductionActivity::class.java))
//        }
        binding.tvAboutUs.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
        binding.tvDeletePassport.setOnClickListener {
            startActivity(Intent(this, PassportRemovalActivity::class.java))
        }
    }

    private fun pickImage() {
        startActivity(Intent(this, ChooseCutImageActivity::class.java))
    }

    private fun captureImage() {
        RxPermissions(this)
                .request(Manifest.permission.CAMERA)
                .subscribe {
                    if (it) {
                        //granted
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        if (takePictureIntent.resolveActivity(packageManager) != null) {
                            startActivityForResult(takePictureIntent, RequestCodes.CAPTURE_IMAGE)
                        }
                    } else {
                        toast("没有相机权限")
                    }
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            RequestCodes.CAPTURE_IMAGE->{
                if(resultCode == ResultCodes.RESULT_OK){
                    val bitmap = data?.extras?.get("data") as? Bitmap
                    bitmap?.let {
                        App.get().passportRepository.saveAvatar(it)
                                .doOnSubscribe {
                                    showLoadingDialog()
                                }
                                .doFinally {
                                    hideLoadingDialog()
                                }
                                .subscribe({
                                    toast("头像修改成功")
                                }, {
                                    stackTrace(it)
                                })
                    }
                }
            }
            else-> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    class ChooseImageFromDialog:DialogFragment(){
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view = inflater.inflate(R.layout.dialog_choose_image_from, container, false)
            view.findViewById<View>(R.id.tv_capture).setOnClickListener {
                dismiss()
                host?.captureImage()
            }
            view.findViewById<View>(R.id.tv_pick).setOnClickListener {
                dismiss()
                host?.pickImage()
            }
            view.findViewById<View>(R.id.btn_cancel).setOnClickListener {
                dismiss()
            }
            ViewCompat.setElevation(view,resources.getDimension(R.dimen.dialog_elevation))
            return view
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            setStyle(STYLE_NORMAL,R.style.Theme_AppCompat_Light_Dialog_Alert_Dcc)
            val dialog = super.onCreateDialog(savedInstanceState)
            dialog.window.apply {
                setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
                setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            }
            return dialog
        }

        private var host:PassportSettingsActivity? = null

        companion object {
            fun create(activity: PassportSettingsActivity): ChooseImageFromDialog {
                val dialog = ChooseImageFromDialog()
                dialog.host = activity
                return dialog
            }
        }
    }
}
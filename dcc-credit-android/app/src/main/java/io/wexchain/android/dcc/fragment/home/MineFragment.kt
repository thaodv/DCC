package io.wexchain.android.dcc.fragment.home

import android.Manifest
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.DialogFragment
import android.support.v4.view.ViewCompat
import android.view.*
import android.view.animation.AnimationUtils
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.rxkotlin.subscribeBy
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.navigateTo
import io.wexchain.android.common.onClick
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.*
import io.wexchain.android.dcc.base.BindFragment
import io.wexchain.android.dcc.chain.IpfsOperations
import io.wexchain.android.dcc.chain.IpfsOperations.checkKey
import io.wexchain.android.dcc.constant.RequestCodes
import io.wexchain.android.dcc.modules.addressbook.activity.AddressBookActivity
import io.wexchain.android.dcc.modules.ipfs.activity.MyCloudActivity
import io.wexchain.android.dcc.modules.ipfs.activity.OpenCloudActivity
import io.wexchain.android.dcc.modules.mine.SettingActivity
import io.wexchain.android.dcc.modules.selectnode.SelectNodeActivity
import io.wexchain.android.dcc.vm.Protect
import io.wexchain.android.localprotect.LocalProtectType
import io.wexchain.android.localprotect.fragment.CreateProtectFragment
import io.wexchain.android.localprotect.fragment.VerifyProtectFragment
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.FragmentMineBinding
import io.wexchain.ipfs.utils.io_main

/**
 *Created by liuyang on 2018/9/18.
 */
class MineFragment : BindFragment<FragmentMineBinding>() {

    private val passport by lazy {
        App.get().passportRepository
    }

    override val contentLayoutId: Int
        get() = R.layout.fragment_mine

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClicks()
        initVm()
    }

    private fun initVm() {
        passport.currPassport.observe(this, Observer {
            binding.passport = it
        })
    }

    override fun onResume() {
        super.onResume()
        getCloudToken()
    }

    private fun getCloudToken() {
        val anim = AnimationUtils.loadAnimation(activity, R.anim.rotate)
        IpfsOperations.getIpfsKey()
                .checkKey()
                .io_main()
                .doOnSubscribe {
                    binding.ivCloudLoding.startAnimation(anim)
                    binding.ivCloudLoding.visibility = View.VISIBLE
                    binding.tvCloudStatus.visibility = View.INVISIBLE
                }
                .doFinally {
                    binding.ivCloudLoding.clearAnimation()
                    binding.ivCloudLoding.visibility = View.INVISIBLE
                    binding.tvCloudStatus.visibility = View.VISIBLE
                }
                .subscribeBy {
                    val ipfsKeyHash = passport.getIpfsKeyHash()
                    binding.tvCloudStatus.text = if (it.isEmpty()) getString(R.string.start_in) else getString(R.string.start_out)
                    binding.tvDataCloud.onClick {
                        if (it.isEmpty()) {
                            activity?.navigateTo(OpenCloudActivity::class.java) {
                                putExtra("activity_type", PassportSettingsActivity.NOT_OPEN_CLOUD)
                            }
                        } else {
                            if (ipfsKeyHash.isNullOrEmpty()) {
                                activity?.navigateTo(OpenCloudActivity::class.java) {
                                    putExtra("activity_type", PassportSettingsActivity.OPEN_CLOUD)
                                }
                            } else {
                                if (ipfsKeyHash == it) {
                                    activity?.navigateTo(MyCloudActivity::class.java)
                                } else {
                                    passport.setIpfsKeyHash("")
                                    activity?.navigateTo(OpenCloudActivity::class.java) {
                                        putExtra("activity_type", PassportSettingsActivity.OPEN_CLOUD)
                                    }
                                }
                            }
                        }
                    }
                }
    }

    private fun setupClicks() {
        val binding = binding
        binding.tvUserAvatar.onClick {
            ChooseImageFromDialog.create(this).show(childFragmentManager, null)
        }
        binding.tvUserNickname.onClick {
            navigateTo(EditNicknameActivity::class.java)
        }
        binding.tvAddressBook.onClick {
            navigateTo(AddressBookActivity::class.java)
        }
        binding.tvSetting.onClick {
            navigateTo(SettingActivity::class.java)
        }
        binding.tvPassportBackup.onClick {
            navigateTo(PassportExportActivity::class.java)
        }
        binding.tvModifyPassportPassword.onClick {
            navigateTo(ModifyPassportPasswordActivity::class.java)
        }
    }

    private fun pickImage() {
        navigateTo(ChooseCutImageActivity::class.java)
    }

    private fun captureImage() {
        RxPermissions(activity!!)
                .request(Manifest.permission.CAMERA)
                .subscribe {
                    if (it) {
                        //granted
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        if (takePictureIntent.resolveActivity(activity!!.packageManager) != null) {
                            startActivityForResult(takePictureIntent, RequestCodes.CAPTURE_IMAGE)
                        }
                    } else {
                        toast("没有相机权限")
                    }
                }
    }



    class ChooseImageFromDialog : DialogFragment() {
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
            ViewCompat.setElevation(view, resources.getDimension(R.dimen.dialog_elevation))
            return view
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            setStyle(STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog_Alert_Dcc)
            val dialog = super.onCreateDialog(savedInstanceState)
            dialog.window.apply {
                setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
                setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            }
            return dialog
        }

        private var host: MineFragment? = null

        companion object {
            fun create(activity: MineFragment): ChooseImageFromDialog {
                val dialog = ChooseImageFromDialog()
                dialog.host = activity
                return dialog
            }
        }
    }


}
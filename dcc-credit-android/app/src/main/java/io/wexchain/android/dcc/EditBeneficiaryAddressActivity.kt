package io.wexchain.android.dcc

import android.Manifest
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.DialogFragment
import android.support.v4.view.ViewCompat
import android.view.*
import android.widget.EditText
import com.tbruyelle.rxpermissions2.RxPermissions
import com.wexmarket.android.passport.ResultCodes
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.postOnMainThread
import io.wexchain.android.common.stackTrace
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BindActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.constant.RequestCodes
import io.wexchain.android.dcc.repo.db.BeneficiaryAddress
import io.wexchain.android.dcc.tools.CommonUtils
import io.wexchain.android.dcc.tools.isAddressShortNameValid
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityEditBeneficiaryAddressBinding
import io.wexchain.digitalwallet.util.isEthAddress
import java.io.File

class EditBeneficiaryAddressActivity : BindActivity<ActivityEditBeneficiaryAddressBinding>() {

    var realFilePath: String? = ""

    override val contentLayoutId: Int
        get() = R.layout.activity_edit_beneficiary_address

    private val ba
        get() = intent.getSerializableExtra(Extras.EXTRA_BENEFICIARY_ADDRESS) as? BeneficiaryAddress

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        title = "编辑地址"
        val beneficiaryAddress = ba
        if (beneficiaryAddress == null) {
            postOnMainThread { finish() }
        } else {
            App.get().passportRepository.defaultBeneficiaryAddress.observe(this, Observer {})
            binding.etInputAddress.setText(beneficiaryAddress.address)
            binding.etAddressShortName.setText(beneficiaryAddress.shortName)
            binding.ivAvatar.setImageURI(CommonUtils.str2Uri(beneficiaryAddress!!.avatarUrl))
            //binding.checkDefaultAddress.isChecked = App.get().passportRepository.defaultBeneficiaryAddress.value == beneficiaryAddress.address
            initClicks()
        }
    }

    private fun initClicks() {
        /*binding.ibScanAddress.setOnClickListener {
            startActivityForResult(Intent(this, QrScannerActivity::class.java).apply {
                putExtra(Extras.EXPECTED_SCAN_TYPE, QrScannerActivity.SCAN_TYPE_ADDRESS)
            }, RequestCodes.SCAN)
        }*/
        binding.btnSave.setOnClickListener {
            val inputAddr = binding.etInputAddress.text.toString()
            val inputShortName = binding.etAddressShortName.text.toString()
            if (!isEthAddress(inputAddr)) {
                toast("地址格式不符,请核对后重新输入")
            } else if (inputAddr.isEmpty() || !isAddressShortNameValid(inputShortName)) {
                toast("请输入有效的简称")
            } else {
                //val setDefault = binding.checkDefaultAddress.isChecked
                val passportRepository = App.get().passportRepository
                passportRepository.addBeneficiaryAddress(BeneficiaryAddress(inputAddr, inputShortName, realFilePath))
                /*if (setDefault) {
                    passportRepository.setDefaultBeneficiaryAddress(inputAddr)
                }else if(passportRepository.defaultBeneficiaryAddress.value == inputAddr){
                    passportRepository.setDefaultBeneficiaryAddress(null)
                }*/
                toast("修改成功")
                finish()
            }
        }

        binding.ivAvatar.setOnClickListener {
            EditBeneficiaryAddressActivity.ChooseImageFromDialog.create(this).show(supportFragmentManager, null)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodes.SCAN -> {
                val address = data?.getStringExtra(QrScannerActivity.EXTRA_SCAN_RESULT)
                if (address != null) {
                    findViewById<EditText>(R.id.et_input_address).setText(address)
                }
            }

            RequestCodes.CAPTURE_IMAGE -> {
                if (resultCode == ResultCodes.RESULT_OK) {
                    val bitmap = data?.extras?.get("data") as? Bitmap
                    bitmap?.let {
                        saveAvatar(it)
                                .doOnSubscribe {
                                    showLoadingDialog()
                                }
                                .doFinally {
                                    hideLoadingDialog()
                                }
                                .subscribe({
                                    realFilePath = it.toString()
                                    binding.ivAvatar.setImageURI(it)
                                }, {
                                    stackTrace(it)
                                })
                    }
                }
            }
            RequestCodes.PICK_AVATAR -> {
                if (RequestCodes.PICK_AVATAR_BACK == resultCode) {
                    realFilePath = data?.getStringExtra(Extras.EXTRA_PICKAVATAR_BACK)
                    binding.ivAvatar.setImageURI(Uri.parse(realFilePath))
                }
            }

            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun saveAvatar(bitmap: Bitmap): Single<Uri> {
        val filesDir = App.get().filesDir
        return Single.just(bitmap)
                .map { bitmap ->
                    val imgDir = File(filesDir, ChooseCutImageActivity.IMG_DIR)
                    if (!imgDir.exists()) {
                        val mkdirs = imgDir.mkdirs()
                        assert(mkdirs)
                    }
                    val fileName =
                            "${System.currentTimeMillis().toString(16).padStart(
                                    16,
                                    '0'
                            )}.png"
                    val imgFile = File(filesDir, fileName)
                    imgFile.outputStream()
                            .use {
                                bitmap.compress(Bitmap.CompressFormat.PNG, 87, it)
                            }
                    Uri.fromFile(imgFile)
                }
                .observeOn(AndroidSchedulers.mainThread())
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_asset_scan, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_asset_scan -> {
                startActivityForResult(Intent(this, QrScannerActivity::class.java).apply {
                    putExtra(Extras.EXPECTED_SCAN_TYPE, QrScannerActivity.SCAN_TYPE_ADDRESS)
                }, RequestCodes.SCAN)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun pickImage() {
        startActivityForResult(Intent(this, ChooseCutImageActivity::class.java).apply {
            putExtra(Extras.EXTRA_PICKAVATAR, "1")
        }, RequestCodes.PICK_AVATAR)
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

        private var host: EditBeneficiaryAddressActivity? = null

        companion object {
            fun create(activity: EditBeneficiaryAddressActivity): ChooseImageFromDialog {
                val dialog = ChooseImageFromDialog()
                dialog.host = activity
                return dialog
            }
        }
    }


}

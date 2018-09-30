package io.wexchain.android.dcc.modules.addressbook.activity

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
import io.wexchain.android.common.stackTrace
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.ChooseCutImageActivity
import io.wexchain.android.dcc.QrScannerActivity
import io.wexchain.android.common.base.BindActivity
import io.wexchain.android.dcc.constant.Extras
import io.wexchain.android.dcc.constant.RequestCodes
import io.wexchain.android.dcc.repo.db.AddressBook
import io.wexchain.android.dcc.repo.db.TransRecord
import io.wexchain.android.dcc.tools.CommonUtils
import io.wexchain.android.dcc.tools.LogUtils
import worhavah.regloginlib.tools.isAddressShortNameValid
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.ActivityEditAddressBookBinding
import io.wexchain.digitalwallet.util.isEthAddress
import java.io.File

class EditAddressBookActivity : BindActivity<ActivityEditAddressBookBinding>() {

    var realFilePath: String? = ""

    override val contentLayoutId: Int
        get() = R.layout.activity_edit_address_book

    private val ba
        get() = intent.getSerializableExtra(Extras.EXTRA_BENEFICIARY_ADDRESS) as AddressBook

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()
        title = getString(R.string.edit_address)

        binding.etInputAddress.setText(ba.address)
        binding.etAddressShortName.setText(ba.shortName)
        binding.ivAvatar.setImageURI(CommonUtils.str2Uri(ba.avatarUrl))
        initClicks()

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
                toast("名称不能为空")
            } else {
                //val setDefault = binding.checkDefaultAddress.isChecked
                val passportRepository = App.get().passportRepository
                passportRepository.addOrUpdateAddressBook(AddressBook(inputAddr, inputShortName, avatarUrl = realFilePath, create_time = ba.create_time, update_time = System.currentTimeMillis()))

                App.get().passportRepository.getTransRecordByAddress(inputAddr).observe(this, Observer {
                    var mTrans: ArrayList<TransRecord> = ArrayList()
                    if (null != it && it.isNotEmpty()) {
                        for (item in it) {
                            mTrans.add(TransRecord(item.id, inputAddr, inputShortName, avatarUrl = realFilePath, is_add = 1, create_time = item.create_time, update_time = System.currentTimeMillis()))
                        }
                        App.get().passportRepository.updateTransRecord(mTrans)
                    }
                })
                toast("修改成功")
                finish()
            }
        }

        binding.ivAvatar.setOnClickListener {
            ChooseImageFromDialog.create(this).show(supportFragmentManager, null)
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

                    LogUtils.i("path", realFilePath)

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
                    val imgDir = File(filesDir, ChooseCutImageActivity.IMG_DIR_ADDRESS)
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

        RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe {
                    if (it) {
                        //granted
                        startActivityForResult(Intent(this, ChooseCutImageActivity::class.java).putExtra(Extras.EXTRA_PICKAVATAR, 1), RequestCodes.PICK_AVATAR)
                    } else {
                        toast("没有读写权限")
                    }
                }
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

        private var host: EditAddressBookActivity? = null

        companion object {
            fun create(activity: EditAddressBookActivity): ChooseImageFromDialog {
                val dialog = ChooseImageFromDialog()
                dialog.host = activity
                return dialog
            }
        }
    }


}

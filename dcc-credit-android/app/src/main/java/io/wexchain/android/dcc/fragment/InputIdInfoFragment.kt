package io.wexchain.android.dcc.fragment

import android.Manifest
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.megvii.idcardlib.IDCardScanActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import io.wexchain.android.dcc.constant.RequestCodes
import com.wexmarket.android.passport.ResultCodes
import io.wexchain.android.dcc.base.BindFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.getViewModel
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.vm.EditIdCardInfoVm
import io.wexchain.android.dcc.vm.domain.IdCardCertData
import io.wexchain.android.idverify.IdVerifyHelper
import io.wexchain.android.idverify.IdVerifyHelper.Companion.SIDE_BACK
import io.wexchain.android.idverify.IdVerifyHelper.Companion.SIDE_FRONT
import io.wexchain.dcc.R
import io.wexchain.dcc.databinding.FragmentEditIdInfoBinding

class InputIdInfoFragment : BindFragment<FragmentEditIdInfoBinding>() {
    override val contentLayoutId: Int = R.layout.fragment_edit_id_info

    private var listener: Listener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVm()
    }

    private fun initVm() {
        val vm = getViewModel<EditIdCardInfoVm>()
        vm.informationIncompleteEvent.observe(this, Observer {
            it?.let { toast(it) }
        })
        vm.proceedEvent.observe(this, Observer {
            it?.let { listener?.onProceed(it) }
        })
        vm.ocrEvent.observe(this, Observer {
            binding.executePendingBindings()
        })
        vm.ocrFailEvent.observe(this, Observer {
            it?.message?.let {
                println("ocr fail : $it")
                toast("ocr 失败   系统繁忙请稍后再试")
            }
            binding.executePendingBindings()
        })
        vm.ocrProcessing.observe(this, Observer {
            it?.let {
                if (it){
                    (activity as? BaseCompatActivity)?.showLoadingDialog()
                }else{
                    (activity as? BaseCompatActivity)?.hideLoadingDialog()
                }
            }
        })
        binding.vm = vm
        binding.ibIdFront.setOnClickListener {
            enterScan(IdVerifyHelper.SIDE_FRONT)
        }
        binding.ibIdBack.setOnClickListener {
            enterScan(IdVerifyHelper.SIDE_BACK)
        }
    }

    override fun onResume() {
        super.onResume()
        activity!!.setTitle(R.string.title_id_certification)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodes.SCAN_ID_CARD -> {
                if (resultCode == ResultCodes.RESULT_OK && data != null) {
                    val side = data.getIntExtra("side", SIDE_FRONT)
                    val imgBytes = data.getByteArrayExtra("idcardImg")!!
                    val vm = binding.vm!!
                    when (side) {
                        SIDE_FRONT, SIDE_BACK -> {
                            vm.setImage(imgBytes, side)
                        }
                    }
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun enterScan(side: Int) {
        val context = activity
        context ?: return
        App.get().idVerifyHelper.ensureLicence()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    RxPermissions(context)
                            .request(Manifest.permission.CAMERA)
                            .subscribe {
                                if (it) {
                                    val intent = Intent(context, IDCardScanActivity::class.java)
                                    intent.putExtra("side", side)
//                    intent.putExtra("isvertical", isVertical)
                                    startActivityForResult(intent, RequestCodes.SCAN_ID_CARD)
                                } else {
                                    toast("没有相机权限", context)
                                }
                            }
                }, {
                    toast("licence fail", context)
                })
    }

    interface Listener {
        fun onProceed(idCardCertData: IdCardCertData)
    }

    companion object {
        fun create(listener: Listener): InputIdInfoFragment {
            return InputIdInfoFragment().apply {
                this.listener = listener
            }
        }
    }
}
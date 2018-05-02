package io.wexchain.android.dcc

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.lyft.android.scissors.CropView
import io.wexchain.android.dcc.constant.RequestCodes
import com.wexmarket.android.passport.ResultCodes
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.wexchain.android.common.stackTrace
import io.wexchain.android.common.toast
import io.wexchain.android.dcc.base.BaseCompatActivity
import io.wexchain.android.dcc.network.GlideApp
import io.wexchain.dcc.R
import java.io.File

class ChooseCutImageActivity : BaseCompatActivity() {

    private lateinit var cropView: CropView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.activity_choose_cut_image)
        initToolbar(true)
        cropView = findViewById<CropView>(R.id.iv_cut_preview)
        cropView.extensions().pickUsing(this, RequestCodes.GET_PICTURE)
        findViewById<Button>(R.id.btn_use_crop).setOnClickListener {
            performCropAndSavePic()
        }
    }

    private fun performCropAndSavePic() {
        val passportRepo = App.get().passportRepository
        val passport = passportRepo.getCurrentPassport()
        if (passport != null) {
            Single.just(passport)
                .observeOn(Schedulers.computation())
                .map {
                    val crop = cropView.crop()!!
                    it to crop
                }
                .observeOn(Schedulers.io())
                .map { (passport, bitmap) ->
                    val imgDir = File(filesDir, IMG_DIR)
                    if (!imgDir.exists()) {
                        val mkdirs = imgDir.mkdirs()
                        assert(mkdirs)
                    }
                    val fileName =
                        "${passport.address}-${System.currentTimeMillis().toString(16).padStart(
                            16,
                            '0'
                        )}.png"
                    val imgFile = File(filesDir, fileName)
                    imgFile.outputStream()
                        .use {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 87, it)
                        }
                    passport to Uri.fromFile(imgFile)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    passportRepo.updatePassportUserAvatar(it.first, it.second)
                    it
                }
                .doOnSubscribe {
                    showLoadingDialog()
                }
                .doFinally {
                    hideLoadingDialog()
                }
                .subscribe({
                    toast("头像修改成功")
                    finish()
                }, {
                    stackTrace(it)
                })
        } else {
            // todo
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodes.GET_PICTURE -> {
                if (resultCode == ResultCodes.RESULT_OK && data?.data != null) {
                    val uri = data.data
                    GlideApp.with(cropView)
                            .load(uri)
                            .into(cropView)
                } else {
                    toast("未选择任何图片")
                    finish()
                }
            }
            else ->
                super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        const val IMG_DIR = "avatar_images"
    }
}

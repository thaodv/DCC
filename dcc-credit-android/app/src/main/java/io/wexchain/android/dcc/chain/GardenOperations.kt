package io.wexchain.android.dcc.chain

import android.content.Context
import android.net.Uri
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.modelmsg.SendAuth
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.ChooseCutImageActivity
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.tools.toBean
import io.wexchain.android.dcc.tools.toJson
import io.wexchain.dcc.BuildConfig
import io.wexchain.dcc.WxApiManager
import io.wexchain.dccchainservice.MarketingApi
import io.wexchain.dccchainservice.domain.LoginInfo
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.util.ParamSignatureUtil
import io.wexchain.ipfs.utils.doMain
import retrofit2.Response
import zlc.season.rxdownload3.RxDownload
import zlc.season.rxdownload3.core.Mission
import zlc.season.rxdownload3.core.Succeed
import java.io.File

/**
 *Created by liuyang on 2018/8/21.
 */
object GardenOperations {

    private val passport by lazy {
        App.get().passportRepository
    }

    fun loginWithCurrentPassport(context: Context): Single<Response<Result<LoginInfo>>> {
        val api = App.get().marketingApi
        val address = passport.currPassport.value?.address
        val privateKey = passport.getCurrentPassport()?.authKey?.getPrivateKey()
        return if (address == null || privateKey == null) {
            Single.error(Throwable("passport is null"))
        } else api.getNonce2()
                .check()
                .flatMap {
                    api.login(address, it, ParamSignatureUtil.sign(
                            privateKey, mapOf(
                            "nonce" to it,
                            "address" to address
                    )))
                }
                .doMain()
                .map {
                    val body = it.body()
                    if (it.isSuccessful && body != null) {
                        if (body.isSuccess) {
                            it
                        } else {
                            throw body.asError()
                        }
                    } else {
                        throw IllegalStateException()
                    }
                }
                .doOnSuccess {
                    App.get().gardenTokenManager.gardenToken = it.headers()[MarketingApi.HEADER_TOKEN]!!
                    val info = it.body()!!.result!!
                    passport.setUserInfo(info.toJson())
                    App.get().isLogin.postValue(true)

                    info.member.profilePhoto?.let {
                        val filesDir = App.get().filesDir
                        val imgDir = File(filesDir, ChooseCutImageActivity.IMG_DIR)
                        if (!imgDir.exists()) {
                            val mkdirs = imgDir.mkdirs()
                            assert(mkdirs)
                        }
                        val fileName = "${passport.getCurrentPassport()!!.address}-useravatar.png"
                        val imgFile = File(imgDir, fileName)
                        RxDownload.create(Mission(it, fileName, imgDir.absolutePath))
                                .filter {
                                    it is Succeed
                                }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeBy {
                                    if (imgFile.exists()) {
                                        val uri = Uri.fromFile(imgFile)
                                        passport.updatePassportUserAvatar(passport.getCurrentPassport()!!, uri)
                                    }
                                }


                        /*GlideApp.with(context).asBitmap().load(it).into(object : SimpleTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                passport.saveAvatar(resource)
                            }
                        })*/
                    }

                    info.member.name?.let {
                        passport.updatePassportNickname(passport.getCurrentPassport()!!, it)
                    }
                }
    }

    fun boundWechat(code: String): Single<String> {
        val address = passport.currPassport.value?.address
        val app = App.get()
        return if (address == null) {
            Single.error(Throwable("passport is null"))
        } else app.marketingApi.bound(app.gardenTokenManager.gardenToken, address, code).check()
    }

    fun wechatLogin(erroe: (String) -> Unit) {
        val wxapi = WxApiManager.wxapi.isWXAppInstalled
        if (!wxapi) {
            erroe("您还未安装微信客户端")
            return
        }
        val req = SendAuth.Req()
        req.scope = "snsapi_userinfo"
        req.openId = BuildConfig.WECHAT_OPEN_APP_ID
        req.state = "bitexpress_login"
        WxApiManager.wxapi.sendReq(req)
    }

    fun isBound(): Boolean {
        val userinfo = passport.getUserInfo()
        return if (null == userinfo) {
            false
        } else {
            val info = userinfo.toBean(LoginInfo::class.java)
            if (null == info.player) {
                false
            } else {
                info.player?.id != null
            }
        }
    }

    fun startWechat(error: (String) -> Unit) {
        val info = passport.getUserInfo()
        if (null == info) {
            error("用户未登录")
            return
        }
        val data = info.toBean(LoginInfo::class.java)
        if (data.player == null) {
            error("未绑定微信")
            return
        }
        val req = WXLaunchMiniProgram.Req()
        req.userName = "gh_0d13628f5e03"
        req.path = "/pages/login/login?playerid=${data.player!!.id}"
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_TEST
        WxApiManager.wxapi.sendReq(req)
    }


}

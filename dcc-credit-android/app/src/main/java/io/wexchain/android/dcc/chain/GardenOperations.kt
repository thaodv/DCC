package io.wexchain.android.dcc.chain

import android.graphics.BitmapFactory
import android.net.Uri
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.ChooseCutImageActivity
import io.wexchain.android.dcc.tools.check
import io.wexchain.android.dcc.tools.toJson
import io.wexchain.dcc.BuildConfig
import io.wexchain.dcc.R
import io.wexchain.dcc.WxApiManager
import io.wexchain.dccchainservice.MarketingApi
import io.wexchain.dccchainservice.domain.ChangeOrder
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.domain.UserInfo
import io.wexchain.dccchainservice.type.TaskCode
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

    private val api: MarketingApi by lazy {
        App.get().marketingApi
    }

    fun loginWithCurrentPassport(): Single<Pair<UserInfo, String>> {
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
                .map {
                    val body = it.body()
                    if (it.isSuccessful && body != null) {
                        if (body.isSuccess) {
                            val token = it.headers()[MarketingApi.HEADER_TOKEN]!!
                            App.get().gardenTokenManager.gardenToken = token
                            it.body()!!.result!! to token
                        } else {
                            throw body.asError()
                        }
                    } else {
                        throw IllegalStateException()
                    }
                }
                .doMain()
                .doOnSuccess {
                    val userinfo = it.first
                    App.get().userInfo = userinfo
                    passport.setUserInfo(it.toJson())

                    userinfo.member.profilePhoto?.let {
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
                    }

                    userinfo.member.name?.let {
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

    fun wechatLogin(error: (String) -> Unit) {
        val wxapi = WxApiManager.wxapi.isWXAppInstalled
        if (!wxapi) {
            error("您还未安装微信客户端")
            return
        }
        val req = SendAuth.Req()
        req.scope = "snsapi_userinfo"
        req.openId = BuildConfig.WECHAT_OPEN_APP_ID
        req.state = "bitexpress_login"
        WxApiManager.wxapi.sendReq(req)
    }

    fun isBound(): Boolean {
        val userinfo = App.get().userInfo
        return if (userinfo == null) {
            false
        } else {
            if (userinfo.player == null) {
                false
            } else {
                userinfo.player!!.id != null
            }
        }
    }

    fun isLogin(): Boolean {
        val userinfo = App.get().userInfo
        return userinfo != null
    }

    fun ((String) -> Unit).check(success: (Int) -> Unit) {
        val wxapi = WxApiManager.wxapi.isWXAppInstalled
        if (!wxapi) {
            this("您还未安装微信客户端")
            return
        }
        val info = App.get().userInfo
        if (null == info) {
            this("用户未登录")
            return
        }
        if (info.player == null) {
            this("未绑定微信")
            return
        } else {
            success(info.player!!.id!!)
        }
    }

    fun startWechat(error: (String) -> Unit) {
        error.check {
            val req = WXLaunchMiniProgram.Req()
            req.userName = "gh_0d13628f5e03"
            req.path = "/pages/contest/contest?playId=$it"
            req.miniprogramType = WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW
            WxApiManager.wxapi.sendReq(req)
        }
    }

    fun shareWechat(error: (String) -> Unit) {
        error.check {
            val miniProgramObj = WXMiniProgramObject()
                    .apply {
                        webpageUrl = "http://open.dcc.finance/dapp/invite/index.html" // 兼容低版本的网页链接
                        miniprogramType = WXMiniProgramObject.MINIPROGRAM_TYPE_PREVIEW// 正式版:0，测试版:1，体验版:2
                        userName = "gh_0d13628f5e03"
                        path = "/pages/login/login?playId=$it"
                    }

            val msg = WXMediaMessage(miniProgramObj)
                    .apply {
                        setThumbImage(BitmapFactory.decodeResource(App.get().resources, R.drawable.wechat_share))
                        title = "哈哈，既然发现了我，不如顺手来偷点糖果吧。"
                        description = ""
                    }

            val req = SendMessageToWX.Req()
                    .apply {
                        transaction = buildTransaction("webpage", false)
                        message = msg
                        scene = SendMessageToWX.Req.WXSceneSession  // 目前支持会话
                    }
            WxApiManager.wxapi.sendReq(req)
        }
    }


    private fun buildTransaction(code: String, toCircle: Boolean): String {
        return "share_${code}_${System.currentTimeMillis()}_${if (toCircle) 1 else 0}"
    }

    fun completeTask(taskCode: TaskCode): Single<ChangeOrder> {
        return GardenOperations
                .refreshToken {
                    api.completeTask(it, taskCode.name).check()
                }

    }

    fun <T> refreshToken(data: (String) -> Single<T>): Single<T> {
        return Single
                .defer {
                    Single.fromCallable {
                        App.get().gardenTokenManager.gardenToken
                    }
                }
                .flatMap {
                    data(it)
                }
                .retryWhen {
                    var mRetryCount = 0
                    if (mRetryCount < 3) {
                        mRetryCount += 1
                        loginWithCurrentPassport()
                                .map {
                                    it.second
                                }
                                .toFlowable()
                    } else {
                        Flowable.just(Throwable())
                    }
                }
    }


}

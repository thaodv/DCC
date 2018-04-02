package io.wexchain.android.idverify

import android.content.Context
import com.megvii.idcardquality.IDCardQualityLicenseManager
import com.megvii.licensemanager.Manager
import com.megvii.livenessdetection.LivenessLicenseManager
import com.megvii.livenesslib.util.ConUtil
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * Created by sisel on 2018/2/27.
 * id card capture sdk & liveness sdk helper
 */
class IdVerifyHelper(context: Context,
                     val uuid: String = ConUtil.getUUIDString(context)) {

    val manager = Manager(context)
    val idLicenseManager = IDCardQualityLicenseManager(context)
    val livenessLicenseManager = LivenessLicenseManager(context)

    private inline val idLicenceOk: Boolean
        get() = idLicenseManager.checkCachedLicense() > 0 &&
                livenessLicenseManager.checkCachedLicense() > 0

    init {
        manager.registerLicenseManager(idLicenseManager)
        manager.registerLicenseManager(livenessLicenseManager)
        ensureLicence().subscribe()
    }

    fun ensureLicence(): Single<Boolean> {
        return Single.just(System.currentTimeMillis())
                .observeOn(Schedulers.io())
                .map {
                    if (idLicenceOk) {
                        true
                    } else {
                        manager.takeLicenseFromNetwork(uuid)
                        if (idLicenceOk) {
                            true
                        } else {
                            throw IllegalStateException()
                        }
                    }
                }
    }

    companion object {

        const val SIDE_FRONT = 0
        const val SIDE_BACK = 1
    }
}
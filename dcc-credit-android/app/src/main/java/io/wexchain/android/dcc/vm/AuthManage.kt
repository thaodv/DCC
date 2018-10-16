package io.wexchain.android.dcc.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.databinding.ObservableField
import io.reactivex.android.schedulers.AndroidSchedulers
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.chain.PassportOperations
import io.wexchain.android.dcc.domain.Passport
import io.wexchain.android.dcc.repo.db.AuthKeyChangeRecord
import io.wexchain.android.localprotect.LocalProtectType
import io.wexchain.android.localprotect.UseProtect
import io.wexchain.dccchainservice.domain.Result
import io.wexchain.dccchainservice.domain.TicketResponse
import io.wexchain.android.dcc.domain.AuthKey
/**
 * Created by lulingzhi on 2017/11/24.
 */
class AuthManage(application: Application) : AndroidViewModel(application), UseProtect {

    private val repo = getApplication<App>().passportRepository


    override val type = ObservableField<LocalProtectType>()
    override val protectChallengeEvent = SingleLiveEvent<(Boolean) -> Unit>()

    val passport = ObservableField<Passport?>()
    val authKey = ObservableField<AuthKey?>()
    val changeRecords = repo.authKeyChangeRecords

    val loadingEvent = SingleLiveEvent<Boolean>()
    val successEvent = SingleLiveEvent<Void>()
    val authKeyChangeEvent = SingleLiveEvent<AuthKeyChangeRecord.UpdateType>()
    val errorEvent = SingleLiveEvent<Throwable>()

    fun sync(lifecycleOwner: LifecycleOwner) {
        syncProtect(lifecycleOwner)
        repo.currPassport.observe(lifecycleOwner, Observer {
            passport.set(it)
            authKey.set(it?.authKey)
        })
    }

    fun replaceAuthKey() {
        verifyProtect {
            updateAuthKey(AuthKeyChangeRecord.UpdateType.UPDATE)
        }
    }

    private fun updateAuthKey(updateType: AuthKeyChangeRecord.UpdateType) {
        val p = passport.get()!!
        App.get().chainGateway.getTicket()
                .compose(Result.checked())
                .flatMap {
                    require(it.accessRestriction == TicketResponse.AccessRestriction.GRANTED)
                    PassportOperations.updatePubKeyAndUploadChecked(p, it.ticket, null)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    App.get().passportRepository.addAuthKeyChangedRecord(
                            AuthKeyChangeRecord(p.address, System.currentTimeMillis(), updateType)
                    )
                    App.get().initcerts()
                    successEvent.call()
                }
                .doOnSubscribe {
                    loadingEvent.value = true
                }
                .doFinally {
                    loadingEvent.value = false
                }
                .subscribe(
                        {
                            authKeyChangeEvent.value = updateType
                        },
                        {
                            errorEvent.value = it
                        }
                )
    }

    fun switchPassportLoginEnable() {
        if (authKey.get() == null) {
            verifyProtect {
                updateAuthKey(AuthKeyChangeRecord.UpdateType.ENABLE)
            }
        } else {
            verifyProtect {
                disableAuthKey()
            }
        }
    }

    private fun disableAuthKey() {
        val p = passport.get()!!
        App.get().chainGateway.getTicket()
                .compose(Result.checked())
                .flatMap {
                    require(it.accessRestriction == TicketResponse.AccessRestriction.GRANTED)
                    PassportOperations.deletePubKeyAndUploadChecked(p, it.ticket, null)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    loadingEvent.value = true
                }
                .doFinally {
                    loadingEvent.value = false
                }
                .subscribe({}, {
                    errorEvent.value = it
                })
    }
}
package io.wexchain.android.dcc.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableField
import io.wexchain.android.common.SingleLiveEvent
import io.wexchain.android.dcc.App
import io.wexchain.android.dcc.domain.Passport

/**
 * Created by sisel on 2018/1/30.
 */
class EditNicknameVm(application: Application) : AndroidViewModel(application) {

    private val passportRepo = getApplication<App>().passportRepository

    val inputNickname = ObservableField<String>()

    val passport = ObservableField<Passport>()

    val changeSubmittedEvent = SingleLiveEvent<String>()

    fun encurePassport(curr: Passport) {
        passport.set(curr)
        inputNickname.set(curr.nickname)
    }

    fun check(passport: Passport?, input: String?): Boolean {
        val ori = passport?.nickname
        return ori != input
    }

    fun submitChange() {
        val p = passport.get()!!
        val ori = p.nickname
        val input = inputNickname.get()
        if (ori != input) {
            passportRepo.updatePassportNickname(p, input)
            changeSubmittedEvent.value = input
        }
    }
}
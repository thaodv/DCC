package io.wexchain.android.dcc.view.dialog

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.wexchain.android.common.commitTransaction
import io.wexchain.android.dcc.view.dialog.bonus.BonusAppearFragment
import io.wexchain.android.dcc.view.dialog.bonus.BonusRedeemFragment
import io.wexchain.android.dcc.vm.RedeemBonusVm
import io.wexchain.dcc.R
import io.wexchain.dccchainservice.domain.RedeemToken

class BonusDialog : DialogFragment() {

    private lateinit var redeemBonusVm: RedeemBonusVm
    private lateinit var listener: Listener

    private val bonusAppearFragment by lazy { BonusAppearFragment.create(redeemBonusVm) }

    private val bonusRedeemFragment by lazy { BonusRedeemFragment.create(redeemBonusVm) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm = redeemBonusVm
        vm.skipEvent.observe(this, Observer {
            dismiss()
            listener.onSkip()
        })
        vm.redeemEvent.observe(this, Observer {
            tryRedeem()
        })
        vm.redeemCompleteEvent.observe(this, Observer {
            dismiss()
            listener.onComplete()
        })
    }

    private fun tryRedeem() {
        //todo
        childFragmentManager.commitTransaction {
            replace(R.id.fl_container,bonusRedeemFragment)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_bonus, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.commitTransaction {
            replace(R.id.fl_container,bonusAppearFragment)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    interface Listener{
        fun onSkip()
        fun onComplete()
    }

    companion object {
        fun create(redeemToken: RedeemToken,listener: Listener): BonusDialog {
            val vm = RedeemBonusVm().apply {
                init(redeemToken)
            }
            return BonusDialog().apply {
                this.redeemBonusVm = vm
                this.listener = listener
            }
        }
    }

}
package io.wexchain.android.dcc.view.dialog

import android.app.Dialog
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.support.annotation.StyleRes
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import io.wexchain.android.dcc.vm.CDVm
import io.wexchain.auth.R
import io.wexchain.auth.databinding.DialogCustomBinding

/**
 * Created by lulingzhi on 2017/11/29.
 */
class CustomDialog(context: Context, @StyleRes theme: Int = R.style.Theme_AppCompat_Light_Dialog_Alert_Dcc) : Dialog(context, theme) {

    var textContent: CharSequence? = null
    var viewContent: View? = null
    val vm = CDVm()

    private var textTitle: CharSequence? = null

    var positiveButton: Pair<CharSequence, () -> Boolean>? = defaultPositiveButton()
    var negativeButton: Pair<CharSequence, () -> Boolean>? = null
    var neutralButton: Pair<CharSequence, () -> Boolean>? = null

    private fun defaultPositiveButton(): Pair<CharSequence, () -> Boolean> {
        return Pair(context.getString(R.string.confirm), onClickDismiss)
    }

    fun withPositiveButton(text: CharSequence = context.getString(R.string.confirm), onClick: () -> Boolean = onClickDismiss): CustomDialog {
        this.positiveButton = text to onClick
        return this
    }

    fun withNegativeButton(text: CharSequence = context.getString(R.string.cancel), onClick: () -> Boolean = onClickDismiss): CustomDialog {
        this.negativeButton = text to onClick
        return this
    }

    fun assembleAndShow() {
        assemble()
        show()
    }

    private fun assemble() {
        val binding = DataBindingUtil.inflate<DialogCustomBinding>(layoutInflater, R.layout.dialog_custom, null, false)
        binding.vm = vm
        setupVm()
        positiveButton?.let { (_, h) -> setButtonOnClick(binding.button1, h) }
        neutralButton?.let { (_, h) -> setButtonOnClick(binding.button2, h) }
        negativeButton?.let { (_, h) -> setButtonOnClick(binding.button3, h) }
        if (viewContent != null) {
            binding.flContent.removeAllViews()
            binding.flContent.addView(viewContent)
        }
        setContentView(binding.root)
    }

    private fun setButtonOnClick(button: Button, onClick: () -> Boolean) {
        button.setOnClickListener {
            if (onClick()) {
                dismiss()
            }
        }
    }

    private fun setupVm() {
        vm.showTitle.set(textTitle != null)
        vm.showText.set(textContent != null)
        vm.showButton1.set(positiveButton != null)
        vm.showButton2.set(neutralButton != null)//neutral button
        vm.showButton3.set(negativeButton != null)
        vm.textTitle.set(textTitle)
        vm.textContent.set(textContent)
        vm.textButton1.set(positiveButton?.first)
        vm.textButton2.set(neutralButton?.first)
        vm.textButton3.set(negativeButton?.first)
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        this.textTitle = title
    }


    companion object {
        private val onClickDismiss = { true }
    }
}

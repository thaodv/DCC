package io.wexchain.android.dcc.vm

import android.databinding.ObservableField
import io.wexchain.digitalwallet.DigitalCurrency

class SelectedDigitalCurrencies {
    val set = ObservableField<Set<DigitalCurrency>>(emptySet())
}
package io.wexchain.android.dcc.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import io.wexchain.dcc.R

class SubmitPersonalInfoVm(application: Application)
    : AndroidViewModel(application) {

    val maritalStatus = SelectOptions().apply {
        val context = getApplication<Application>()
        optionTitle.set(context.getString(R.string.marital_status_title))
        options.set(context.resources.getTextArray(R.array.marital_status_options).map { toString() })
    }

    val education = SelectOptions().apply {
        val context = getApplication<Application>()
        optionTitle.set(context.getString(R.string.education_status_title))
        options.set(context.resources.getTextArray(R.array.education_status_options).map { toString() })
    }
    val occupationCategory = SelectOptions().apply {
        val context = getApplication<Application>()
        optionTitle.set(context.getString(R.string.occupation_category_title))
        options.set(context.resources.getTextArray(R.array.occupation_category_options).map { toString() })
    }
    val jobRank = SelectOptions().apply {
        val context = getApplication<Application>()
        optionTitle.set(context.getString(R.string.job_rank_title))
        options.set(context.resources.getTextArray(R.array.job_ranks).map { toString() })
    }

}
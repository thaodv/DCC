package io.wexchain.dccchainservice.domain

import io.wexchain.dccchainservice.type.TaskType
import io.wexchain.dccchainservice.type.StatusType
import io.wexchain.dccchainservice.type.TaskCode

/**
 *Created by liuyang on 2018/11/6.
 */

data class TaskList(
        val taskList: List<Task>,
        val category: TaskType,
        val name: String,
        val description: String
) {
    data class Task(
            val code: TaskCode,
            val name: String,
            val img: String,
            val type: String,
            val bonus: String,
            val link: String,
            val status: StatusType,
            val statusDescription: String
    )
}



package io.wexchain.dccchainservice.domain

/**
 *Created by liuyang on 2018/11/6.
 */

data class TaskList(
        val category: String,
        val name: String,
        val description: String,
        val taskList: List<Task>
) {
    data class Task(
            val code: String,
            val name: String,
            val img: String,
            val type: String,
            val bonus: String,
            val link: String,
            val status: String,
            val statusDescription: String
    )
}

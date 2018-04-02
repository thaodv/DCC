package io.wexchain.digitalwallet.api.domain.front

/**
 * Created by sisel on 2018/1/23.
 */

data class Paged<T>(
        val totalElements: Int,
        val totalPages: Int,
        val pageNo: Int,
        val pageSize: Int,
        val items: List<T>?
)
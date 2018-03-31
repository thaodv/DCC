package io.wexchain.wexchainservice.domain

import java.io.Serializable

/**
 * Created by sisel on 2018/2/9.
 */
data class IdOcrInfo(
    @JvmField val idCardInfo: IdCardInfo,
    @JvmField val side: Side
) {

    /**
     * (address=汉东省骡子县脊背乡石丰村赖家组20号, day=3, month=11, year=1984, nation=汉, sex=男, name=侯亮平, number=430423198411036252,"authority":"建瓯市公安局","timelimit":"20120625-20220625")
     */
    data class IdCardInfo(
        @JvmField val address: String?,
        @JvmField val day: String?,
        @JvmField val month: String?,
        @JvmField val year: String?,
        @JvmField val nation: String?,
        @JvmField val sex: String?,
        @JvmField val name: String?,
        @JvmField val number: String?,
        @JvmField val authority: String?,
        @JvmField val timelimit: String?
    ) : Serializable {
        companion object {
            fun from(
                first: IdOcrInfo,
                second: IdOcrInfo
            ): IdCardInfo {
                val front = when {
                    first.side == Side.front -> first.idCardInfo
                    second.side == Side.front -> second.idCardInfo
                    else -> null
                }
                val back = when {
                    first.side == Side.back -> first.idCardInfo
                    second.side == Side.back -> second.idCardInfo
                    else -> null
                }
                return IdCardInfo(
                    address = front?.address,
                    day = front?.day,
                    month = front?.month,
                    year = front?.year,
                    nation = front?.nation,
                    sex = front?.sex,
                    name = front?.name,
                    number = front?.number,
                    authority = back?.authority,
                    timelimit = back?.timelimit
                )
            }
        }
    }

    enum class Side {
        front,
        back
        ;
    }
}

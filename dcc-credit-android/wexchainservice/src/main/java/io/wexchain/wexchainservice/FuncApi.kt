package io.wexchain.wexchainservice

import io.reactivex.Single
import io.wexchain.wexchainservice.domain.CertOrder
import io.wexchain.wexchainservice.domain.IdOcrInfo
import io.wexchain.wexchainservice.domain.Result
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import java.io.File

/**
 * Created by sisel on 2018/2/9.
 */
interface FuncApi {

    @POST("cert/id/ocr")
    @Multipart
    fun ocr(@Part file: MultipartBody.Part): Single<Result<IdOcrInfo>>

    /**
     * @param file photo to upload, part named as "file"
     */
    @POST("cert/id/verify")
    @Multipart
    fun verify(
        @Part("orderId") orderId: Long,
        @Part("realName") realName: String,
        @Part("certNo") certNo: String,
        @Part file: MultipartBody.Part
    ): Single<Result<CertOrder>>

    companion object {
        fun uploadFilePart(file: File, mimeType: String): MultipartBody.Part {
            return MultipartBody.Part.createFormData(
                "file",
                file.name,
                RequestBody.create(
                    MediaType.parse(mimeType),
                    file
                )
            )
        }

        fun uploadFilePart(data: ByteArray, name: String, mimeType: String): MultipartBody.Part {
            return MultipartBody.Part.createFormData(
                "file",
                name,
                RequestBody.create(
                    MediaType.parse(mimeType),
                    data
                )
            )
        }
    }
}
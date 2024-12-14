package com.example.uas_synthesize.data.network

import android.annotation.SuppressLint
import com.cloudinary.Cloudinary
import com.cloudinary.Transformation
import com.cloudinary.utils.ObjectUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object CloudinaryManager {

    @SuppressLint("AuthLeak")
    private val cloudinary: Cloudinary = Cloudinary(
        "cloudinary://269294678898637:v-H4uqpNjoUeAYM7Cqh4_e9lgiA@djno49igv"
    )

        suspend fun uploadImage(file: File): UploadResponse {
            return withContext(Dispatchers.IO) {
                try {
                    // Upload file ke Cloudinary
                    val uploadResult = cloudinary.uploader().upload(
                        file.absolutePath,
                        ObjectUtils.emptyMap()
                    )

                    // Ambil URL gambar
                    val imageUrl = uploadResult["secure_url"] as? String

                    val transformedUrl = cloudinary.url()
                        .transformation(
                            Transformation<Transformation<*>>()
                                .width(200)
                                .height(200)
                                .crop("thumb").chain()
                                .radius("max").chain()
                                .fetchFormat("auto")
                        )
                        .generate(uploadResult["public_id"] as String)

                    val secureTransformedUrl = transformedUrl.replace("http://", "https://")

                    if (imageUrl != null) {
                        UploadResponse(true, secureTransformedUrl)
                    } else {
                        throw Exception("Gagal mendapatkan URL dari hasil unggahan.")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    UploadResponse(false, null, e.message)
                }
            }
        }


//      Data Class untuk Response
        data class UploadResponse(
            val isSuccessful: Boolean,
            val url: String?,
            val errorMessage: String? = null
        )
    }


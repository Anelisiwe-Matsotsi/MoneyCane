package com.example.prog7313_sem1_part2


import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class SupabaseStorageService {

    private val client = OkHttpClient()

    private val baseUrl = BuildConfig.SUPABASE_URL
    private val anonKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY
    private val bucketName = "images"

    fun uploadImage(
        fileBytes: ByteArray,
        filePath: String,
        mimeType: String = "image/jpeg"
    ): Result<String> {
        return try {
            val uploadUrl = "$baseUrl/storage/v1/object/$bucketName/$filePath"

            val requestBody = fileBytes.toRequestBody(mimeType.toMediaType())

            val request = Request.Builder()
                .url(uploadUrl)
                .addHeader("apikey", anonKey)
                .addHeader("Authorization", "Bearer $anonKey")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorBody = response.body?.string()
                    return Result.failure(
                        Exception("Upload failed: ${response.code} $errorBody")
                    )
                }
            }

            val publicUrl = "$baseUrl/storage/v1/object/public/$bucketName/$filePath"
            Result.success(publicUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
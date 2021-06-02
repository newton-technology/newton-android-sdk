package io.nwtn.newton_auth

import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

private const val TAG = "AuthHttpController"
private const val CONNECTION_TIMEOUT = 10
private const val READ_TIMEOUT = 50
private const val RETRY_DELAY = 2
private const val RETRY_MAX_COUNT = 5

class AuthHttpController {

    companion object {
        var instance = AuthHttpController()
    }

    class AuthException(override val message: String, val body: String): java.lang.Exception()

    private class RetryInterceptor(): Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            var response: Response? = null
            var responseOK = false
            var tryCount = 0
            while (!responseOK && tryCount < RETRY_MAX_COUNT) {
                try {
                    response = chain.proceed(request)
                    responseOK = response.isSuccessful || response.code in 400..499
                } catch (e: Exception) {
                    Log.e(TAG, e.message, e)
                    Log.d(TAG, "request is not successful, retry ($tryCount)")
                } finally {
                    try {
                        Thread.sleep(RETRY_DELAY.toLong())
                        tryCount++
                    } catch (e: InterruptedException) {
                        tryCount = RETRY_MAX_COUNT
                        Log.e(TAG, e.message, e)
                    }
                }
            }
            if (response == null) {
                throw IOException("network error")
            }
            return response
        }
    }

    fun post(
        url: String,
        parameters: Map<String, String?>,
        headers: Map<String, String?>?,
        callback: AuthHttpCallback
    ) {
        val data = JSONObject(parameters).toString()
        val mediaType = "application/json; charset=utf-8".toMediaType()
        var builder = Request.Builder()
            .url(url)
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .post(data.toRequestBody(mediaType))

        if (headers != null) {
            for ((key, value) in headers) {
                if (value != null) {
                    builder = builder.addHeader(key, value)
                }
            }
        }

        val request = builder.build()
        sendRequest(request, callback)
    }

    private fun sendRequest(request: Request, callback: AuthHttpCallback) {
        getClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, e.message, e)
                callback.onError(e, AuthError(AuthError.AuthErrorCode.unknownError))
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBody = response.body ?: throw AuthException("response fail", "")
                    val responseJson = responseBody.string()
                    if (!response.isSuccessful) {
                        throw AuthException("response fail", responseBody.string())
                    }
                    try {
                        callback.onSuccess(response.code, JSONObject(responseJson))
                    } catch (e: Exception) {
                        callback.onSuccess(response.code, null)
                    }
                } catch (e: AuthException) {
                    try {
                        callback.onError(e, AuthError(JSONObject(e.body)))
                    } catch (e: Exception) {
                        callback.onError(e,  AuthError(AuthError.AuthErrorCode.unknownError))
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "http request exception", e)
                    callback.onError(e, AuthError(AuthError.AuthErrorCode.unknownError))
                }
            }
        })
    }

    private fun getClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .addInterceptor(RetryInterceptor())
        return builder.build()
    }

}
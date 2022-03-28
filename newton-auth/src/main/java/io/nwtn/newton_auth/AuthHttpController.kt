package io.nwtn.newton_auth

import android.util.Log
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Interceptor
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.HttpUrl

private const val TAG = "AuthHttpController"
private const val CONNECTION_TIMEOUT = 10
private const val READ_TIMEOUT = 50
private const val RETRY_DELAY = 2
private const val RETRY_MAX_COUNT = 5

class AuthHttpController {

    companion object {
        var instance = AuthHttpController()
    }

    class AuthException(val code: Int, override val message: String, val body: String): java.lang.Exception()

    private class RetryInterceptor(): Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            var response: Response? = null
            var responseOK = false
            var tryCount = 0
            while (!responseOK && tryCount < RETRY_MAX_COUNT) {
                try {
                    response = chain.proceed(request)
                    responseOK = response.isSuccessful || response.code() in 400..500
                } catch (e: Exception) {
                    Log.e(TAG, "interceptor error ${e.message}", e)
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

    fun get(url: String,
            parameters: Map<String, String?>,
            headers: Map<String, String?>?,
            callback: AuthHttpCallback
    ) {
        val urlBuilder = HttpUrl.parse(url)?.newBuilder()

        if (urlBuilder == null) {
            callback.onError(java.lang.Exception("error"), AuthError(AuthError.AuthErrorCode.unknownError))
            return
        }
        for ((k, v) in parameters) {
            if (v != null) {
                urlBuilder.addQueryParameter(k, v)
            }
        }
        val requestUrl = urlBuilder.build()

        var builder = Request.Builder()
            .url(requestUrl)
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .get()

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

    fun post(
        url: String,
        parameters: Map<String, String?>,
        headers: Map<String, String?>?,
        callback: AuthHttpCallback
    ) {
        var formBody = FormBody.Builder()
        for ((k, v) in parameters) {
            if (v != null) {
                formBody = formBody.add(k, v)
            }
        }
        var builder = Request.Builder()
            .url(url)
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .post(formBody.build())

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
                Log.e(TAG, "error failure ${e.message}", e)
                callback.onError(e, AuthError(AuthError.AuthErrorCode.unknownError))
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBody = response.body() ?: throw AuthException(response.code(), "response fail", "")
                    val responseJson = responseBody.string()
                    if (!response.isSuccessful) {
                        throw AuthException(response.code(), "response fail", responseJson)
                    }
                    try {
                        callback.onSuccess(response.code(), JSONObject(responseJson), response.headers())
                    } catch (e: Exception) {
                        Log.e(TAG, "http request exception", e)
                        callback.onSuccess(response.code(), null, null)
                    }
                } catch (e: AuthException) {
                    Log.e(TAG, "auth exception ${e.code} ${e.message} body ${e.body}", e)
                    try {
                        if (e.code == 500) {
                            callback.onError(e,  AuthError(AuthError.AuthErrorCode.serverError))
                            return
                        }
                        callback.onError(e, AuthError(JSONObject(e.body)))
                    } catch (e: Exception) {
                        Log.e(TAG, "auth error parse exception ${e.message}", e)
                        callback.onError(e,  AuthError(AuthError.AuthErrorCode.unknownError))
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "http request exception ${e.message}", e)
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

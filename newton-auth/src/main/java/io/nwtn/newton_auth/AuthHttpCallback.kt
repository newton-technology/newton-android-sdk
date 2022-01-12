package io.nwtn.newton_auth

import okhttp3.Headers
import org.json.JSONObject

interface AuthHttpCallback {
    fun onSuccess(responseCode: Int, jsonObject: JSONObject?, headers: Headers?)
    fun onError(error: Exception, errorData: AuthError)
}

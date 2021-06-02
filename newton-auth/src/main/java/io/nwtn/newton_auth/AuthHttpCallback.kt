package io.nwtn.newton_auth

import org.json.JSONObject

interface AuthHttpCallback {
    fun onSuccess(responseCode: Int, jsonObject: JSONObject?)
    fun onError(error: Exception, errorData: AuthError)
}

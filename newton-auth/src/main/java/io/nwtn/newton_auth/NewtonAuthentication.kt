package io.nwtn.newton_auth

import org.json.JSONObject
import java.lang.Exception

class NewtonAuthentication constructor(
    private var url: String,
    private var clientId: String,
    private var realm: String,
    private var serviceRealm: String
) {

    fun sendPhoneCode(
        phoneNumber: String,
        callback: AuthResultCallback
    ) {
        val parameters = mapOf(
            "client_id" to clientId,
            "grant_type" to "password",
            "phone_number" to phoneNumber
        )
        return requestServiceToken(parameters, callback)
    }

    fun verifyPhone(
        code: String,
        serviceToken: String,
        callback: AuthResultCallback
    ) {
        return verifyCode(code, serviceToken, callback)
    }

    fun sendEmailCode(
            email: String?,
            serviceToken: String,
            callback: AuthResultCallback
    ) {
        val parameters = mutableMapOf(
                "client_id" to clientId,
                "grant_type" to "password"
        )
        if (email != null) {
            parameters.put("email", email)
        }
        return requestServiceToken(parameters, serviceToken, callback)
    }

    fun sendEmailCode(
        serviceToken: String,
        callback: AuthResultCallback
    ) {
        return sendEmailCode(null, serviceToken, callback)
    }

    fun verifyEmail(
        code: String,
        serviceToken: String,
        callback: AuthResultCallback
    ) {
        return verifyCode(code, serviceToken, callback)
    }

    fun login(
        serviceToken: String,
        callback: AuthResultCallback
    ) {
        return login(serviceToken, null, callback)
    }

    fun login(
        serviceToken: String,
        password: String?,
        callback: AuthResultCallback
    ) {
        val parameters = mutableMapOf(
            "client_id" to clientId,
            "grant_type" to "password"
        )
        if (password != null) {
            parameters["password"] = password
        }
        return requestMainToken(parameters, serviceToken, callback)
    }

    fun refreshToken(
        refreshToken: String,
        callback: AuthResultCallback
    ) {
        val parameters = mapOf<String, String>(
            "client_id" to clientId,
            "grant_type" to "refresh_token",
            "refresh_token" to refreshToken
        )
        return requestMainToken(parameters, null, callback)
    }

    fun requestPasswordReset(
        serviceToken: String,
        callback: AuthResultCallback
    ) {
        val parameters = mapOf<String, String>(
            "client_id" to clientId,
            "grant_type" to "password",
            "reset_password" to "true"
        )
        requestServiceToken(parameters, serviceToken, callback)
    }

    private fun verifyCode(
        code: String,
        serviceToken: String,
        callback: AuthResultCallback
    ) {
        val parameters = mapOf(
            "client_id" to clientId,
            "grant_type" to "password",
            "code" to code
        )
        return requestServiceToken(parameters, serviceToken, callback)
    }

    private fun requestServiceToken(
        parameters: Map<String, String>,
        callback: AuthResultCallback
    ) {
        return requestAccessToken(serviceRealm, parameters, null, callback)
    }

    private fun requestServiceToken(
        parameters: Map<String, String>,
        authorizationToken: String?,
        callback: AuthResultCallback
    ) {
        return requestAccessToken(serviceRealm, parameters, authorizationToken, callback)
    }

    private fun requestMainToken(
        parameters: Map<String, String>,
        authorizationToken: String?,
        callback: AuthResultCallback
    ) {
        return requestAccessToken(realm, parameters, authorizationToken, callback)
    }

    private fun requestAccessToken(
        currentRealm: String,
        parameters: Map<String, String>,
        authorizationToken: String?,
        callback: AuthResultCallback
    ) {
        val requestUrl = "${url}/auth/realms/${currentRealm}/protocol/openid-connect/token"
        val httpController = AuthHttpController.instance

        val headers: Map<String, String>? = if (authorizationToken != null) mapOf("Authorization" to "Bearer $authorizationToken") else null

        httpController.post(
            requestUrl,
            parameters,
            headers,
            object : AuthHttpCallback {
                override fun onSuccess(responseCode: Int, jsonObject: JSONObject?) {
                    try {
                        val result = AuthResult(jsonObject!!)
                        val flowState = JWTUtils.decodeAuthFlowState(result.accessToken)
                        callback.onSuccess(result, flowState)
                    } catch (e: Exception) {
                        callback.onError(AuthError(AuthError.AuthErrorCode.unknownError))
                    }
                }
                override fun onError(error: Exception, errorData: AuthError) {
                    if (errorData.error == AuthError.AuthErrorCode.invalidGrant &&
                            authorizationToken != null &&
                            JWTUtils.tokenExpired(authorizationToken)) {
                        callback.onError(AuthError(AuthError.AuthErrorCode.tokenExpired, "Authorization token expired"))
                        return
                    }
                    callback.onError(errorData)
                }
            }
        )
    }
}

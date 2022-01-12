package io.nwtn.newton_auth

import okhttp3.Headers
import org.json.JSONObject
import java.lang.Exception

/**
 * Main authentication class
 *
 * @param[url] Newton auth server url
 * @param[clientId] Newton auth server client id used
 * @param[realm] the name of newton auth server realm
 * @param[serviceRealm] the name of newton auth server service realm
 * @constructor created new NewtonAuthentication instance
 */
class NewtonAuthentication constructor(
    private var url: String,
    private var clientId: String,
    private var realm: String,
    private var serviceRealm: String
) {
    /**
     * requests a phone code to start authentication flow
     *
     * @param[phoneNumber] user phone number
     * @param[callback] authentication result callback
     */
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

    /**
     * verifies phone with code
     *
     * @param[code] code received by user after successful sendPhoneCode request
     * @param[serviceToken] access token received on previous flow step
     * @param[callback] authentication result callback
     */
    fun verifyPhone(
        code: String,
        serviceToken: String,
        callback: AuthResultCallback
    ) {
        return verifyCode(code, serviceToken, callback)
    }

    /**
     * requests an email code to continue authentication flow
     *
     * @param[email] user email (optional)
     * @param[serviceToken] access token received on previous flow step
     * @param[callback] authentication result callback
     */
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

    /**
     * requests an email code to continue authentication flow for user that already has an email
     *
     * @param[serviceToken] access token received on previous flow step
     * @param[callback] authentication result callback
     */
    fun sendEmailCode(
        serviceToken: String,
        callback: AuthResultCallback
    ) {
        return sendEmailCode(null, serviceToken, callback)
    }

    /**
     * verifies email with code
     *
     * @param[code] email code received by user after successful sendEmailCode request
     * @param[serviceToken] access token received on previous flow step
     * @param[callback] authentication result callback
     */
    fun verifyEmail(
        code: String,
        serviceToken: String,
        callback: AuthResultCallback
    ) {
        return verifyCode(code, serviceToken, callback)
    }

    /**
     * signs in user with service token (for short login flow only)
     *
     * @param[serviceToken] access token received on previous flow step
     * @param[callback] authentication result callback
     */
    fun login(
        serviceToken: String,
        callback: AuthResultCallback
    ) {
        return login(serviceToken, null, callback)
    }

    /**
     * signs in user with password and service token (for short login flow only)
     *
     * @param[password] users existing (for normal login flow) or new (for normal with email login flow) password
     * @param[serviceToken] access token received on previous flow step
     * @param[callback] authentication result callback
     */
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

    /**
     * requests new access and refresh token with current refresh token
     *
     * @param[refreshToken] current refresh token
     * @param[callback] authentication result callback
     */
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

    /**
     * starts password reset with normal with email login flow
     *
     * @param[serviceToken] access token that is valid to start password reset
     * @param[callback] authentication result callback
     */
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
        return requestAccessToken(serviceRealm, parameters, null, false, callback)
    }

    private fun requestServiceToken(
        parameters: Map<String, String>,
        authorizationToken: String?,
        callback: AuthResultCallback
    ) {
        return requestAccessToken(serviceRealm, parameters, authorizationToken, false, callback)
    }

    private fun requestMainToken(
        parameters: Map<String, String>,
        authorizationToken: String?,
        callback: AuthResultCallback
    ) {
        return requestAccessToken(realm, parameters, authorizationToken, true, callback)
    }

    private fun requestAccessToken(
        currentRealm: String,
        parameters: Map<String, String>,
        authorizationToken: String?,
        updateMainTokenData: Boolean,
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
                override fun onSuccess(responseCode: Int, jsonObject: JSONObject?, headers: Headers?) {
                    try {
                        val result = AuthResult(jsonObject!!)
                        val flowState = JWTUtils.decodeAuthFlowState(result.accessToken, headers)
                        if (updateMainTokenData) {
                            AccessTokenData.updateTokenData(result.accessToken, headers)
                        }
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

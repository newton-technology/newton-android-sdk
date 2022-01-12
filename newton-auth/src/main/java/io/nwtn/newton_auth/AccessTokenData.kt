package io.nwtn.newton_auth

import okhttp3.Headers
import org.json.JSONException
import org.json.JSONObject

object AccessTokenData {

    /**
     * access token
     */
    var accessToken: String? = null
        private set

    /**
     * access token local expiration time in millis
     */
    var localExpirationTime: Double? = null
        private set

    fun updateTokenData(jwtToken: String?, headers: Headers?) {
        if (jwtToken == null || headers == null) {
            accessToken = null
            localExpirationTime = null
            return
        }
        accessToken = jwtToken
        updateExpirationData(jwtToken, headers)
    }

    private fun updateExpirationData(token: String, headers: Headers) {
        val expTimeFromToken = getAccessTokenExpirationTime(token) ?: return
        localExpirationTime = TimestampUtils.getTimestampInLocalTime(expTimeFromToken, headers)
    }

    private fun getAccessTokenExpirationTime(token: String): Double? {
        val payload: JSONObject? = JWTUtils.decode(token)
        try {
            if (payload != null && payload.has("exp")) {
                return payload.getDouble("exp") * 1000
            }
        } catch (e: JSONException) {
            //
        }
        return null
    }


}
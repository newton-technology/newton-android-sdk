package io.nwtn.newton_auth

import okhttp3.Headers
import org.json.JSONException
import org.json.JSONObject
import java.util.*

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
        if (jwtToken == null) {
            return
        }
        accessToken = jwtToken
        if (headers != null) {
            updateExpirationData(jwtToken, headers)
        }
    }

    private fun updateExpirationData(token: String, headers: Headers) {
        val headerDate = headers.getDate("Date") ?: return
        val now = Date()
        val delta = now.time - headerDate.time
        val expTimeFromToken = getAccessTokenExpirationTime(token) ?: return
        localExpirationTime = expTimeFromToken + delta
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
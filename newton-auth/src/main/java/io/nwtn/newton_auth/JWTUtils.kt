package io.nwtn.newton_auth

import android.util.Base64
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

object JWTUtils {
    fun decode(jwt: String): JSONObject? {
        val segments = jwt.split("\\.".toRegex()).toTypedArray()
        return decodeJWTPart(segments[1])
    }

    private fun decodeJWTPart(value: String): JSONObject? {
        val decoded = String(Base64.decode(value, Base64.DEFAULT))
        var json: JSONObject? = null
        try {
            json = JSONObject(decoded)
        } catch (e: JSONException) {
            //
        }
        return json
    }

    fun decodeAuthFlowState(jwt: String): AuthFlowState? {
        return try {
            val data = decode(jwt)
            AuthFlowState(data!!)
        } catch (e: Exception) {
            null
        }

    }
}

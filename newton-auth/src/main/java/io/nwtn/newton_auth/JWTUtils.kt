package io.nwtn.newton_auth

import android.util.Base64
import okhttp3.Headers
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.util.*

object JWTUtils {
    fun decode(jwt: String): JSONObject? {
        val segments = jwt.split("\\.".toRegex()).toTypedArray()
        if (segments.size != 3) {
            // TODO: log error
            return null
        }
        return decodeJWTPart(segments[1])
    }

    fun decodeAuthFlowState(jwt: String, responseDate: Date?): AuthFlowState? {
        return try {
            val data = decode(jwt)
            AuthFlowState(data!!, responseDate)
        } catch (e: Exception) {
            null
        }
    }

    fun decodeAuthFlowState(jwt: String): AuthFlowState? {
        return decodeAuthFlowState(jwt, null)
    }

    fun tokenExpired(jwt: String): Boolean {
        val decoded = decode(jwt) ?: return false
        return try {
            val tokenDate = Date(decoded.getLong("exp")  * 1000)
            Date().after(tokenDate)
        } catch (e: Exception) {
            false
        }
    }

    private fun decodeJWTPart(value: String): JSONObject? {
        val decoded = String(Base64.decode(value, Base64.DEFAULT))
        return try {
            JSONObject(decoded)
        } catch (e: JSONException) {
            null
        }
    }
}

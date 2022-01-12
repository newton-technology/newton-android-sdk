package io.nwtn.newton_auth

import okhttp3.Headers
import org.json.JSONObject
import java.lang.Exception
import java.util.*

object TimestampUtils {

    private const val HEADER_KEY_DATE = "Date";

    fun getExpirationTimeInMillis(
            jsonObject: JSONObject,
            timestampKey: String,
            headers: Headers?
    ): Double? {
        if (!jsonObject.has(timestampKey)) {
            return null
        }
        return try {
            getExpirationTimeInMillis(jsonObject.getDouble(timestampKey), headers)
        } catch (e: Exception) {
            null
        }
    }

    fun getExpirationTimeInSeconds(
        jsonObject: JSONObject,
        timestampKey: String,
        headers: Headers?
    ): Double? {
        val ts = getExpirationTimeInMillis(jsonObject, timestampKey, headers)
        return getTimeInSeconds(ts)
    }

    fun getExpirationTimeInMillis(timestamp: Double?, headers: Headers?): Double? {
        if (timestamp == null) {
            return null
        }
        val timestampInMillis = timestamp * 1000
        if (headers == null) {
            return timestampInMillis
        }
        return getTimestampInLocalTime(timestampInMillis, headers)
    }

    fun getExpirationTimeInSeconds(timestamp: Double, headers: Headers?): Double? {
        val ts = getExpirationTimeInMillis(timestamp, headers)
        return getTimeInSeconds(ts)
    }

    fun getTimestampInLocalTime(timestamp: Double, headers: Headers): Double {
        val headerDate = headers.getDate(HEADER_KEY_DATE) ?: return timestamp
        val now = Date()
        val delta = now.time - headerDate.time
        return timestamp + delta
    }

    private fun getTimeInSeconds(timestamp: Double?): Double? {
        if (timestamp != null) {
            return timestamp / 1000
        }
        return null
    }
}
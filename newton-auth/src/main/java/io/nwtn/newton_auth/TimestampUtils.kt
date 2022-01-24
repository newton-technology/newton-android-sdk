package io.nwtn.newton_auth

import org.json.JSONObject
import java.lang.Exception
import java.util.*

object TimestampUtils {

    fun getExpirationTimeInMillis(
            jsonObject: JSONObject?,
            timestampKey: String,
            responseDate: Date?
    ): Double? {
        if (jsonObject == null) {
            return null
        }
        if (!jsonObject.has(timestampKey)) {
            return null
        }
        return try {
            getExpirationTimeInMillis(jsonObject.getDouble(timestampKey), responseDate)
        } catch (e: Exception) {
            null
        }
    }

    fun getExpirationTimeInSeconds(
        jsonObject: JSONObject?,
        timestampKey: String,
        responseDate: Date?
    ): Double? {
        val ts = getExpirationTimeInMillis(jsonObject, timestampKey, responseDate)
        return getTimeInSeconds(ts)
    }

    fun getExpirationTimeInMillis(timestamp: Double?, responseDate: Date?): Double? {
        if (timestamp == null) {
            return null
        }
        val timestampInMillis = timestamp * 1000
        if (responseDate == null) {
            return timestampInMillis
        }
        return getTimestampInLocalTime(timestampInMillis, responseDate)
    }

    fun getExpirationTimeInSeconds(timestamp: Double, responseDate: Date?): Double? {
        val ts = getExpirationTimeInMillis(timestamp, responseDate)
        return getTimeInSeconds(ts)
    }

    fun getTimestampInLocalTime(timestamp: Double, responseDate: Date?): Double {
        if (responseDate == null) {
            return timestamp
        }
        val now = Date()
        val delta = now.time - responseDate.time
        return timestamp + delta
    }

    private fun getTimeInSeconds(timestamp: Double?): Double? {
        if (timestamp != null) {
            return timestamp / 1000
        }
        return null
    }
}

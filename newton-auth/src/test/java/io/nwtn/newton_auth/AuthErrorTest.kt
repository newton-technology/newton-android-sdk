package io.nwtn.newton_auth

import org.json.JSONObject
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AuthErrorTest {

    @Test
    fun `should parse correctly and have unsupported grant type code`() {
        val jsonObject = JSONObject(UNSUPPORTED_GRANT_TYPE_ERROR)
        val authError = AuthError(jsonObject)
        assertEquals(authError.error, AuthError.AuthErrorCode.unsupportedGrantType)
    }

    @Test
    fun `unknown error code should result in unknown error`() {
        val jsonObject = JSONObject().put("error", "completely unknown invalid error")
        val authError = AuthError(jsonObject)
        assertEquals(authError.error, AuthError.AuthErrorCode.unknownError)
    }

    @Test
    fun `parsed error has otp fields`() {
        val jsonObject = JSONObject(ERROR_WITH_OTP_FIELDS)
        val authError = AuthError(jsonObject)
        assertEquals(5, authError.otpChecksLeft)
        assertEquals(10, authError.otpSendsLeft)
    }

}

private const val UNSUPPORTED_GRANT_TYPE_ERROR = """
    {
        "error": "unsupported_grant_type"
    }
"""

private const val ERROR_WITH_OTP_FIELDS = """
    {
        "error": "invalid_grant",
        "otp_checks_left": 5,
        "otp_sends_left": 10
    }
"""

package io.nwtn.newton_auth

import org.json.JSONObject
import org.junit.Test
import org.junit.Assert.*

public class AuthErrorTest {

    @Test
    fun decode_shouldBeValid() {
        val authError = AuthError(UNSUPPORTED_GRANT_TYPE_ERROR)
        assertEquals(authError.error, AuthError.AuthErrorCode.unsupportedGrantType)
    }
}

private val UNSUPPORTED_GRANT_TYPE_ERROR = JSONObject(
        """
            {
                "error": "unsupported_grant_type"
            }
        """
)
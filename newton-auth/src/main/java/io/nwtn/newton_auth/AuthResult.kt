package io.nwtn.newton_auth

import org.json.JSONObject

/**
 * authentication result received after successful authentication
 *
 * @param[jsonObject] object with auth result data
 */
class AuthResult(jsonObject: JSONObject) {
    /**
     * access token (JSON web token)
     */
    val accessToken: String = jsonObject.getString("access_token")

    /**
     * access token expiration time
     */
    val accessTokenExpiresIn: Int = jsonObject.getInt("expires_in")

    /**
     * refresh token (JSON web token)
     */
    val refreshToken: String = jsonObject.getString("refresh_token")

    /**
     * refresh token expiration time
     */
    val refreshTokenExpiresIn: Int = jsonObject.getInt("refresh_expires_in")

    /**
     * access token type (e.g. "Bearer")
     */
    val tokenType: String = jsonObject.getString("token_type")
}

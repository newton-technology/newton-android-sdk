package io.nwtn.newton_auth

import org.json.JSONObject

class AuthResult(jsonObject: JSONObject) {
    val accessToken: String = jsonObject.getString("access_token")
    val accessTokenExpiresIn: Int = jsonObject.getInt("expires_in")
    val refreshToken: String = jsonObject.getString("refresh_token")
    val refreshTokenExpiresIn: Int = jsonObject.getInt("refresh_expires_in")
    val tokenType: String = jsonObject.getString("token_type")
}

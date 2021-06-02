package io.nwtn.newton_auth

import org.json.JSONObject

class AuthError(val error: AuthErrorCode, val errorDescription: String?) {

    enum class AuthErrorCode(val text: String) {
        unsupportedGrantType("unsupported_grant_type"),
        invalidClient("invalid_client"),
        invalidRequest("invalid_request"),
        notAllowed("not_allowed"),
        invalidGrant("invalid_grant"),
        passwordMissing("password_missing"),
        phoneMissing("phone_missing"),
        invalidPhone("invalid_phone"),
        codeMissing("code_missing"),
        usernameMissing("username_missing"),
        usernameInUse("username_in_use"),
        emailInUse("email_in_use"),
        realmDisabled("realm_disabled"),
        unknownError("unknown_error");

        companion object {
            fun fromString(value: String): AuthErrorCode {
                for (code in values()) {
                    if (code.text == value) {
                        return code
                    }
                }
                return unknownError
            }
        }
    }

    constructor(error: AuthErrorCode) : this(error, null)

    constructor(jsonObject: JSONObject) : this(
        AuthErrorCode.fromString(jsonObject.getString("error")),
        jsonObject.getString("error_description")
    )
}
package io.nwtn.newton_auth

import org.json.JSONObject

/**
 * authorization error
 *
 * @param[error] authentication error code
 * @param[errorDescription] error description text
 * @constructor returns new authentication error
 */
class AuthError(val error: AuthErrorCode, val errorDescription: String?) {

    /**
     * authorization error code
     */
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
        codeAlreadySubmitted("code_already_submitted"),
        tokenExpired("token_expired"),
        unknownError("unknown_error"),
        serverError("server_error");

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

    constructor (jsonObject: JSONObject) : this(
        AuthErrorCode.fromString(jsonObject.getString("error")),
        if (jsonObject.has("error_description"))
            jsonObject.getString("error_description")
        else null
    )
}

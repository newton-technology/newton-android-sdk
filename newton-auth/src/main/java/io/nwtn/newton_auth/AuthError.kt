package io.nwtn.newton_auth

import org.json.JSONObject

/**
 * authorization error
 *
 * @param[error] authentication error code
 * @param[errorDescription] error description text
 * @constructor returns new authentication error
 */
class AuthError(
    val error: AuthErrorCode,
    val errorDescription: String?,
    val otpChecksLeft: Int?,
    val otpSendsLeft: Int?
) {

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
        serverError("server_error"),
        attemptsOtpCheckExceeded("attempts_otp_check_exceeded"),
        /// Password is blacklisted
        invalidPasswordBlacklisted("invalid_password_blacklisted"),
        /// Password has too few digits
        invalidPasswordMinDigits("invalid_password_min_digits"),
        /// Password was recently used
        invalidPasswordHistory("invalid_password_history"),
        /// Password if too short
        invalid_password_min_length("invalid_password_min_length"),
        /// Password has too few lower case chars
        invalidPasswordMinLowerCaseChars("invalid_password_min_lower_case_chars"),
        /// Password is too long
        invalidPasswordMaxLength("invalid_password_max_length"),
        /// Password equals email
        invalidPasswordNotEmail("invalid_password_not_email"),
        /// Password equals username
        invalidPasswordNotUsername("invalid_password_not_username"),
        /// Invalid password regex pattern
        invalidPasswordRegexPattern("invalid_password_regex_pattern"),
        /// Password has too few special symbols
        invalidPasswordMinSpecialChars("invalid_password_min_special_chars"),
        /// Password has too few uppercase symbols
        invalidPasswordMinUpperCaseChars("invalid_password_min_upper_case_chars");

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

    constructor(error: AuthErrorCode, errorDescription: String?) : this(error, errorDescription, null, null)

    constructor(error: AuthErrorCode) : this(error, null, null, null)

    constructor (jsonObject: JSONObject) : this(
        AuthErrorCode.fromString(jsonObject.getString("error")),
        if (jsonObject.has("error_description"))
            jsonObject.getString("error_description")
        else null,
        if (jsonObject.has("otp_checks_left"))
            jsonObject.getInt("otp_checks_left")
        else null,
        if (jsonObject.has("otp_sends_left"))
            jsonObject.getInt("otp_sends_left")
        else null
    )
}

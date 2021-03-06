package io.nwtn.newton_auth

import org.json.JSONObject
import java.util.*

/**
 * Newton authentication flow state
 *
 * @param[jsonObject] object with auth flow state data
 */
class AuthFlowState(jsonObject: JSONObject, responseDate: Date?) {

    constructor(jsonObject: JSONObject) : this(jsonObject, null)

    /**
     * login flow
     */
    enum class LoginFlow(val text: String) {
        shortFlow("SHORT"),
        normal("NORMAL"),
        normalWithEmail("NORMAL_WITH_EMAIL");

        companion object {
            fun fromString(value: String): LoginFlow {
                for (code in values()) {
                    if (code.text == value) {
                        return code
                    }
                }
                throw IllegalArgumentException("No enum constant LoginFlow for $value")
            }
        }

    }

    /**
     * login step
     */
    enum class LoginStep(val text: String) {
        sendPhoneCode("SEND_PHONE_CODE"),
        verifyPhoneCode("VERIFY_PHONE_CODE"),
        sendEmailCode("SEND_EMAIL_CODE"),
        verifyEmailCode("VERIFY_EMAIL_CODE"),
        getMainToken("GET_MAIN_TOKEN");

        companion object {
            fun fromString(value: String): LoginStep {
                for (code in values()) {
                    if (code.text == value) {
                        return code
                    }
                }
                throw IllegalArgumentException("No enum constant LoginStep for $value")
            }
        }
    }

    /**
     * login flow
     */
    val loginFlow: LoginFlow = LoginFlow.fromString(jsonObject.getString("login_flow"))

    /**
     * current login step
     */
    val loginStep: LoginStep = LoginStep.fromString(jsonObject.getString("login_step"))

    /**
     * masked user email
     */
    val maskedEmail: String? = if (jsonObject.has("masked_email")) jsonObject.getString("masked_email") else null

    /**
     * user phone number
     */
    val phoneNumber: String? = if (jsonObject.has("phone_number")) jsonObject.getString("phone_number") else null

    /**
     * timestamp of code expiration time (for verifyPhoneCode and verifyEmailCode login steps)
     */
    val codeExpiresTimestamp: Int? = TimestampUtils.getExpirationTimeInSeconds(
            jsonObject,
            "code_expires_timestamp",
            responseDate
    )?.toInt()

    /**
     * timestamp of when code can be resubmitted (for verifyPhoneCode and verifyEmailCode login steps)
     */
    val codeCanBeResubmittedTimestamp: Int? = TimestampUtils.getExpirationTimeInSeconds(
            jsonObject,
            "code_can_be_resubmitted_timestamp",
            responseDate
    )?.toInt()

    /**
     * OTP checks left in current flow
     */
    val otpChecksLeft: Int? = if (jsonObject.has("otp_checks_left")) jsonObject.getInt("otp_checks_left") else null

    /**
     * OTP checks left in current flow
     */
    val otpSendsLeft: Int? = if (jsonObject.has("otp_sends_left")) jsonObject.getInt("otp_sends_left") else null
}

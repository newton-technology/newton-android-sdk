package io.nwtn.newton_auth

import org.json.JSONException
import org.json.JSONObject

/**
 * Newton authentication flow state
 *
 * @param[jsonObject] object with auth flow state data
 */
class AuthFlowState(jsonObject: JSONObject) {

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
    val codeExpiresTimestamp: Int? = if (jsonObject.has("code_expires_timestamp")) jsonObject.getInt("code_expires_timestamp") else null

    /**
     * timestamp of when code can be resubmitted (for verifyPhoneCode and verifyEmailCode login steps)
     */
    val codeCanBeResubmittedTimestamp: Int? = if (jsonObject.has("code_can_be_resubmitted_timestamp")) jsonObject.getInt("code_can_be_resubmitted_timestamp") else null
}

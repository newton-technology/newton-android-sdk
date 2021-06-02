package io.nwtn.newton_auth

import org.json.JSONException
import org.json.JSONObject

class AuthFlowState(jsonObject: JSONObject) {
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

    val loginFlow: LoginFlow = LoginFlow.fromString(jsonObject.getString("login_flow"))
    val loginStep: LoginStep = LoginStep.fromString(jsonObject.getString("login_step"))
    val maskedEmail: String? = if (jsonObject.has("masked_email")) jsonObject.getString("masked_email") else null
    val phoneNumber: String? = if (jsonObject.has("phone_number")) jsonObject.getString("phone_number") else null
}

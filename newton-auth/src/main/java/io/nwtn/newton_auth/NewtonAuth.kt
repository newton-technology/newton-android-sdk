package io.nwtn.newton_auth

import org.json.JSONObject

fun authentication(url: String, clientId: String, realm: String, serviceRealm: String): NewtonAuthentication {
    return NewtonAuthentication(url, clientId, realm, serviceRealm)
}

fun decodeJWT(jwt: String): JSONObject? {
    return JWTUtils.decode(jwt)
}

fun decodeAuthFlowState(jwt: String): AuthFlowState? {
    return JWTUtils.decodeAuthFlowState(jwt)
}

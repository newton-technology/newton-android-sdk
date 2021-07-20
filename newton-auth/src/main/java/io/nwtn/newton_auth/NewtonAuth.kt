package io.nwtn.newton_auth

import org.json.JSONObject

/**
 * creates Newton authentication instance
 *
 * @param[url] Newton auth server url
 * @param[clientId] Newton auth server client id used
 * @param[realm] the name of newton auth server realm
 * @param[serviceRealm] the name of newton auth server service realm
 * @return Newton authentication instance
 */
fun authentication(url: String, clientId: String, realm: String, serviceRealm: String): NewtonAuthentication {
    return NewtonAuthentication(url, clientId, realm, serviceRealm)
}

/**
 * decodes JSON web token string into JSONObject
 *
 * @param[jwt] JSON web token
 * @return jwt parsed into JSONObject
 */
fun decodeJWT(jwt: String): JSONObject? {
    return JWTUtils.decode(jwt)
}

/**
 * decodes authentication flow state from JSON web token
 *
 * @param[jwt] JSON web token
 * @return decoded authentication flow state
 */
fun decodeAuthFlowState(jwt: String): AuthFlowState? {
    return JWTUtils.decodeAuthFlowState(jwt)
}

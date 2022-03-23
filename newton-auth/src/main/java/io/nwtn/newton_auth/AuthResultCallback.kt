package io.nwtn.newton_auth

interface AuthResultCallback {
    fun onSuccess(authResult: AuthResult, authFlowState: AuthFlowState?)
    fun onError(error: AuthError)
}

interface AuthSimpleCallback {
    fun onSuccess()
    fun onError(error: AuthError?)
}

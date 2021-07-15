# Newton Android SDK

Android SDK to work with Newton services

## Table of Contents

- [Newton Auth](#newton-auth)
- [Author](#author)

## Newton Auth

SDK to integrate login with Newton authentication service based on Keycloak

#### Getting started

1. import newton authentication method

```kotlin
import io.nwtn.newton_auth.authentication
```

2. request phone code to start authentication flow
```kotlin
val newtonAuth = authentication(
    NEWTON_AUTH_URL, // Newton auth server url
    NEWTON_AUTH_CLIENT_ID, // Newton auth client id
    "main", // main realm name
    "service" // service realm name
)

newtonAuth.sendPhoneCode(
    PHONE_NUMBER, // user phone number
    object: AuthResultCallback { // result callback
        override fun onError(error: AuthError) {
            Log.i("NEWTON_AUTH", error.error.text)
        }
        override fun onSuccess(authResult: AuthResult, authFlowState: AuthFlowState?) {
            // get service access token here
            Log.i("NEWTON_AUTH", authResult.accessToken)    
        }
    }
)
```

3. verify phone code with service token from previous step

```kotlin
newtonAuth.verifyPhone(
    CODE, // phone received by user
    SERVICE_TOKEN, // service token received from previous step
    object: AuthResultCallback { // result callback
        override fun onError(error: AuthError) {}
        override fun onSuccess(authResult: AuthResult, authFlowState: AuthFlowState?) {
            // service access token here
            Log.i("NEWTON_AUTH", authResult.accessToken)
        }
    }
)
```

4. sign in with service token from previous step and get access token and refresh token
```kotlin
newtonAuth.login(
    SERVICE_TOKEN, // service token received from previous step
    object: AuthResultCallback { // result callback
        override fun onError(error: AuthError) {}
        override fun onSuccess(authResult: AuthResult, authFlowState: AuthFlowState?) {
            // main access token and refresh token here
            Log.i("NEWTON_AUTH", authResult.accessToken)
            Log.i("NEWTON_AUTH", authResult.refreshToken)
        }
    }
)
```

or if user signs in with password

```kotlin
newtonAuth.login(
    SERVICE_TOKEN, // service token received from previous step
    PASSWORD, // user password
    object: AuthResultCallback { // result callback
        override fun onError(error: AuthError) {}
        override fun onSuccess(authResult: AuthResult, authFlowState: AuthFlowState?) {
            // main access token and refresh token here
            Log.i("NEWTON_AUTH", authResult.accessToken)
            Log.i("NEWTON_AUTH", authResult.refreshToken)
        }
    }
)
```

5. get new access token with refresh token
```kotlin
newtonAuth.refreshToken(
    REFRESH_TOKEN,
    object: AuthResultCallback {
        override fun onError(error: AuthError) {}
        override fun onSuccess(authResult: AuthResult, authFlowState: AuthFlowState?) {
            // main access token and refresh token here
            Log.i("NEWTON_AUTH", authResult.accessToken)
            Log.i("NEWTON_AUTH", authResult.refreshToken)
        }
    }
)
```

## Author

[Newton](https://nwtn.io/)

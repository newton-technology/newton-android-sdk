# Newton Android SDK

Android SDK to work with Newton services

## Requirements

TODO

## Newton Auth

SDK to integrate login with Newton authentication service based on Keycloak

#### Installation

TODO

#### Getting started

1. import newton authentication method

```kotlin
import io.nwtn.newton_auth.authentication
```

2. request phone code to start authentication flow
```kotlin
val newtonAuth = authentication(
    NEWTON_AUTH_URL,
    NEWTON_AUTH_CLIENT_ID, 
    "main", 
    "service"
)

newtonAuth.sendPhoneCode(
    PHONE_NUMBER,
    object: AuthResultCallback {
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

2. verify phone code with service token from previous step

```kotlin
newtonAuth.verifyPhone(
    CODE,
    SERVICE_TOKEN,
    object: AuthResultCallback {
        override fun onError(error: AuthError) {}
        override fun onSuccess(authResult: AuthResult, authFlowState: AuthFlowState?) {
            // service access token here
            Log.i("NEWTON_AUTH", authResult.accessToken)
        }
    }
)
```

3. sign in with service token from previous step and get access token and refresh token
```kotlin
newtonAuth.login(
    SERVICE_TOKEN,
    object: AuthResultCallback {
        override fun onError(error: AuthError) {}
        override fun onSuccess(authResult: AuthResult, authFlowState: AuthFlowState?) {
            // main access token here
            Log.i("NEWTON_AUTH", authResult.accessToken)
        }
    }
)
```

4. get new access token with refresh token
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

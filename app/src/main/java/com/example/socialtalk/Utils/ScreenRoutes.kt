package com.example.socialtalk.Utils

sealed class ScreenRoutes(val route: String) {
    object numberInput : ScreenRoutes("NI")
    object codeVerify : ScreenRoutes("code")
    object Main : ScreenRoutes("MCS")
    object newUser : ScreenRoutes("newUser")
    object eachChat : ScreenRoutes("chat")
    object profile : ScreenRoutes("profile")
}

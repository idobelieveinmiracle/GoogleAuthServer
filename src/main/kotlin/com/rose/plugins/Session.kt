package com.rose.plugins

import com.rose.domain.model.UserSession
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import java.io.File

fun Application.configureSession() {
    install(Sessions) {
        val secretEncryptedKey = hex("00112233445566778899aabbccddeeff")
        val secretAuthKey = hex("6819b57a326945c1968f45236589")
        cookie<UserSession>(
            name = "USER_SESSION",
            storage = directorySessionStorage(File(".sessions"))
        ) {
            transform(SessionTransportTransformerEncrypt(secretEncryptedKey, secretAuthKey))
        }
    }
}

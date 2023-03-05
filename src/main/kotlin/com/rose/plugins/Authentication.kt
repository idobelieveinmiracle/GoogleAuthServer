package com.rose.plugins

import com.rose.domain.model.Endpoint
import com.rose.domain.model.UserSession
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*

fun Application.configureAuth() {
    install(Authentication) {
        session<UserSession>(name = "auth-session") {
            validate { session -> session }
            challenge {
                println("SESSION EXPIRED")
                call.respondRedirect(Endpoint.Unauthorized.path)
            }
        }
    }
}
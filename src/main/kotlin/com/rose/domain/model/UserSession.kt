package com.rose.domain.model

import io.ktor.server.auth.*

data class UserSession(
    val id: String,
    val name: String
): Principal